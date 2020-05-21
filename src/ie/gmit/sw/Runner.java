package ie.gmit.sw;

import java.io.IOException;
import java.util.Scanner;

/*
 * RUNNER
 * 
 * This class handles the menu of the application
 * It also sets the defaults of the application
 */
public class Runner {
	private final int DEFAULT_NGRAM_SIZE = 2; // Optimal -> 2
	private final int DEFAULT_VH_COUNT = 300; // Optimal -> 300 (older GPUs), -> 1000 (newer GPUs)
	private final double DEFAULT_ERROR_RATE = 0.0001; // Optimal -> 0.0001
	private int ngramSize;
	private int vectorHashCount;
	private VectorProcessor vectorProcessor;
	private LanguageClassifier languageClassifier;
	private Language[] languages;
	private double errorRate;
	private NeuralNetwork neuralNetwork;

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
		sb.append("--------- Main Menu ---------\n\n[OPTIONS]\n"); // Header
		sb.append("|  1: Prepare Training Data\n"); // Option 1
		sb.append("|  2: Train & Test Application\n"); // Option 2
		sb.append("|  3: Input User Data\n"); // Option 3
		sb.append("|  4: Configurations Menu\n"); // Option 4
		sb.append("|  5: Display Configurations\n"); // Option 5
		sb.append("|  6: Help\n"); // Option 6
		sb.append("| -1: Exit Application"); // Exit option
		System.out.print(sb.toString());
	}

	/*
	 * Configureations Menu Options
	 * 
	 * Display the list of options used for the configuration menu
	 */
	public void displayConfigurationMenuOptions() {
		StringBuilder sb = new StringBuilder();
		sb.append("------ Configuration Menu ------\n\n[OPTIONS]\n"); // Header
		sb.append("|  1: Select N-Gram Size\n"); // Option 1
		sb.append("|  2: Select Input Vector Size\n"); // Option 2
		sb.append("|  3: Select Error Rate Size\n"); // Option 3
		sb.append("|  4: Reset Default Configurations\n");
		sb.append("|  5: Display Configurations\n"); // Option 5
		sb.append("| -1: Go Back"); // Go back
		System.out.print(sb.toString());
	}

	/*
	 * Display Configurations
	 * 
	 * Set and handle all relevant configurations for NN
	 */
	public void displayConfigurations() {
		StringBuilder sb = new StringBuilder();
		sb.append("-- Current Configuration --\n\n[CONFIG]\n"); // Header
		sb.append("| Ngram Size: " + getNgramSize() + "\n"); // Ngram Size
		sb.append("| Vector Hash Size: " + getVectorHashCount() + "\n"); // Vector Hash Size
		sb.append("| Error Rate: " + getErrorRate() + "\n"); // Error rate
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
		System.out.print("[MENU] Input ngram size [1-10]:\n-> ");
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
		System.out.print("[MENU] Input vector hash count [1-1000]:\n-> ");
		int vectorHashSizeIn = scanner.nextInt();
		if (vectorHashSizeIn > 1000 || vectorHashSizeIn < 1)
			System.out.println("[ERROR]\nInvalid Range [1-1000] -> " + vectorHashSizeIn);
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
		// TODO: Fix me
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
		System.out.print("[MENU] Input error rate [1.0 - 0.000001]:\n-> ");
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
		System.out.println("[INFO] Prepairing Training Data...\n[INFO] Please wait...");
		vectorProcessor = new VectorProcessor(getVectorHashCount(), getNgramSize(), getLanguages());
		vectorProcessor.go();
	}

	/*
	 * Neural Netowork Handler
	 * 
	 * 
	 */
	public void neuralNetworkHandler(Scanner scanner) {
		System.out.println("[INFO] Training Neural Network...\n[INFO] Please wait...");
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
		System.out.println("[INFO] Defaults reset");
	}

	/*
	 * Display Help
	 * 
	 * Displays some text to the user which they may find useful
	 */
	public void displayHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append("* Run option 1 on fresh launch or on configuration change\n");
		sb.append("* Run option 2 to train application on data generated from option 1\n");
		sb.append("* Run option 3 to input your own data from either a file or from the console\n");
		sb.append(
				"* Run option 4 to go into the configurations menu. From there you can set up the neural networks configuration\n");
		sb.append("* Run option 5 to display the current neural network configurations\n");
		sb.append("* To quit the application, or to go back a menu level, input -1");
		System.out.println(sb.toString());
	}

	public void userInputHandler(Scanner scanner) throws IOException {
		String inputPrompt = "[OPTIONS]\n|  1: User File\n|  2: Console Input\n| -1: Quit\n-> ";
		int choice = getChoice(scanner, inputPrompt);
		while (choice != -1) {
			switch (choice) {
			case 1:
				System.out.println("[INFO] Prepairing User Input...\n[INFO] Please wait...");
				languageClassifier = new LanguageClassifier(getVectorHashCount(), getNgramSize());
				languageClassifier.generateFromFile();
				break;
			case 2:
				break;
			default:
				displayError(choice);
				break;
			}
			inputPrompt = "[OPTIONS]\n|  1: User File\n|  2: Console Input\n| -1: Quit\n-> ";
			choice = getChoice(scanner, inputPrompt);
		}
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
		String inputPrompt = "-> ";
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
				userInputHandler(scanner);
				break;
			case 4:
				configurationsMenu(scanner, inputPrompt);
				break;
			case 5:
				displayConfigurations();
				break;
			case 6:
				displayHelp();
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
	 * Accessors & Mutators
	 * 
	 * Getters and Setters for all class level variables
	 */
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
	 * Main
	 * 
	 * Run the Runner
	 */
	public static void main(String[] args) throws IOException {
		new Runner();
	}
}