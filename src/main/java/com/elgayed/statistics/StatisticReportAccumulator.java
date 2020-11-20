package com.elgayed.statistics;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.elgayed.model.Speech;

public class StatisticReportAccumulator {
	
	//TODO is concurrent map required if this is used in a // stream
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
	
	
	public Map<String, Long> getWordsPerSpeaker() {
		return wordsPerSpeaker;
	}

	public Map<String, Long> getSpeechesIn2013PerSpeaker() {
		return speechesIn2013PerSpeaker;
	}

	public Map<String, Long> getInternalSecuritySpeechesPerSpeaker() {
		return internalSecuritySpeechesPerSpeaker;
	}

	public void accumulate (Speech speech) {
		updateInternalSecuritySpeechesPerSpeeker(speech);
		updateSpeechIn2013PerSpeeker(speech);
		updateWordsPerSpeeker(speech);
	}
	
	public void updateWordsPerSpeeker (Speech speech) {
		wordsPerSpeaker.merge(speech.getSpeaker(), speech.getWords(), Long::sum);
	}
	
	public void updateSpeechIn2013PerSpeeker (Speech speech) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(speech.getDate());
		if ( calendar.get(Calendar.YEAR) == StatisticReportConstants.MOST_SPEECHES_YEAR)
			speechesIn2013PerSpeaker.merge(speech.getSpeaker(), Long.valueOf(1), Long::sum);
	}
	
	public void updateInternalSecuritySpeechesPerSpeeker (Speech speech) {
		if (StatisticReportConstants.INTERNAL_SECURITY_THEME.equals(speech.getTheme()))
			internalSecuritySpeechesPerSpeaker.merge(speech.getSpeaker(), Long.valueOf(1), Long::sum);
	}
}
