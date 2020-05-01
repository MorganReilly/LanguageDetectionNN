package ie.gmit.sw;

import java.util.Scanner;

public class Runner {

	private Language[] languages = null;
	private int ngramSize;
	private int vectorHashSize;

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

	public int getVectorHashSize() {
		return vectorHashSize;
	}

	public void setVectorHashSize(int vectorHashSize) {
		this.vectorHashSize = vectorHashSize;
	}

	public Runner() {
		if (getLanguages() == null)
			generateLanguages();
		setNgramSize(4);
		setVectorHashSize(100);
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

	public void displayOptions() {
		String header = "\nLangauge Detection Options Menu\n";
		String option1 = " 1: Select ngram size\n";
		String option2 = " 2: Select input vector size\n";
		String option3 = " 3: Load NN\n";
		String option4 = " 4: Display Configurations\n";
		String optionQ = "-1: Quit\n";

		System.out.println(header + option1 + option2 + option3 + option4 + optionQ);
	}

	public void displayConfigurations() {
		String header = "\nCurrent Configuration\n";
		String ngramSize = "Ngram Size: " + getNgramSize() + "\n";
		String vectorHashSize = "Vector Hash Size: " + getVectorHashSize() + "\n";

		System.out.println(header + ngramSize + vectorHashSize);
	}

	public void mainMenu() {
		String inputDisplay = "Please Input Option\n-> ";
		displayOptions();

		Scanner scanner = new Scanner(System.in);
		System.out.print(inputDisplay);
		int choice = scanner.nextInt();
		while (choice != -1) {
			switch (choice) {
			case 1:
				System.out.print("Input ngram size\n-> ");
				int ngramSizeIn = scanner.nextInt();
				if (ngramSizeIn >= 10 || ngramSizeIn < 1) {
					System.out.println("[ERROR]");
					System.out.println("Invalid Range [1-10] -> " + ngramSizeIn);
				} else
					setNgramSize(ngramSizeIn);
				System.out.println("Ngram size now at: " + getNgramSize());
				break;
			case 2:
				System.out.print("Input vector hash size\n-> ");
				int vectorHashSizeIn = scanner.nextInt();
				if (vectorHashSizeIn >= 1000 || vectorHashSizeIn < 1) {
					System.out.println("[ERROR]");
					System.out.println("Invalid Range [1-1000] -> " + vectorHashSizeIn);
				} else
					setVectorHashSize(vectorHashSizeIn);
				System.out.println("Vector hash size now at: " + getVectorHashSize());
				break;
			case 3:
				System.out.println("Load previous NN");
				System.out.println("Not yet implemented");
				break;
			case 4:
				displayConfigurations();
				break;
			default:
				System.out.println("[ERROR]");
				System.out.println("Choice Not Valid -> " + choice);
				break;
			}

			displayOptions();
			System.out.print("\n" + inputDisplay);
			choice = scanner.nextInt();
		}
	}

	public static void main(String[] args) {
		new Runner();
	}
}