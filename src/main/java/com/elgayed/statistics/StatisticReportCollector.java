package com.elgayed.statistics;

import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collector;

import com.elgayed.model.Speech;
import com.elgayed.model.StatisticReport;

/**
 * Collects statistics from a stream of speeches:
 * It uses 3 internal Map data structures as data accumulators for this collector
 * <ul>
 * <li>Words per speaker</li>
 * <li>Speeches in 2013 per speaker</li>
 * <li>Speeches about internal security theme per speaker</li>
 * </ul>
 * 
 * This collectors finilizer returns an {@link StatisticReport} instance holding information about:
 * least wordy speaker, the speaker with most speeches in 2013, and the speaker with most speeches about internal security
 */
public class StatisticReportCollector implements Consumer<Speech> {
	//Alternatively, the following 3 maps could be in a separate class that serves as the collector accumulator, 
	//currently this class provides data container for accumulation as well
	/**
	 * Map for accumulating words per speaker
	 * <ul>
	 * <li>Key: speaker name</li>
	 * <li>Value: number of words in overall</li>
	 * </ul>
	 */
	private Map<String, Long> wordsPerSpeaker = new HashMap<>();
	/**
	 * Map for accumulating speeches in 2013 per speaker
	 * <ul>
	 * <li>Key: speaker's name</li>
	 * <li>Value: number of speeches given in 2013</li>
	 * </ul>
	 */
	private Map<String, Long> speechesIn2013PerSpeaker = new HashMap<>();
	/**
	 * Map for accumulating internal security speeches per speaker
	 * <ul>
	 * <li>Key: speaker's name</li>
	 * <li>Value: number of speeches about the theme of internal security</li>
	 * <ul>
	 */
	private Map<String, Long> internalSecuritySpeechesPerSpeaker = new HashMap<>();
	
	/**
	 * Static factory method that instantiated a {@code StatisticReportCollector}
	 * @return a collector that accepts speeches and returns a {@link StatisticReport}
	 */
	public static Collector<Speech, StatisticReportCollector, StatisticReport> newCollector() {
        return Collector.of(
        		StatisticReportCollector::new, 
        		StatisticReportCollector::accept,
        		StatisticReportCollector::combine, 
        		StatisticReportCollector::finilize);
    }
	
	@Override
	public void accept(Speech speech) {
		updateInternalSecuritySpeechesPerSpeeker(speech);
		updateWordsPerSpeeker(speech);
		updateSpeechIn2013PerSpeeker(speech);
	}
	
	//TODO if this collector is to be used in a parallel stream this implementation should be changed
	//to merge accumulation maps from two different instants
	public StatisticReportCollector combine(StatisticReportCollector other) {
        return this;
    }
	
	public StatisticReport finilize() {
		String leastWordy = wordsPerSpeaker
				.entrySet()
				.stream()
				.min(Comparator.comparing(Entry::getValue))
				.map(Entry::getKey)
				.orElse(StatisticReportConstants.NO_CLEAR_ANSWER);
		
		String mostSecurity = internalSecuritySpeechesPerSpeaker
				.entrySet()
				.stream()
				.max(Comparator.comparing(Entry::getValue))
				.map(Entry::getKey)
				.orElse(StatisticReportConstants.NO_CLEAR_ANSWER);
		
		String mostSpeeches = speechesIn2013PerSpeaker
			.entrySet()
			.stream()
			.max(Comparator.comparing(Entry::getValue))
			.map(Entry::getKey)
			.orElse(StatisticReportConstants.NO_CLEAR_ANSWER);
		
		return new StatisticReport(mostSpeeches, mostSecurity, leastWordy);
	}
	
	private void updateWordsPerSpeeker (Speech speech) {
		wordsPerSpeaker.merge(speech.getSpeaker(), speech.getWords(), Long::sum);
	}
	
	private void updateSpeechIn2013PerSpeeker (Speech speech) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(speech.getDate());
		if ( calendar.get(Calendar.YEAR) == StatisticReportConstants.MOST_SPEECHES_YEAR)
			speechesIn2013PerSpeaker.merge(speech.getSpeaker(), Long.valueOf(1), Long::sum);
	}
	
	private void updateInternalSecuritySpeechesPerSpeeker (Speech speech) {
		if (StatisticReportConstants.INTERNAL_SECURITY_THEME.equals(speech.getTheme()))
			internalSecuritySpeechesPerSpeaker.merge(speech.getSpeaker(), Long.valueOf(1), Long::sum);
	}
	
}
