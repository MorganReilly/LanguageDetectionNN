package ie.gmit.sw;

import java.util.*;

/**
 * LANGUAGE DATABASE
 * 
 * This class is used for building a map(database) of all 235 languages.
 * It also is used for resizing, sorting, and comparing distance metrics between maps.
 * 
 * @author John Healy
 * @author Morgan Reilly
 */
public class Database {
	/**
	 * 235 langauges Maps languages to ngram & frequency of occurance.
	 * NOTE: Don't have to use a Treemap
	 */
	private Map<Language, Map<Integer, LanguageEntry>> languageDB = new TreeMap<>();

	/**
	 * ADD
	 * Sort by frequency
	 * Add to database
	 * @param s
	 * @param lang
	 */
	public void add(CharSequence s, Language lang) {
		int kmer = s.hashCode(); // Convert string into hashcode -- int
		/*
		 * Get a handle on Language class Get associated language map
		 */
		Map<Integer, LanguageEntry> langDb = getLanguageEntries(lang); // Map int to language entry

		// Set initial frequency
		int frequency = 1;
		// If db already contains language, add it's frequency to the initial
		if (langDb.containsKey(kmer)) {
			// Increment frequency
			frequency += langDb.get(kmer).getFrequency();
		}
		// Override existing kmer, with new language kmer and frequency
		langDb.put(kmer, new LanguageEntry(kmer, frequency));
	}

	/**
	 * GET LANGUAGE ENTRIES
	 * Gets a handle on all language entries
	 * 
	 * @param lang
	 * @return a map of langdb
	 */
	private Map<Integer, LanguageEntry> getLanguageEntries(Language lang) {
		Map<Integer, LanguageEntry> langDb = null;
		if (languageDB.containsKey(lang)) {
			langDb = languageDB.get(lang);
		} else {
			langDb = new TreeMap<Integer, LanguageEntry>();
			languageDB.put(lang, langDb);
		}
		return langDb;
	}

	/**
	 * RESIZE
	 * Resise the database to only a specific amount of kmer entries
	 * Using: 300 kmers, optimal: 400 kmers
	 * 
	 * @param max
	 */
	public void resize(int max) {
		Set<Language> keys = languageDB.keySet(); // Get all of the langages
		// For each language
		// Give mapping of ints to language being changed, then reinsert into map
		for (Language lang : keys) {
			Map<Integer, LanguageEntry> top = getTop(max, lang);
			languageDB.put(lang, top);
		}
	}

	/**
	 * GET TOP
	 * Get the top(most frequent) amount of kmer entries
	 * 
	 * @param max
	 * @param lang
	 * @return temp map -- sorted based on frequency
	 */
	public Map<Integer, LanguageEntry> getTop(int max, Language lang) {
		// create temporary map
		Map<Integer, LanguageEntry> temp = new TreeMap<>();
		// Set of frqeuncies for main map
		List<LanguageEntry> languageEntries = new ArrayList<>(languageDB.get(lang).values());
		Collections.sort(languageEntries);

		int rank = 1; // Initial rank to 1
		for (LanguageEntry languageEntry : languageEntries) {
			// Very first read will be highest ranking
			languageEntry.setRank(rank); // Set kmer to highest ranking (1)
			temp.put(languageEntry.getKmer(), languageEntry); // Put the new highest into temporary map
			if (rank == max)
				break;
			rank++;
		}
		return temp;
	}

	/**
	 * GET LANGUAGE
	 * Returns the language based on distance metrics
	 * 
	 * @param query
	 * @return the language guessed
	 */
	public Language getLanguage(Map<Integer, LanguageEntry> query) {
		/**
		 * Ordered Treeset
		 */
		TreeSet<OutOfPlaceMetric> outOfPlaceMetric = new TreeSet<>();

		/**
		 * For each language in map: Add to sorted tree set new outofplacemetic with
		 * language name, new query and query langage name
		 * 
		 * Compares query to database
		 */
		Set<Language> langs = languageDB.keySet();
		for (Language lang : langs) {
			// Add to sorted treeset: new outofplacemetric with language name and
			// outofplacedistance
			outOfPlaceMetric.add(new OutOfPlaceMetric(lang, getOutOfPlaceDistance(query, languageDB.get(lang))));
		}
		return outOfPlaceMetric.first().getLanguage();
	}

	/**
	 * GET OUT OF PLACE DISTANCE
	 * Handles the distance between kmer entries in both maps
	 * 
	 * @param query
	 * @param subject
	 * @return distance between kmers in maps
	 */
	private int getOutOfPlaceDistance(Map<Integer, LanguageEntry> query, Map<Integer, LanguageEntry> subject) {
		int distance = 0; // Set distance to 0

		// Sorted tree set
		// For each language entry
		Set<LanguageEntry> languageEntries = new TreeSet<>(query.values()); // Sorted language entries
		/**
		 * For each entries Get language: german, chinese... If doesn't exist: Set
		 * distance to be furthest distance If it does exist: Set subject rank - query
		 * rank q - Query s- Subject
		 */
		for (LanguageEntry q : languageEntries) {
			LanguageEntry s = subject.get(q.getKmer());
			if (s == null) {
				distance += subject.size() + 1;
			} else {
				distance += s.getRank() - q.getRank();
			}
		}
		return distance;
	}

	/**
	 * OUT OF PLACE METRIC
	 * Inner class
	 * 
	 * For a language it shows how out of place. Distance between language and
	 * query.
	 * 
	 * @author John Healy
	 * @author Morgan Reilly
	 */
	private class OutOfPlaceMetric implements Comparable<OutOfPlaceMetric> {
		private Language lang;
		private int distance;

		/**
		 * 2 Parameter Constructor
		 * @param lang
		 * @param distance
		 */
		public OutOfPlaceMetric(Language lang, int distance) {
			super();
			this.lang = lang;
			this.distance = distance;
		}

		/**
		 * GET LANGUAGE
		 * Gets a language
		 * @return language
		 */
		public Language getLanguage() {
			return lang;
		}

		/**
		 * GET ABSOLUTE DISTANCE
		 * 
		 * abs --> Gets rid of minus sign
		 * 
		 * @return absolute distance
		 */
		public int getAbsoluteDistance() {
			return Math.abs(distance);
		}

		/**
		 * COMPARE TO
		 * 
		 * Closest one to 0 will be top of list Sorts it in ascending order
		 * @return compared distances between maps
		 */
		@Override
		public int compareTo(OutOfPlaceMetric o) {
			return Integer.compare(this.getAbsoluteDistance(), o.getAbsoluteDistance());
		}
		
		@Override
		public String toString() {
			return "[lang=" + lang + ", distance=" + getAbsoluteDistance() + "]";
		}
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		int langCount = 0;
		int kmerCount = 0;
		Set<Language> keys = languageDB.keySet();
		for (Language lang : keys) {
			langCount++;
			sb.append(lang.name() + "->\n");

			Collection<LanguageEntry> m = new TreeSet<>(languageDB.get(lang).values());
			kmerCount += m.size();
			for (LanguageEntry le : m) {
				sb.append("\t" + le + "\n");
			}
		}
		sb.append(kmerCount + " total k-mers in " + langCount + " languages");
		return sb.toString();
	}
}