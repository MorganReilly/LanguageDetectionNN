package ie.gmit.sw;

import java.util.Scanner;

public class Runner {
	public void displayOptions() {
		System.out.println("Language Detection Options Menu\n1: Do thing\n2: Other thing\n-1: Quit");
		
		// TODO: Options: Ngram size, vector size, load nn, save nn...
	}
	
	public void mainMenu() {
		displayOptions();
		Scanner scanner = new Scanner(System.in);
		System.out.print("Please Input option\n-> ");
		int choice = scanner.nextInt();
		while (choice != -1) {
			System.out.println("choice: " + choice);
			
			switch (choice) {
			case 1:
				System.out.println("Doing thing");
				break;
			case 2:
				System.out.println("Doing other thing");
				break;
			default:
				System.out.println("Invalid thing");
				break;
			}

			System.out.print("\nPlease Input option\n-> ");
			choice = scanner.nextInt();
		}
	}
	
	public static void main(String[] args) {
		/*
		 * Each of the languages in the enum Language can be represented as a number
		 * between 0 and 234. You can map the output of the neural network and the
		 * training data label to / from the language using the following. Eg. index 0
		 * maps to Achinese, i.e. langs[0].
		 */
//		Language[] langs = Language.values(); //Only call this once...		
//		for (int i = 0; i < langs.length; i++){
//			System.out.println(i + "-->" + langs[i]);
//		}

		new Runner().mainMenu();
	}
}