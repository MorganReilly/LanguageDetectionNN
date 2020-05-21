package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/*
 * Language Classifier
 * 
 * @author: Morgan Reilly
 * 
 * This class is a simplified version of the VectorProcessor
 * It handles the generation of ngrams from a file which the user can edit
 * It does so in a similar fashion to the vector processor
 * It does this by reading in line by line of text which the user has specified
 * With that text it generates a specific number of ngrams, based on vector hash count
 * Those ngrams are then vector hashed and normalised between -0.5 and 0.5
 * It is then written to file
 * 
 * Note: This is a live data file handler so there is no '@' delimiter or handling
 */
public class LanguageClassifier {
	private final File DATA_FILE = new File("userGenData.csv");
	private int vectorHashCount, ngramSize;
	private double[] vectorNgram;

	public LanguageClassifier(int vectorHashCount, int n) {
		this.vectorHashCount = vectorHashCount;
		this.vectorNgram = new double[vectorHashCount];
		this.ngramSize = n;
	}

	/*
	 * Generate From File
	 * 
	 * Reads each line of file
	 * Send through process
	 * Write to file
	 */
	public void generateFromFile(String file) throws IOException {
		PrintWriter writer = new PrintWriter(DATA_FILE, "UTF-8");
		BufferedReader reader = null;
		String line = null, toFile = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file))));
			while ((line = reader.readLine()) != null) {
				toFile = process(this.ngramSize, line, writer);
				writer.print(toFile + "\n");
			}
		} catch (Exception e) {
			System.out.println("[ERROR] -> " + e);
		} finally {
			reader.close();
			System.out.println("[INFO] " + DATA_FILE + " Generated -> " + DATA_FILE.getAbsolutePath());
			writer.close();
		}
	}

	/*
	 * Process
	 * 
	 * Read text
	 * Initialise vector hashes to 0
	 * Generate Ngrams
	 * Generate vector hash
	 * Normalise Vector hashed ngrams
	 * Add vector hashes to CSV builder
	 * Return builder
	 */
	public String process(int n, String line, PrintWriter writer) {
		StringBuilder builder = new StringBuilder();
		String text;
		int index, i;
		try {
			text = line.toLowerCase();

			/* Initialise Vectors */
			for (i = 0; i < vectorNgram.length; i++)
				vectorNgram[i] = 0; // Initialise Vector

			/* Generate Vector Hash */
			for (i = 0; i <= text.length() - n; i++) {
				if (!(i >= vectorHashCount)) { // Don't wanna go out of our set bounds...
					CharSequence ngram = text.substring(i, i + n);
					index = ngram.hashCode() % vectorNgram.length;
					vectorNgram[index]++;
				}
			}

			/* Normaise vector hashes between -0.5 and 0.5 */
			vectorNgram = Utilities.normalize(vectorNgram, -0.5, 0.5);

			/* Add normalised vectors to csv */
			for (i = 0; i < vectorNgram.length; i++)
				builder.append(vectorNgram[i] + ",");

			builder.setLength(builder.length() - 1); // Remove final comma at end of file
			return builder.toString();
		} catch (

		Exception e) {
			System.out.println("[ERROR] -> " + e);
		}
		return builder.toString(); // This will cause blank lines in csv file
	}

	public double[] getVectorNgram() {
		return vectorNgram;
	}

	public void setVectorNgram(double[] vectorNgram) {
		this.vectorNgram = vectorNgram;
	}
}
