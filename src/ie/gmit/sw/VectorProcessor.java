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
 * this class will read in line by line of the wili dataset 
 * convert it to a vector hash
 * save into a csv for the nn to read and use for training data? 
 */
public class VectorProcessor {
	private final String WILI_11750_SMALL = "./wili-2018-Small-11750-Edited.txt";
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
		PrintWriter writer = new PrintWriter("data.csv", "UTF-8");
		BufferedReader reader = null;
		String line = null;
		int count;
		try {
			count = 0;
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(WILI_11750_SMALL))));
			while ((line = reader.readLine()) != null) {
				process(this.ngramSize, line, writer);
				count++;
			}
			System.out.println("count: " + count);
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			reader.close();
		}
	}

	public double[] process(int n, String line, PrintWriter writer) {
		StringBuilder builder = new StringBuilder();
		String[] record;
		String text, lang;
		int index, i;
		try {
			/* Split Line */
			record = line.split("@");

//			/* Handle Bad Text */
//			if (record.length > 2)
//				return; // Bail out if 2 @ symbols -- Dodgy text

			/* Store split text and langauges */
			text = record[0].toLowerCase();
			lang = record[1]; // Language from wili
//			builder.append(lang + ",");

			/* Initialise Vectors */
			for (i = 0; i < vectorNgram.length; i++)
				vectorNgram[i] = 0; // Initialise Vector

			/* Generate Vector Hash */
			for (i = 0; i <= text.length() - n; i++) {
				if (!(i >= vectorHashCount)) { // Don't wanna go out of our set bounds...
					CharSequence ngram = text.substring(i, i + n);
					index = ngram.hashCode() % vectorNgram.length;
					vectorNgram[i] = index;
					System.out.println("vectorNgram[" + i + "]" + vectorNgram[i]);
				}
			}
			/* Normaise vector hashes between -0.5 and 0.5 */
			vectorNgram = Utilities.normalize(vectorNgram, -0.5, 0.5);

			builder.append(vectorNgram + ",");
//			System.out.println(builder.toString());
			return vectorNgram;
		} catch (Exception e) {
			System.out.println("[ERROR] -> " + e);
		} finally {
			writer.print(builder.toString());
			writer.close();
			System.out.println("vectorNgram.length: " + vectorNgram.length);
		}
		return vectorNgram;
	}

	@Override
	public String toString() {
		return "VectorProcessor [vectorHashCount=" + vectorHashCount + ", ngram=" + ngramSize + ", vectorHashedNgram="
				+ Arrays.toString(vectorNgram) + ", decimalFormat=" + decimalFormat + ", languages="
				+ Arrays.toString(languages) + "]";
	}
}
