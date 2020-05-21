package ie.gmit.sw;

import java.io.File;
import java.util.Arrays;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.buffer.MemoryDataLoader;
import org.encog.ml.data.buffer.codec.CSVDataCODEC;
import org.encog.ml.data.buffer.codec.DataSetCODEC;
import org.encog.ml.data.folded.FoldedDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.cross.CrossValidationKFold;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.EncogUtility;

/*
 * Neural Network
 * 
 * @author: Morgan Reilly
 * 
 * This class handles the NN generation and testing
 * It also handles the prediction from user file
 * The neural network is a 3 layer model
 * The first layer is the input layer and is dependant on the vector hashing from the vector processor
 * The second layer is a hidden layer, with a sigmoidal activation and a dropout layer
 * The third layer is the output layer, with a softmax activation to map output
 */
public class NeuralNetwork {
	public Language[] languages;
	private final String DATA_FILE = "data.csv";
	private BasicNetwork basicNetwork;
	private CrossValidationKFold crossValidationKFold;
	private DataSetCODEC dataSetCodec;
	private FoldedDataSet foldedDataSet;
	private MemoryDataLoader mlDataLoader;
	private MLDataSet mlDataSet;
	private ResilientPropagation trainer;
	private int inputNodes, hiddenNodes, outputNodes;
	private double errorRate;

	public NeuralNetwork() {
	}

	public NeuralNetwork(int input, int output, double errorRate) {
		this.inputNodes = input;
		this.hiddenNodes = calculateHiddenLayerNodes(input, output);
		this.outputNodes = output;
		this.errorRate = errorRate;
		System.out.println("[INFO] Input Nodes: " + input + "\n[INFO] Hidden Nodes: " + hiddenNodes
				+ "\n[INFO] Output Nodes: " + output + "\n[INFO] Error Rate Set: " + errorRate);
		go(input, this.hiddenNodes, output);
	}

	/*
	 * Calculate Hidden Layer Nodes
	 * 
	 * Using this to calculate the number of nodes in the hidden layer Using only a
	 * single layer, would need to modify for other layers.. Using Geometric Pyramid
	 * rule to calculate optimal hidden layer
	 */
	public int calculateHiddenLayerNodes(int input, int output) {
		int hidden = (int) Math.sqrt((double) (input * output));
		return hidden;
	}

	/*
	 * Network Topology
	 * 
	 * Layer 1: Input Layer, No Activation function, bias = true, n input nodes
	 * Layer 2: Hidden Layer, Sigmoidal Activation, bias = true, n hidden nodes = Geometric pyriamid rule, n dropout = input * 5
	 * Layer 3: Output Layer, Softmax Activation, bias = false, n output nodes
	 */
	public BasicNetwork declareNetworkTopology() {
		basicNetwork = new BasicNetwork();
		basicNetwork.addLayer(new BasicLayer(null, true, inputNodes)); // Input layer
		basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), true, hiddenNodes, (inputNodes * 5)));
		basicNetwork.addLayer(new BasicLayer(new ActivationSoftMax(), false, outputNodes));
		basicNetwork.getStructure().finalizeStructure();
		basicNetwork.reset();
		setBasicNetwork(basicNetwork);
		return basicNetwork;
	}

	/*
	 * Prepare Training Data Set
	 * 
	 * Load and store from .csv as data set (codac)
	 */
	public MLDataSet prepareTrainingDataSet() {
		dataSetCodec = new CSVDataCODEC(new File(DATA_FILE), CSVFormat.DECIMAL_POINT, false, inputNodes, outputNodes,
				false);
		mlDataLoader = new MemoryDataLoader(dataSetCodec);
		mlDataSet = mlDataLoader.external2Memory();
		return mlDataSet;
	}

	/*
	 * Train Neural Network
	 * 
	 * The neural network is trained by first generating a folded dataset It is then
	 * backpropigated with an alpha and beta size of
	 */
	public void trainNeuralNetwork(BasicNetwork basicNetwork, MLDataSet mlDataSet) {
		foldedDataSet = new FoldedDataSet(mlDataSet);
		trainer = new ResilientPropagation(basicNetwork, foldedDataSet, 0.0001, 0.02);
		crossValidationKFold = new CrossValidationKFold(trainer, 5); // Crossfold validation
		long start = System.nanoTime();
		System.out.println("[INFO] Training...");
		EncogUtility.trainToError(trainer, errorRate);
		Utilities.saveNeuralNetwork(basicNetwork, "./trainedNN.nn");
		crossValidationKFold.finishTraining();
		long elapsedTime = System.nanoTime() - start;
		elapsedTime = elapsedTime / 1000000000;
		System.out.println("[INFO] Network Trained in: " + elapsedTime + " seconds");
	}

	/*
	 * Test Neural Network
	 * 
	 * This handles the dataset and checks the MLData pair for the languages in the
	 * actual dataset, looping through the actual data and comparing it with a
	 * negative -1 value
	 */
	public void testNeuralNetwork(BasicNetwork basicNetwork, MLDataSet mlDataSet) {
		double correct = 0; // Compute Test Error
		double total = 0;
		int found = -1;
		int ideal = 0;
		// Iterate over the dataset
		System.out.println("[INFO] Testing...");
		for (MLDataPair pair : mlDataSet) {
			MLData actual = basicNetwork.compute(pair.getInput()); // Compute the actual
			MLData expected = pair.getIdeal(); // Get the ideal
			for (int i = 0; i < actual.size(); i++) { // Iterate over the size of actual and compare
				if (actual.getData(i) >= -0.5 && (found == -1 || (actual.getData(i) > actual.getData(found))))
					found = i;
			}
			for (int i = 0; i < expected.size(); i++) {
				if (expected.getData(i) == 1.0) {
					ideal = i;
					if (found == ideal)
						correct++;
				}
			}
			total++;
		}
		System.out.println("[INFO] Testing Complete");
		System.out.println("[INFO] Total: " + total + " Correct: " + correct);
		System.out.println("[INFO] Testing Complete. Acc= " + ((correct / total) * 100) + "%");
	}

	/*
	 * Generate Prediction
	 * 
	 * This handles the prediction for the user input file 
	 * Compares values to closest lowest prediction to make choice
	 */
	public void generatePrediction(String neuralNetworkIn, double[] vectorsIn, Language[] languages) {
		MLData prediction = new BasicMLData(vectorsIn);
		prediction.setData(vectorsIn);
		MLData output = null;
		double lower = -0.5;
		int actual = 0;
		try {
			basicNetwork = Utilities.loadNeuralNetwork(neuralNetworkIn);
			output = basicNetwork.compute(prediction);
			for (int i = 0; i < output.size(); i++) {
				if (output.getData(i) > lower) {
					lower = output.getData(i);
					actual = i;
				}
			}
			System.out.println("\n[Prediction] -> " + languages[actual].toString());
		} catch (Exception e) {
			System.out.println("[ERROR] -> " + e);
		}
	}

	/*
	 * Go
	 * 
	 * Build, test, and train neural network
	 */
	public void go(int inputNodes, int hiddenNodes, int outputNodes) {
		/* Step 1: Declare a Network Topology */
		basicNetwork = declareNetworkTopology();
		// Step 2: Read the Training Data Set
		mlDataSet = prepareTrainingDataSet();
		// Step 3: Train the Neural Network
		trainNeuralNetwork(basicNetwork, mlDataSet);
		// Step 4: Test the NN
		testNeuralNetwork(basicNetwork, mlDataSet);
	}

	public BasicNetwork getBasicNetwork() {
		return basicNetwork;
	}

	public void setBasicNetwork(BasicNetwork basicNetwork) {
		this.basicNetwork = basicNetwork;
	}

	@Override
	public String toString() {
		return "NeuralNetwork [languages=" + Arrays.toString(languages) + ", DATA_FILE=" + DATA_FILE + ", basicNetwork="
				+ basicNetwork + ", crossValidationKFold=" + crossValidationKFold + ", dataSetCodec=" + dataSetCodec
				+ ", foldedDataSet=" + foldedDataSet + ", mlDataLoader=" + mlDataLoader + ", mlDataSet=" + mlDataSet
				+ ", trainer=" + trainer + ", inputNodes=" + inputNodes + ", hiddenNodes=" + hiddenNodes
				+ ", outputNodes=" + outputNodes + ", errorRate=" + errorRate + "]";
	}
}