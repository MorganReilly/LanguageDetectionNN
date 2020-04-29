package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Parser {
	private Database db = null;
	private int k;
	private List<String> wiliLanguageList = new ArrayList<String>();
	private List<String> parsedWiliLangaugeList = new ArrayList<String>();
	private String[] wiliLanguageListArray = null;
	private List<String> ngramWiliList = new ArrayList<String>();

	public Parser(String file) throws IOException {
		// Read in text from file
		this.wiliLanguageList = generateListFromFile(file);
//		printList(wiliLanguageList);

		// Parse text
		this.parsedWiliLangaugeList = removeUnwantedCharacters(wiliLanguageList);
//		printList(parsedWiliLangaugeList);

		// Create ngrams
		this.ngramWiliList = generateNgrams(4, parsedWiliLangaugeList);
//		printList(ngramWiliList);
	}

	/*
	 * Parser
	 * 
	 * Reads in from file Strips out unwanted characters Parses into ngrams
	 */
	public List<String> generateListFromFile(String file) throws IOException {
		List<String> list = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
//				System.out.println("Line" + line);
				list.add(line);
			}
		} finally {
			reader.close();
		}

//		 Debugging
//	    for (String i : list) {
//	    	System.out.println(i);
//	    }
		return list;
	}

	public void go(File file) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line = null;

		while ((line = br.readLine()) != null) {
			String[] record = line.trim().split("@");
			if (record.length != 2)
				continue;
			parse(record[0], record[1]);
		}
		br.close();
	}

	private void parse(String text, String lang, int... ks) {
		Language language = Language.valueOf(lang);

		// Generate the k-mers
		for (int i = 0; i <= text.length() - k; i++) {
			CharSequence kmer = text.substring(i, i + k);
			db.add(kmer, language);
		}
	}

	/* Read through array list and strip out unwanted characters */
	public List<String> removeUnwantedCharacters(List<String> list) {
		String[] parsedWords = null;
		List<String> parsedWordList = new ArrayList<>();

		// Split on words and numbers, will ignore all other characters
		for (String word : list) {
//			System.out.println("word: " + word);
			parsedWords = word.toLowerCase().split("[^A-Za-z0-9]");
		}

		// Add the parsed words to a list, removing all spaces
		for (int i = 0; i < parsedWords.length; i++) {
//			System.out.println(parsedWords[i]);

			if (parsedWords[i] != null && !parsedWords[i].isEmpty()) {
				parsedWordList.add(parsedWords[i]);
			}
		}

//		printList(parsedWordList);
		return parsedWordList;
	}

	public List<String> generateNgrams(int n, List<String> list) {
		List<String> ngrams = new ArrayList<String>();
		for (int i = 0; i < list.size() - n + 1; i++) {
			ngrams.add(list.toString().substring(i, i + n));
		}
		return ngrams;
	}

	/*
	 * Print List
	 * 
	 * Prints a given list
	 */
	public void printList(List<String> list) {
		for (String i : list)
			System.out.println(i);
	}

	/*
	 * Main Runner
	 * 
	 * Currently has two files. Wili, Loreum
	 */
	public static void main(String[] args) throws IOException {
		String wiliLanguageFile = "./res/wili-2018-Small-11750-Edited.txt";
		String loremIpsum = "./res/lorem-ipsum.txt";

		new Parser(wiliLanguageFile);
	}

}
