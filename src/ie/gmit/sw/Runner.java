package ie.gmit.sw;

import java.io.IOException;
import java.util.Scanner;

import org.encog.neural.networks.BasicNetwork;

/*
 * RUNNER
 * 
 * This class handles the menu of the application
 * It also sets the defaults of the application
 */
public class Runner {
	private final int DEFAULT_NGRAM_SIZE = 2; // Don't touch
	private final int DEFAULT_VH_COUNT = 300; // Don't touch
	private final double DEFAULT_ERROR_RATE = 0.0001; // Don't touch
//	private final double DEFAULT_ERROR_RATE = 0.1; // Testing
	private int ngramSize;
	private int vectorHashCount;
	private VectorProcessor vectorProcessor;
	private Language[] languages;
	private double errorRate;
	private NeuralNetwork neuralNetwork;

	public Language[] getLanguages() {
		return languages;
	}

	public void setLanguages(Language[] languages) {
		this.languages = languages;
	}

	public int getNgramSize() {
		return ngramSize;
	}

	public void setNgramSize(int ngramSize) {
		this.ngramSize = ngramSize;
	}

	public int getVectorHashCount() {
		return vectorHashCount;
	}

	public void setVectorHashCount(int vectorHashSize) {
		this.vectorHashCount = vectorHashSize;
	}

	public double getErrorRate() {
		return errorRate;
	}

	public void setErrorRate(double errorRate) {
		this.errorRate = errorRate;
	}

	/*
	 * Runner
	 * 
	 * Run the application Generate the languages for the output layer Set the ngram
	 * size Set the vector hash count Call main menu
	 */
	public Runner() throws IOException {
		if (getLanguages() == null)
			generateLanguages();
		setNgramSize(DEFAULT_NGRAM_SIZE);
		setVectorHashCount(DEFAULT_VH_COUNT);
		setErrorRate(DEFAULT_ERROR_RATE);
		mainMenu();
	}

	/*
	 * Generate Languages
	 * 
	 * Each of the languages in the enum Language can be represented as a number
	 * between 0 and 234. You can map the output of the neural network and the
	 * training data label to / from the language using the following. Eg. index 0
	 * maps to Achinese, i.e. langs[0].
	 */
	public void generateLanguages() {
		Language[] langs = Language.values(); // Only call this once...
		setLanguages(langs);
	}

	/*
	 * Main Menu Options
	 * 
	 * Display the list of options used for the main menu
	 */
	public void displayMainMenuOptions() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n** Langauge Detection Options Menu **\n"); // Header
		sb.append(" 1: Prepare Training Data [Vector Processor]\n"); // Option 1
		sb.append(" 2: Train Application\n"); // Option 2
		sb.append(" 3: Test Application\n"); // Option 3
		sb.append(" 4: Configurations Menu\n"); // Option 4
		sb.append(" 5: Display Configurations\n"); // Option 5
		sb.append("-1: Exit Application\n"); // Exit option
		System.out.println(sb.toString());
	}

	/*
	 * Configureations Menu Options
	 * 
	 * Display the list of options used for the configuration menu
	 */
	public void displayConfigurationMenuOptions() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n** Configuration Menu **\n"); // Header
		sb.append(" 1: Select ngram size\n"); // Option 1
		sb.append(" 2: Select input vector size\n"); // Option 2
		sb.append(" 3: Select error rate size\n"); // Option 3
		sb.append(" 4: Reset Default Configurations\n");
		sb.append(" 5: Display Configurations\n"); // Option 5
		sb.append("-1: Go Back\n"); // Go back
		System.out.println(sb.toString());
	}

	/*
	 * Display Configurations
	 * 
	 * Set and handle all relevant configurations for NN
	 */
	public void displayConfigurations() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n** Current Configuration **\n"); // Header
		sb.append("Ngram Size: " + getNgramSize() + "\n"); // Ngram Size
		sb.append("Vector Hash Size: " + getVectorHashCount() + "\n"); // Vector Hash Size
		sb.append("Error Rate: " + getErrorRate() + "\n"); // Error rate
		System.out.println(sb.toString());
	}

	/*
	 * Display Error
	 * 
	 * Basic error message, might change...
	 */
	public void displayError(int choice) {
		System.out.println("[ERROR]");
		System.out.println("Choice Not Valid -> " + choice);
	}

	/*
	 * Handle NGram Size
	 * 
	 * Handler used for grabbing the ngram from the user
	 */
	public void handleNGramSize(Scanner scanner) {
		System.out.print("Input ngram size [1-10]\n-> ");
		int ngramSizeIn = scanner.nextInt();
		if (ngramSizeIn > 10 || ngramSizeIn < 1) {
			System.out.println("[ERROR]");
			System.out.println("Invalid Range [1-10] -> " + ngramSizeIn);
		} else
			setNgramSize(ngramSizeIn);
		System.out.println("Ngram size now at: " + getNgramSize());
	}

	/*
	 * Handle Vector Hash Size
	 * 
	 * Handler used for grabbing vector hash size from user
	 */
	public void handleVectorHashSize(Scanner scanner) {
		System.out.print("Input vector hash count [1-500]\n-> ");
		int vectorHashSizeIn = scanner.nextInt();
		if (vectorHashSizeIn > 500 || vectorHashSizeIn < 1)
			System.out.println("[ERROR]\nInvalid Range [1-500] -> " + vectorHashSizeIn);
		else
			setVectorHashCount(vectorHashSizeIn);
		System.out.println("Vector hash size now at: " + getVectorHashCount());
	}

	/*
	 * Handle Neural Network Load
	 * 
	 * Handler used for loading NN from file from user specified location
	 */
	public void handleNNLoad(Scanner scanner) throws IOException {
		//TODO: Fix me
//		new Utilities();
//		System.out.print("Input Neural Network\n-> ");
//		String nnIn = null;
//		try {
//			nnIn = scanner.next();
//			BasicNetwork nn = Utilities.loadNeuralNetwork(nnIn);
//			setLoadNN(nn);
//		} catch (Exception e) {
//			System.out.println("[ERROR]\nFile not found -> " + e);
//		}
	}

	public void handleErrorRate(Scanner scanner) {
		System.out.print("Input error rate [1.0 - 0.000001]\n-> ");
		double errorRateIn = scanner.nextDouble();
		if (errorRateIn > 1.0 || errorRateIn < 0.000001)
			System.out.println("[ERROR]\nInvalid Range [1.0 - 0.000001] -> " + errorRateIn);
		else
			setErrorRate(errorRateIn);
		System.out.println("Error rate now at: " + getErrorRate());
	}

	/*
	 * Vector Processor Handler
	 * 
	 * Handler used for generating the train/test data based on ngram size vector
	 * hash size
	 */
	public void vectorProcessorHandler() throws IOException {
		System.out.println("Prepairing Training Data...\nPlease wait...");
		vectorProcessor = new VectorProcessor(getVectorHashCount(), getNgramSize(), getLanguages());
		vectorProcessor.go();
	}

	/*
	 * Neural Netowork Handler
	 * 
	 * 
	 */
	public void neuralNetworkHandler(Scanner scanner) {
		System.out.println("Training Neural Network...\nPlease wait...");
		neuralNetwork = new NeuralNetwork(getVectorHashCount(), getLanguages().length, getErrorRate());
	}

	/*
	 * Reset Defaults
	 * 
	 * Resets to the best possible outputs that I've found so far
	 */
	public void resetDefaults() {
		setNgramSize(DEFAULT_NGRAM_SIZE);
		setVectorHashCount(DEFAULT_VH_COUNT);
		setErrorRate(DEFAULT_ERROR_RATE);
	}

	/*
	 * Get Choice
	 * 
	 * Grab the user input as a String
	 */
	public int getChoice(Scanner scanner, String inputPrompt) {
		System.out.print("\n" + inputPrompt);
		return scanner.nextInt();
	}

	/*
	 * Main Menu
	 * 
	 * Display the main menu options to the user
	 */
	public void mainMenu() throws IOException {
		Scanner scanner = new Scanner(System.in);
		String inputPrompt = "** Please Input Option **\n-> ";
		displayMainMenuOptions();
		int choice = getChoice(scanner, inputPrompt);
		while (choice != -1) {
			switch (choice) {
			case 1:
				vectorProcessorHandler();
				break;
			case 2:
				neuralNetworkHandler(scanner);
				break;
			case 3:
				System.out.println("Testing application...\n[ISSUE] Not yet implemented"); // User input
				break;
			case 4:
				configurationsMenu(scanner, inputPrompt);
				break;
			case 5:
				displayConfigurations();
				break;
			default:
				displayError(choice);
				break;
			}
			displayMainMenuOptions();
			choice = getChoice(scanner, inputPrompt);
		}
	}

	/*
	 * Configurations Menu
	 * 
	 * Display the configurations menu to the user
	 */
	public void configurationsMenu(Scanner scanner, String inputPrompt) throws IOException {
		displayConfigurationMenuOptions();
		int choice = getChoice(scanner, inputPrompt);
		while (choice != -1) {
			switch (choice) {
			case 1:
				handleNGramSize(scanner);
				break;
			case 2:
				handleVectorHashSize(scanner);
				break;
			case 3:
				handleErrorRate(scanner);
				break;
			case 4:
				resetDefaults();
				break;
			case 5:
				displayConfigurations();
				break;
			default:
				displayError(choice);
				break;
			}
			displayConfigurationMenuOptions();
			choice = getChoice(scanner, inputPrompt);
		}
	}

	/*
	 * Main
	 * 
	 * Run the Runner
	 */
	public static void main(String[] args) throws IOException {
		new Runner();
	}
}