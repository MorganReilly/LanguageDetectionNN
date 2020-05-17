package ie.gmit.sw;

import java.io.File;
import java.sql.ResultSet;
import java.util.Arrays;

import org.encog.ConsoleStatusReportable;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.buffer.MemoryDataLoader;
import org.encog.ml.data.buffer.codec.CSVDataCODEC;
import org.encog.ml.data.buffer.codec.DataSetCODEC;
import org.encog.ml.data.folded.FoldedDataSet;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.sources.CSVDataSource;
import org.encog.ml.data.versatile.sources.VersatileDataSource;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.model.EncogModel;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.cross.CrossValidationKFold;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;
import org.encog.util.simple.EncogUtility;

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
	private static String DATA_FILE = "data.csv";

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

	public NeuralNetwork() {
	}

	public NeuralNetwork(int input, int output) {
		setInputNodes(input);
		setOutputNodes(output);
		int hidden = calculateHiddenLayerNodes(input, output);

		// TODO: Debugging, remove when complete
		System.out.println("input: " + getInputNodes() + "\noutput: " + getOutputNodes() + "\nhidden: " + hidden);

		go(input, hidden, output);
	}

	/*
	 * Calculate Hidden Layer Nodes
	 * 
	 * Using this to calculate the number of nodes in the hidden layer Using only a
	 * single layer, would need to modify for other layers.. Using Geometric Pyramid
	 * Rule to calculate optimal hidden layer
	 */
	public int calculateHiddenLayerNodes(int input, int output) {
		input = getInputNodes();
		output = getOutputNodes();
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
	public BasicNetwork declareNetworkTopology(int hiddenNodes) {
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, true, getInputNodes()));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, hiddenNodes));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, getOutputNodes()));
		network.getStructure().finalizeStructure();
		network.reset();
		return network;
	}

	/*
	 * Prepare Training Data Set
	 * 
	 * Load and store from .csv as data set (codac)
	 */
	public MLDataSet prepareTrainingDataSet() {
		DataSetCODEC dsc = new CSVDataCODEC(new File(DATA_FILE), CSVFormat.ENGLISH, false, getInputNodes(),
				getOutputNodes(), false);
		MemoryDataLoader mdl = new MemoryDataLoader(dsc);
		MLDataSet trainingSet = mdl.external2Memory();
		return trainingSet;
	}

	/*
	 * goEncog
	 * 
	 * Current main runner TODO: Break into smaller components...
	 */
	public void goEncog(int vectorCount) {
		// Step 1: Mapping the Input File -> Prepare for NN
		VersatileDataSource source = new CSVDataSource(new File(DATA_FILE), false, CSVFormat.DECIMAL_POINT);
		VersatileMLDataSet data = new VersatileMLDataSet(source);
		for (int i = 0; i < vectorCount; i++) // Defining vector hash columns
			data.defineSourceColumn("vector-hash", i, ColumnType.continuous);
		ColumnDefinition out = data.defineSourceColumn("language", vectorCount, ColumnType.nominal);
		data.analyze();
		data.defineSingleOutputOthersInput(out);

		// Step 2: Specifying the Model & Normalizing
		EncogModel model = new EncogModel(data);
		model.selectMethod(data, MLMethodFactory.TYPE_FEEDFORWARD);
		model.setReport(new ConsoleStatusReportable());
		data.normalize();

		// Step 3: Fitting the Model -> Hold 30% of data back to use for testing later
		/*
		 * Note: Cross-validation breaks the training dataset into 5 different
		 * combinations of training and validation data.
		 */
		model.holdBackValidation(0.3, true, 1001);
		model.selectTrainingType(data);
		MLRegression bestMethod = (MLRegression) model.crossvalidate(5, true);

		// Step 4: Displaying the Results
		System.out.println(
				"Training error: " + EncogUtility.calculateRegressionError(bestMethod, model.getTrainingDataset()));
		System.out.println(
				"Validation error: " + EncogUtility.calculateRegressionError(bestMethod, model.getValidationDataset()));
		NormalizationHelper helper = data.getNormHelper();
		System.out.println(helper.toString());
		System.out.println("Final Model: " + bestMethod);

		// Save NN after training
//		Utilities.saveNeuralNetwork(model, "./test.nn");

		// Step 5: Using the Model & Denormalising
		ReadCSV csv = new ReadCSV(new File(DATA_FILE), false, CSVFormat.DECIMAL_POINT);
		String[] line = new String[vectorCount];
		MLData input = helper.allocateInputVector();
		while (csv.next()) {
			StringBuilder result = new StringBuilder();
			for (int i = 0; i < vectorCount; i++)
				line[i] = csv.get(i);
			String correct = csv.get(vectorCount); // The expected result is the last column on each row
			helper.normalizeInputVector(line, input.getData(), false);
			MLData output = bestMethod.compute(input); // Ask the network to classify the input
			String languageChosen = helper.denormalizeOutputVectorToString(output)[0];
//			result.append(Arrays.toString(line));
			result.append("Expected: ");
			result.append(languageChosen);
			result.append("(correct: ");
			result.append(correct);
			result.append(")");
			System.out.println(result.toString());
		}
		// Step 5: Shutdown the Model
		Encog.getInstance().shutdown();
	}

	public void go(int inputNodes, int hiddenNodes, int outputNodes) {
		/* Step 1: Declare a Network Topology */
		BasicNetwork network = declareNetworkTopology(hiddenNodes);

		// Step 2: Read the Training Data Set
		MLDataSet trainingSet = prepareTrainingDataSet();

		// Step 3: Train the Neural Network
		FoldedDataSet folderDataSet = new FoldedDataSet(trainingSet);
		ResilientPropagation mlTrain = new ResilientPropagation(network, folderDataSet);
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
			System.out.println("[INFO] " + elapsedSeconds + " sec elapsed");
			epoch++;
		} while (cv.getError() > minError);
		cv.finishTraining();
		System.out.println("[INFO] Training Complete in " + epoch + " epochs with e=" + cv.getError());

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
		new NeuralNetwork().goEncog(50);
	}
}