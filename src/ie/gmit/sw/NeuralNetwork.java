package ie.gmit.sw;

import java.io.File;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.buffer.MemoryDataLoader;
import org.encog.ml.data.buffer.codec.CSVDataCODEC;
import org.encog.ml.data.buffer.codec.DataSetCODEC;
import org.encog.ml.data.folded.FoldedDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.cross.CrossValidationKFold;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.csv.CSVFormat;

/*
 * *************************************************************************************
 * NB: READ THE FOLLOWING CAREFULLY AFTER COMPLETING THE TWO LABS ON ENCOG AND REVIEWING
 * THE LECTURES ON BACKPROPAGATION AND MULTI-LAYER NEURAL NETWORKS! YOUR SHOULD ALSO 
 * RESTRUCTURE THIS CLASS AS IT IS ONLY INTENDED TO DEMO THE ESSENTIALS TO YOU. 
 * *************************************************************************************
 * 
 * The following demonstrates how to configure an Encog Neural Network and train
 * it using backpropagation from data read from a CSV file. The CSV file should
 * be structured like a 2D array of doubles with input + output number of columns.
 * Assuming that the NN has two input neurons and two output neurons, then the CSV file
 * should be structured like the following:
 *
 *			-0.385, -0.231, 0.0, 1.0
 *			-0.538, -0.538, 1.0, 0.0
 *			-0.63,  -0.259, 1.0, 0.0
 *			-0.091, -0.636, 0.0, 1.0
 * 
 * The each row consists of four columns. The first two columns will map to the input
 * neurons and the last two columns to the output neurons. In the above example, rows 
 * 1 an 4 train the network with features to identify a category 2. Rows 2 and 3 contain
 * features relating to category 1.
 * 
 * You can normalize the data using the Utils class either before or after writing to 
 * or reading from the CSV file. 
 */
public class NeuralNetwork {
	private int inputNodes; // Reflect vector hash count
	private int outputNodes;

	public int getInputNodes() {
		return inputNodes;
	}

	public void setInputNodes(int inputNodes) {
		this.inputNodes = inputNodes;
	}

	public int getOutputNodes() {
		return outputNodes;
	}

	public void setOutputNodes(int outputNode) {
		this.outputNodes = outputNode;
	}

	public NeuralNetwork(int input, int output) {		
		setInputNodes(input);
		setOutputNodes(output);
		int hidden = calculateHiddenLayerNodes(input, output);
		
		// TODO: Debugging, remove when complete
		System.out.println("input: " + getInputNodes()
			+ "\noutput: " + getOutputNodes()
			+ "\nhidden: " + hidden);
		
		go(input, hidden, output);
	}
	
	public int calculateHiddenLayerNodes(int input, int output) {
		input = getInputNodes();
		output = getOutputNodes();
		int hidden = (int) Math.sqrt((double)(input * output));
		return hidden;
	}

	public void go(int inputNodes, int hiddenNodes, int outputNodes) {
		/* Step 1: Declare a Network Topology */
		BasicNetwork network = new BasicNetwork(); // Basic NN
		network.addLayer(new BasicLayer(null, true, inputNodes)); // Input Layer: No activation function, bias, n input
																// nodes
		// NOTE: Geometric pyriamid rule used to calc: 187
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, hiddenNodes)); // Hidden Layer: Sigmoid Function, bias, n
																				// hidden nodes
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, outputNodes)); // Output layer
		network.getStructure().finalizeStructure();
		network.reset();

		// Step 2: Read the Training Data Set
		DataSetCODEC dsc = new CSVDataCODEC(new File("data.csv"), CSVFormat.ENGLISH, false, getInputNodes(), getOutputNodes(), false);
		MemoryDataLoader mdl = new MemoryDataLoader(dsc);
		MLDataSet trainingSet = mdl.external2Memory();

		// Step 3: Train the Neural Network
		FoldedDataSet fds = new FoldedDataSet(trainingSet);
		ResilientPropagation mlTrain = new ResilientPropagation(network, fds);
		CrossValidationKFold cv = new CrossValidationKFold(mlTrain, 5); // Crossfold validation
		double minError = 0.1;
		int epoch = 1; // Use this to track the number of epochs
		// TODO:
		// Use backpropagation training with alpha=0.1 and momentum=0.2
//		Backpropagation trainer = new Backpropagation(network, trainingSet, 0.1, 0.2);

		long startTime = System.currentTimeMillis();
		System.out.println("[INFO] Training...");
		do {
			cv.iteration();
			long elapsedTime = System.currentTimeMillis() - startTime;
			long elapsedSeconds = elapsedTime / 1000;
			System.out.println(elapsedSeconds + "seconds elapsed");
			epoch++;
		} while (cv.getError() > minError);
		cv.finishTraining();
		System.out.println("[INFO] Training Complete in " + epoch + " epochs with e=" + cv.getError());

		// Save NN after training
		Utilities.saveNeuralNetwork(network, "./test.nn");

		// Step 4: Test the NN
		double correct = 0; // Compute Test Error
		double total = 0;
		for (MLDataPair pair : trainingSet) {
			total++;
			MLData output = network.compute(pair.getInput()); // Show input, get output
			int y = (int) Math.round(output.getData(0));
			int yd = (int) pair.getIdeal().getData(0);
			if (y == yd) {
				correct++;
			}
		}
		System.out.println("[INFO] Testing Complete. Acc=" + ((correct / total) * 100));
	}

	public static void main(String[] args) {
//		new NeuralNetwork(150, 235).calculateHiddenLayerNodes(150, 235);
	}
}