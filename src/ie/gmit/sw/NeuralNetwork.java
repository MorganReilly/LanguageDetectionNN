package ie.gmit.sw;

import java.io.File;

import org.encog.engine.network.activation.ActivationReLU;
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
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.util.csv.CSVFormat;

public class NeuralNetwork {
	private BasicNetwork basicNetwork;
	private CrossValidationKFold crossValidationKFold;
	private DataSetCODEC dataSetCodec;
	private FoldedDataSet foldedDataSet;
	private MemoryDataLoader mlDataLoader;
	private MLData mlOutput;
	private MLDataSet mlDataSet;
	private Backpropagation trainer;
	private File dataFile = new File("data.csv");

	private int inputNodes; // Reflect vector hash count
	private int outputNodes;
	private int hidden;
	private int epochs;
	private int hiddenLayers = 1;
	
	public NeuralNetwork(int input, int output, int epochs) {
		this.inputNodes = input;
		this.outputNodes = output;
		this.hidden = calculateHiddenLayerNodes(input, output);
		this.epochs = epochs;
		
		go(input, this.hidden, output);
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
		basicNetwork.addLayer(new BasicLayer(new ActivationReLU(), true, inputNodes));
		basicNetwork.addLayer(new BasicLayer(new ActivationReLU(), true, hiddenLayers, hidden));
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
	 */
	public void trainNeuralNetwork(BasicNetwork basicNetwork, MLDataSet mlDataSet) {
		foldedDataSet = new FoldedDataSet(mlDataSet);
		trainer = new Backpropagation(basicNetwork, foldedDataSet, 0.1, 0.2);
		crossValidationKFold = new CrossValidationKFold(trainer, 5); // Crossfold validation
		int epoch = 1; // Use this to track the number of epochs

		System.out.println("[INFO] Training...");
		do {
			crossValidationKFold.iteration();
			System.out.println("[Epoch]: " + epoch);
			System.out.println("ERROR RATE: " + crossValidationKFold.getError());
			epoch++;
		} while (epoch < epochs);
		
		Utilities.saveNeuralNetwork(basicNetwork, "./trainedNN.nn");
		crossValidationKFold.finishTraining();
		System.out.println("[INFO] Training Complete in " + epoch + " epochs with e=" + crossValidationKFold.getError());
	}
	
	public void testNeuralNetwork(BasicNetwork basicNetwork, MLDataSet mlDataSet) {
		double correct = 0; // Compute Test Error
		double total = 0;
		for (MLDataPair pair : mlDataSet) {
			total++;
			mlOutput = basicNetwork.compute(pair.getInput()); // Show input, get output
			int y = (int) Math.round(mlOutput.getData(0));
			int yd = (int) pair.getIdeal().getData(0);
			if (y == yd) {
				correct++;
			}
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

	public static void main(String[] args) {
		new NeuralNetwork(50, 235, 5);
	}
}