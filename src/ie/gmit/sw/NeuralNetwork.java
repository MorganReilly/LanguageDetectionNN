package ie.gmit.sw;

import java.io.File;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
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

public class NeuralNetwork {
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

	public NeuralNetwork(int input, int output, double errorRate) {
		this.inputNodes = input;
		this.hiddenNodes = calculateHiddenLayerNodes(input, output);
		this.outputNodes = output;
		this.errorRate = errorRate;

		System.out.println("[INFO] Input Nodes: " + input + "\n[INFO] Hidden Nodes: " + hiddenNodes + "\n[INFO] Output Nodes: " + output
				+ "\n[INFO] Error Rate Set: " + errorRate);

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
	 * Layer 1: Input Layer No Activation function, bias = true, n input nodes Layer
	 * 2: Hidden Layer A Sigmoidal Activation, bias = true, n hidden nodes =
	 * Geometric pyriamid rule Layer 3: Output Layer
	 * 
	 */
	public BasicNetwork declareNetworkTopology() {
		basicNetwork = new BasicNetwork();
		basicNetwork.addLayer(new BasicLayer(null, true, inputNodes)); // Input layer
		basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), true, hiddenNodes));
		basicNetwork.addLayer(new BasicLayer(new ActivationSoftMax(), false, outputNodes));
		basicNetwork.getStructure().finalizeStructure();
		basicNetwork.reset();
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
	 * 
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
}