package ie.gmit.sw;

import java.io.File;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationReLU;
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
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.training.cross.CrossValidationKFold;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.EncogUtility;

public class NeuralNetwork {
	private BasicNetwork basicNetwork;
	private CrossValidationKFold crossValidationKFold;
	private DataSetCODEC dataSetCodec;
	private FoldedDataSet foldedDataSet;
	private MemoryDataLoader mlDataLoader;
	private MLDataSet mlDataSet;
	private ResilientPropagation trainer;
	private File dataFile = new File("data.csv");

	private int inputNodes; // Reflect vector hash count
	private int outputNodes;
	private int hiddenNodes;
	private int epochs, epoch = 1;
	private double errorRate;
	
	public NeuralNetwork(int input, int output, double errorRate) {
		this.inputNodes = input;
		this.hiddenNodes = calculateHiddenLayerNodes(input, output);
		this.outputNodes = output;
		this.errorRate = errorRate;
		
		System.out.println("Input Nodes: " + input + "\nHidden Nodes: " + hiddenNodes + "\nOutput Nodes: " + output + "\nError Rate Set: " + errorRate);
		
		go(input, this.hiddenNodes, output);
	}

	/*
	 * Calculate Hidden Layer Nodes
	 * 
	 * Using this to calculate the number of nodes in the hidden layer Using only a
	 * single layer, would need to modify for other layers.. Using Geometric Pyramid
	 * Rule to calculate optimal hidden layer
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
		dataSetCodec = new CSVDataCODEC(dataFile, CSVFormat.DECIMAL_POINT, false, inputNodes, outputNodes, false);
		mlDataLoader = new MemoryDataLoader(dataSetCodec);
		mlDataSet = mlDataLoader.external2Memory();
		return mlDataSet;
	}

	/*
	 * Train Neural Network
	 * 
	 * The neural network is trained by first generating a folded dataset
	 * It is then backpropigated with an alpha and beta size of
	 */
	public void trainNeuralNetwork(BasicNetwork basicNetwork, MLDataSet mlDataSet) {
		foldedDataSet = new FoldedDataSet(mlDataSet);
		trainer = new ResilientPropagation(basicNetwork, foldedDataSet, 0.0001, 0.02);
		crossValidationKFold = new CrossValidationKFold(trainer, 5); // Crossfold validation
		epoch = 1; // Use this to track the number of epochs

		long startTime = System.currentTimeMillis();
		System.out.println("[INFO] Training...");
//		do {
//			trainer.setDroupoutRate(0.5);
//			crossValidationKFold.iteration();
//			System.out.println("[Epoch]: " + epoch);
//			System.out.println("ERROR RATE: " + crossValidationKFold.getError());
//			epoch++;
//		} while (epoch < epochs);
		EncogUtility.trainToError(trainer, errorRate);

//		System.out.println(basicNetwork.dumpWeightsVerbose()); // Spit out weights and biases
		
		Utilities.saveNeuralNetwork(basicNetwork, "./trainedNN.nn");
		crossValidationKFold.finishTraining();
		System.out.println("[INFO] Training Complete in " + epoch + " epochs with e=" + crossValidationKFold.getError());
	}
	
	/*
	 * Test Neural Network
	 */
	public void testNeuralNetwork(BasicNetwork basicNetwork, MLDataSet mlDataSet) {
		double correct = 0; // Compute Test Error
		double total = 0;
		int result = -1;
		int ideal = 0;
		for (MLDataPair pair : mlDataSet) {
			MLData actual = basicNetwork.compute(pair.getInput());
			MLData expected = pair.getIdeal();
			for (int i = 0; i < actual.size(); i++) {
				if (actual.getData(i) > 0 && (result == -1 || (actual.getData(i) > actual.getData(result))))
					result = i;
			}
			for (int i = 0; i < expected.size(); i++) {
				if (expected.getData(i) == 1) {
					ideal = i;
					if (result == ideal)
						correct++;
				}
			}
			total++;
		}	
		
		System.out.println("total: " + total + " correct: " + correct);
		System.out.println("[INFO] Testing Complete. Acc=" + ((correct / total) * 100));
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

	//TODO: Move to menu
	public static void main(String[] args) {
		new NeuralNetwork(100, 235, 0.01);
	}
}