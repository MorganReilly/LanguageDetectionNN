package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;

public class LanguageClassifier {
	private final String userInputFile = "./userInFile.txt";
	private final File DATA_FILE = new File("userGenData.csv");
	private int vectorHashCount, ngramSize;
	private double[] vectorNgram;
	private DecimalFormat decimalFormat = new DecimalFormat("###.###");

	/*
	 * Language Classifier
	 * 
	 * Have this class detect the user input? This class should be merged with the
	 * vector processor?
	 */
	public LanguageClassifier(int vectorHashCount, int n) {
		this.vectorHashCount = vectorHashCount;
		this.vectorNgram = new double[vectorHashCount];
		this.ngramSize = n;
	}

	// Generate from file
	public void generateFromFile() throws IOException {
		PrintWriter writer = new PrintWriter(DATA_FILE, "UTF-8");
		BufferedReader reader = null;
		String line = null, toFile = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(userInputFile))));
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

	// Generate from console
	public void generateFromConsole() {

	}
}
