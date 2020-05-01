package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * this class will read in line by line of the wili dataset 
 * convert it to a vector hash
 * save into a csv for the nn to read and use for training data? 
 */
public class VectorProcessor {
	private int vectorHashCount;
	private double[] vectorHashedNgram;
	private DecimalFormat decimalFormat = new DecimalFormat("###.###"); // Decimal format to 3 Places of precision
	private int n;
	private Language[] languages;
	
	public int getVectorHashCount() {
		return vectorHashCount;
	}

	public void setVectorHashCount(int vectorHashCount) {
		this.vectorHashCount = vectorHashCount;
	}

	public VectorProcessor(int vectorHashCount, int n, Language[] languages) {
		this.vectorHashCount = vectorHashCount;
		this.vectorHashedNgram = new double[vectorHashCount];
		this.n = n;
		this.languages = languages;
	}

	@Override
	public String toString() {
		return "VectorProcessor [vectorHashCount=" + vectorHashCount + ", vectorHashedNgram="
				+ Arrays.toString(vectorHashedNgram) + ", decimalFormat=" + decimalFormat + ", n=" + n + ", languages="
				+ Arrays.toString(languages) + "]";
	}

	public void go() throws IOException {
		String file = "./wili-2018-Small-11750-Edited.txt";
		BufferedReader reader = null;
		String line = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file))));
			while ((line = reader.readLine()) != null) {
//				System.out.println(line);
				process(this.n, line);
			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			reader.close();
		}
	}

	public void process(int n, String line) {
		
		String[] record;
		String text, lang;

		try {
			record = line.split("@");

			if (record.length > 2)
				return; // Bail out if 2 @ symbols -- Dodgy text

			text = record[0].toLowerCase();
			lang = record[1]; // Language from wili
			
			int count = 0;
			for (int i = 0; i < vectorHashedNgram.length; i++) {
				vectorHashedNgram[i] = 0; // Initialise Vector
//				System.out.println("[" + i + "] " + vectorHashedNgram[i]);
				count++;
			}
			
			System.out.println(count + " vectors initialised");

			// Generate ngrams
//			for (int i = 0; i <= text.length() - n; i++) {
//				CharSequence ngram = text.substring(i, i + n);
//
////				System.out.println(ngram);
//				int index = ngram.hashCode() % vectorHashedNgram.length;
//				vectorHashedNgram[index]++;
////				System.out.println(index);
//			}

			// Normalise vectors between -1 and 1
//			Utilities.normalize(vectorHashedNgram, -1, 1);

			// Write out vector to CSV file using df.format(number); for each vector
//			for (int i = 0; i < vectorHashedNgram.length; i++) {
////				System.out.println(df.format(vector[i]));
//			}
			// index...
			// Write out the language numbers to the same row in csv file

			// Each row will have:
			// vector.length + number of labels (235 -> one for each language)

		} catch (Exception e) {
			System.out.println("[ERROR] -> " + e);
		}
	}

	public List<CharSequence> generateNgrams(int n, String text) {
		List<CharSequence> ngrams = new ArrayList<>();
		for (int i = 0; i <= text.length() - n; i++) {
			CharSequence kmer = text.substring(i, i + n);

			int index = kmer.hashCode() % vectorHashedNgram.length;

			System.out.print(index);
			ngrams.add(kmer);
		}
		return ngrams;
	}

	public void printList(List<CharSequence> list) {
		for (CharSequence i : list)
			System.out.println(i);
	}

//	public static void main(String[] args) throws IOException {
//		new VectorProcessor().go();
//	}

}
