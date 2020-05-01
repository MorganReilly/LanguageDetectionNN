package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/*
 * this class will read in line by line of the wili dataset 
 * convert it to a vector hash
 * save into a csv for the nn to read and use for training data? 
 */
public class VectorProcessor {

	private double[] vectorHashedNgram = new double[100]; // TODO: Make flexible
	private DecimalFormat decimalFormat = new DecimalFormat("###.###"); // Decimal format to 3 Places of precision
	private int n = 4; // Number of ngrams
//	private Language[] langs... //TODO: Get from runner

	public void go() throws IOException {
		String file = "./wili-2018-Small-11750-Edited.txt";
//		String line = null;
//		try (BufferedReader br = new BufferedReader(
//				new InputStreamReader(new FileInputStream(new File("./wili-2018-Small-11750-Edited.txt"))))) {
//
//			// Read line by line through wili data set
//			while (br.readLine() != null) {
//				// Process the line
//				// Keep track of numbers?
//				
//				System.out.println(line);
//				process(line);
//			}
//		} catch (Exception e) {
//
//		}
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
//				System.out.println(line);
				process(line);
			}
		} finally {
			reader.close();
		}
	}

	public void process(String line) {
		String[] record = line.split("@");

		if (record.length > 2)
			return; // Bail out if 2 @ symbols -- Dodgy text

		// Convert to lowercase
		String text = record[0].toLowerCase(); // Text from wili
		String lang = record[1]; // Language from wili

		for (int i = 0; i < vectorHashedNgram.length; i++)
			vectorHashedNgram[i] = 0; // Initialise Vector

		// Generate ngrams
		for (int i = 0; i <= text.length() - n; i++) {
			CharSequence ngram = text.substring(i, i + n);
			
			System.out.println(ngram);
			int index = ngram.hashCode() % vectorHashedNgram.length;
			vectorHashedNgram[index]++;
//			System.out.println(index);
		}

		// Normalise vectors between -1 and 1
		Utilities.normalize(vectorHashedNgram, -1, 1);

		// Write out vector to CSV file using df.format(number); for each vector
		for (int i = 0; i<vectorHashedNgram.length; i++) {
//			System.out.println(df.format(vector[i]));
		}
		// index...
		// Write out the language numbers to the same row in csv file

		// Each row will have:
		// vector.length + number of labels (235 -> one for each language)
	}

	public List<CharSequence> generateNgrams(int n, String text) {
		List<CharSequence> ngrams = new ArrayList<>();
		for (int i = 0; i <= text.length() - n; i++) {
			CharSequence kmer = text.substring(i, i + n);
			
			int index = kmer.hashCode() % vectorHashedNgram.length;
			
			System.out.println(index);
			ngrams.add(kmer);
		}
		return ngrams;
	}

	public void printList(List<CharSequence> list) {
		for (CharSequence i : list)
			System.out.println(i);
	}

	public static void main(String[] args) throws IOException {
		new VectorProcessor().go();
	}

}
