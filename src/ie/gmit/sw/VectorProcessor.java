package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;

/*
 * VECTOR PROCESSOR
 * 
 * This class handles the generation of training data.
 * The training data is generated from the wili-2018-small-11750 dataset.
 * It does this by reading line by line of the wili dataset text file.
 * On each line it separates the line into text and language.
 * With the text it generates a specific amount of ngrams.
 * Those ngrams are then vector hashed.
 * The vector hashes are normalised between -0.5 and 0.5 
 * From there the languages are set to 0.0 unless it is the language being processed, which is set to 1.0
 * Finally, the combination of all vector hashes and languages are written to a csv file. 
 */
public class VectorProcessor {
	private final String WILI_11750_SMALL = "./wili-2018-Small-11750-Edited.txt";
	private static String DATA_FILE = "data.csv";
	private int vectorHashCount, ngramSize;
	private double[] vectorNgram;
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
		this.vectorNgram = new double[vectorHashCount];
		this.ngramSize = n;
		this.languages = languages;
	}

	public void go() throws IOException {
		File data = new File(DATA_FILE);
		PrintWriter writer = new PrintWriter(data, "UTF-8");
		BufferedReader reader = null;
		String line = null;
		String toFile = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(WILI_11750_SMALL))));
			while ((line = reader.readLine()) != null) {
				toFile = process(this.ngramSize, line, writer);
				writer.print(toFile + "\n");
			}
		} catch (Exception e) {
			System.out.println("[ERROR] -> " + e);
		} finally {
			reader.close();
			System.out.println("CSV Generated -> " + data.getAbsolutePath());
		}
	}

	public String process(int n, String line, PrintWriter writer) {
		StringBuilder builder = new StringBuilder();
		String[] record;
		String text, lang;
		int index, i;
		try {
			/* Split Line */
			record = line.split("@");

			/* Handle Bad Text */
			if (!(record.length > 2)) {
				/* Store split text and langauges */
				text = record[0].toLowerCase();
				lang = record[1]; // Language from wili

				/* Initialise Vectors */
				for (i = 0; i < vectorNgram.length; i++)
					vectorNgram[i] = 0; // Initialise Vector

				/* Generate Vector Hash */
				for (i = 0; i <= text.length() - n; i++) {
					if (!(i >= vectorHashCount)) { // Don't wanna go out of our set bounds...
						CharSequence ngram = text.substring(i, i + n);
						index = ngram.hashCode() % vectorNgram.length;
						vectorNgram[i] = index;
					}
				}
				
				/* Normaise vector hashes between -0.5 and 0.5 */
				vectorNgram = Utilities.normalize(vectorNgram, -0.5, 0.5);
				
				/* Add normalised vectors to csv */
				for (i = 0; i < vectorNgram.length; i++)
					builder.append(vectorNgram[i] + ",");

				/* Add Languages to csv file */
				for (i = 0; i < languages.length; i++) {
					// Want to set the language being processed to 1, otherwise write a 0
					if (languages[i].toString().equals(lang))
						builder.append(1.0 + ",");
					else
						builder.append(0.0 + ",");
				}
				
				builder.setLength(builder.length() - 1); // Remove final comma at end of file
				return builder.toString();
			}
		} catch (Exception e) {
			System.out.println("[ERROR] -> " + e);
		}
		return builder.toString(); // This will cause blank lines in csv file
	}

	@Override
	public String toString() {
		return "VectorProcessor [vectorHashCount=" + vectorHashCount + ", ngram=" + ngramSize + ", vectorHashedNgram="
				+ Arrays.toString(vectorNgram) + ", decimalFormat=" + decimalFormat + ", languages="
				+ Arrays.toString(languages) + "]";
	}
}
