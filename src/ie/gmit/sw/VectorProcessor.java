package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

/*
 * this class will read in line by line of the wili dataset 
 * convert it to a vector hash
 * save into a csv for the nn to read and use for training data? 
 */
public class VectorProcessor {

	private double[] vector = new double[100]; // TODO: Make flexible
	private DecimalFormat df = new DecimalFormat("###.###"); // Decimal format to 3 Places of precision
	private int n = 4; // Number of ngrams
//	private Language[] langs... //TODO: Get from runner
	
	public void go() {
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("./wili-2018-Small-11750-Edited"))))){
			String line = null;
			
			// Read line by line through wili data set
			while(br.readLine() != null) {
				// Process the line
				// Keep track of numbers? 
				process(line);
			}
		} catch(Exception e) {
			
		}
	}
	
	public void process(String line) {
		String[] record = line.split("@");
		
		if (record.length > 2) return; // Bail out if 2 @ symbols -- Dodgy text
		
		// Convert to lowercase
		String text = record[0].toLowerCase(); // Text from wili
		String lang = record[1]; // Language from wili
		
		for (int i =0; i < vector.length; i++) vector[i] = 0; // Initialise Vector
		
		// Psuedo code
		// Loop over "text":
			// For each ngram, do:
				// Compute: index = ngram.hashCode() % vector.length
			// vector[index]++;
		
		// Normalise vectors between -1 and 1
		Utilities.normalize(vector, -1, 1);
		
		// Write out vector to CSV file using df.format(number); for each vector index...
		// Write out the language numbers to the same row in csv file
		
		// Each row will have:
		// vector.length + number of labels (235 -> one for each language)
	}
	
}
