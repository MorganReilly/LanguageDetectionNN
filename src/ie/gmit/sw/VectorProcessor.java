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
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(WILI_11750_SMALL))));
			while ((line = reader.readLine()) != null) {
				process(this.ngramSize, line, writer);
			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			reader.close();
		}
	}

	public void process(int n, String line, PrintWriter writer) {
		String[] record;
		String text, lang;
		int index, i;
		try {
			record = line.split("@");
			if (record.length > 2)
				return; // Bail out if 2 @ symbols -- Dodgy text
			text = record[0].toLowerCase();
			lang = record[1]; // Language from wili
			for (i = 0; i < vectorNgram.length; i++)
				vectorNgram[i] = 0; // Initialise Vector
			for (i = 0; i <= text.length() - n; i++) {
				CharSequence ngram = text.substring(i, i + n);
				index = ngram.hashCode() % vectorNgram.length;
//				System.out.println("vectorHashedNgram[" + i + "] before increment: " + vectorNgram[index]);
				vectorNgram[index]++; // TODO: What is?
//				System.out.println("vectorHashedNgram[" + i + "] after  increment: " + vectorNgram[index]);
			}
			Utilities.normalize(vectorNgram, -0.5, 0.5);

			StringBuilder builder = new StringBuilder();
			for (i = 0; i < vectorNgram.length; i++) {
				if (i % vectorHashCount == 0) {
					builder.append("\n");
				}
				builder.append(vectorNgram[i]);
				writer.print(builder.toString());
			}
		} catch (Exception e) {
			System.out.println("[ERROR] -> " + e);
		} finally {
			writer.close();
		}
	}

	@Override
	public String toString() {
		return "VectorProcessor [vectorHashCount=" + vectorHashCount + ", ngram=" + ngramSize + ", vectorHashedNgram="
				+ Arrays.toString(vectorNgram) + ", decimalFormat=" + decimalFormat + ", languages="
				+ Arrays.toString(languages) + "]";
	}

//	public static void main(String[] args) throws IOException {
//		new VectorProcessor().go();
//	}

}
