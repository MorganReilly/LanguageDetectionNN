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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * this class will read in line by line of the wili dataset 
 * convert it to a vector hash
 * save into a csv for the nn to read and use for training data? 
 */
public class VectorProcessor {
	private final String WILI_11750_SMALL = "./wili-2018-Small-11750-Edited.txt";
	private int vectorHashCount, ngramSize;
	private double[] vectorHashedNgram;
	private DecimalFormat decimalFormat = new DecimalFormat("###.###"); // Decimal format to 3 Places of precision
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
		this.ngramSize = n;
		this.languages = languages;
	}

	public void go() throws IOException {
		BufferedReader reader = null;
		String line = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(WILI_11750_SMALL))));
			while ((line = reader.readLine()) != null) {
//				System.out.println(line);
				process(this.ngramSize, line);
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
		int index, i;

		try {
			record = line.split("@");

			if (record.length > 2)
				return; // Bail out if 2 @ symbols -- Dodgy text

			text = record[0].toLowerCase();
			lang = record[1]; // Language from wili

			for (i = 0; i < vectorHashedNgram.length; i++)
				vectorHashedNgram[i] = 0; // Initialise Vector

			// Generate ngrams
			for (i = 0; i <= text.length() - n; i++) {
				CharSequence ngram = text.substring(i, i + n);
				index = ngram.hashCode() % vectorHashedNgram.length;
				vectorHashedNgram[index]++; // TODO: What is? 
			}
			
			System.out.println(this.toString());

			// Normalise vectors between -1 and 1
//			Utilities.normalize(vectorHashedNgram, -1, 1);

//			System.out.println(toString()); // Debugging

			// Write out vector to CSV file using df.format(number); for each vector
//			for (i = 0; i < vectorHashedNgram.length; i++) {
//				System.out.print(decimalFormat.format(vectorHashedNgram[i]) + " ");
//
//			}
//			System.out.print("\n");
			// index...
			// Write out the language numbers to the same row in csv file

			// Each row will have:
			// vector.length + number of labels (235 -> one for each language)

		} catch (Exception e) {
			System.out.println("[ERROR] -> " + e);
		}
	}

	@Override
	public String toString() {
		return "VectorProcessor [vectorHashCount=" + vectorHashCount + ", ngram=" + ngramSize + ", vectorHashedNgram="
				+ Arrays.toString(vectorHashedNgram) + ", decimalFormat=" + decimalFormat + ", languages="
				+ Arrays.toString(languages) + "]";
	}

//	public static void main(String[] args) throws IOException {
//		new VectorProcessor().go();
//	}

}
