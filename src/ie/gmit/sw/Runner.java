package ie.gmit.sw;

import java.io.IOException;
import java.util.Scanner;

import org.encog.neural.networks.BasicNetwork;
/*
 * RUNNER
 * 
 * This class handles the menu of the application
 * ...
 */
public class Runner {
	private final int DEFAULT_NGRAM_SIZE = 4;
	private final int DEFAULT_VH_COUNT = 150;
	private Language[] languages;
	private BasicNetwork neuralNetwork;
	private int ngramSize;
	private int vectorHashCount;

	private VectorProcessor vectorProcessor;
	private NeuralNetwork nn;

	public Language[] getLanguages() {
		return languages;
	}

	public void setLanguages(Language[] languages) {
		this.languages = languages;
	}

	public BasicNetwork getNeuralNetwork() {
		return neuralNetwork;
	}

	public void setNeuralNetwork(BasicNetwork loadNeuralNetwork) {
		this.neuralNetwork = loadNeuralNetwork;
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

	public Runner() throws IOException {
		if (getLanguages() == null)
			generateLanguages();
		setNgramSize(DEFAULT_NGRAM_SIZE);
		setVectorHashCount(DEFAULT_VH_COUNT);
		mainMenu();
	}

	public void generateLanguages() {
		/*
		 * Each of the languages in the enum Language can be represented as a number
		 * between 0 and 234. You can map the output of the neural network and the
		 * training data label to / from the language using the following. Eg. index 0
		 * maps to Achinese, i.e. langs[0].
		 */
		Language[] langs = Language.values(); // Only call this once...
//		for (int i = 0; i < langs.length; i++)
//			System.out.println(i + "-->" + langs[i]);
		setLanguages(langs);

		System.out.println("Total Lang: " + getLanguages().length);
	}

	public void displayMainMenuOptions() {
		String header = "\n** Langauge Detection Options Menu **\n";
		String option1 = " 1: Prepare Training Data [Vector Processor]\n";
		String option2 = " 2: Train Application\n";
		String option3 = " 3: Test Application\n";
		String option4 = " 4: Configurations Menu\n";
		String option5 = " 5: Display Configurations\n";
		String optionQ = "-1: Exit Application\n";

		System.out.print(header + option1 + option2 + option3 + option4 + option5 + optionQ);
	}

	public void displayConfigurationMenuOptions() {
		String header = "\n** Configuration Menu **\n";
		String option1 = " 1: Select ngram size\n";
		String option2 = " 2: Select input vector size\n";
		String option3 = " 3: Load NN\n";
		String option4 = " 4: Display Configurations\n";
		String optionQ = "-1: Go Back\n";

		System.out.print(header + option1 + option2 + option3 + option4 + optionQ);
	}

	public void displayConfigurations() {
		String header = "\n** Current Configuration **\n";
		String ngramSize = "Ngram Size: " + getNgramSize() + "\n";
		String vectorHashSize = "Vector Hash Size: " + getVectorHashCount() + "\n";
		String currentNN = "Current Neural Network: " + getNeuralNetwork() + "\n";

		System.out.println(header + ngramSize + vectorHashSize + currentNN);
	}

	public void displayError(int choice) {
		System.out.println("[ERROR]");
		System.out.println("Choice Not Valid -> " + choice);
	}

	public void handleNGramSize(Scanner scanner) {
		System.out.print("Input ngram size [1-10]\n-> ");
		int ngramSizeIn = scanner.nextInt();
		if (ngramSizeIn >= 10 || ngramSizeIn < 1) {
			System.out.println("[ERROR]");
			System.out.println("Invalid Range [1-10] -> " + ngramSizeIn);
		} else
			setNgramSize(ngramSizeIn);
		System.out.println("Ngram size now at: " + getNgramSize());
	}

	public void handleVectorHashSize(Scanner scanner) {
		System.out.print("Input vector hash count [1-500]\n-> ");
		int vectorHashSizeIn = scanner.nextInt();
		if (vectorHashSizeIn >= 500 || vectorHashSizeIn < 1) {
			System.out.println("[ERROR]");
			System.out.println("Invalid Range [1-500] -> " + vectorHashSizeIn);
		} else
			setVectorHashCount(vectorHashSizeIn);
		System.out.println("Vector hash size now at: " + getVectorHashCount());
	}

	public void handleNNLoad(Scanner scanner) throws IOException {
		new Utilities();
		
		System.out.print("Input Neural Network\n-> ");
		String nnIn = null;
		try {
			nnIn = scanner.next();
			BasicNetwork nn = Utilities.loadNeuralNetwork(nnIn);
			setNeuralNetwork(nn);
		} catch (Exception e) {
			System.out.println("[ERROR]\nFile not found -> " + e);
		}
	}
	
	public void vectorProcessorHandler() throws IOException {
		System.out.println("Prepairing Training Data...\nPlease wait...");
		vectorProcessor = new VectorProcessor(getVectorHashCount(), getNgramSize(), getLanguages());		
		vectorProcessor.go();
	}
	
	public void neuralNetworkHandler() {
		System.out.println("Training Neural Network...\nPlease wait...");
		System.out.println("vectorHashCount: " + vectorHashCount + "\nlanguages.length: " + languages.length);
//		nn = new NeuralNetwork(vectorHashCount, languages.length); 
	}

	public int getChoice(Scanner scanner, String inputPrompt) {
		System.out.print("\n" + inputPrompt);
		return scanner.nextInt();
	}

	public void mainMenu() throws IOException {
		Scanner scanner = new Scanner(System.in);
		String inputPrompt = "** Please Input Option **\n-> ";
		displayMainMenuOptions();
		displayConfigurations();

		int choice = getChoice(scanner, inputPrompt);
		while (choice != -1) {
			switch (choice) {
			case 1:
				vectorProcessorHandler();
				break;
			case 2:
//				System.out.println("Training application...\n[ISSUE] Not yet implemented");
				neuralNetworkHandler();
				break;
			case 3:
				System.out.println("Testing application...\n[ISSUE] Not yet implemented");
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
				handleNNLoad(scanner);
				break;
			case 4:
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

	public static void main(String[] args) throws IOException {
		new Runner();
	}
}