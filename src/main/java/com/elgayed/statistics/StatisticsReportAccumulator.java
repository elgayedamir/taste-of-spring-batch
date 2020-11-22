package com.elgayed.statistics;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.elgayed.model.Speech;
import com.elgayed.model.StatisticReport;

public class StatisticsReportAccumulator {
	
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
	
	private Optional<String> leastWordySpeaker = Optional.empty();
	private Optional<String> speakerWithMostSpeechesIn2013 = Optional.empty();
	private Optional<String> speakerWithMostSecuritySpeeches = Optional.empty();
	
	
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
	
	private void updateWordsPerSpeeker (Speech speech) {
		Long mergedValue = wordsPerSpeaker.merge(speech.getSpeaker(), speech.getWords(), Long::sum);
		leastWordySpeaker.ifPresentOrElse(
				speaker -> {
					Long totalWords = wordsPerSpeaker.get(speaker);
					if (mergedValue <= totalWords) leastWordySpeaker = Optional.of(speech.getSpeaker());
				},
				() -> {
					leastWordySpeaker = Optional.of(speech.getSpeaker());
				});
	}
	
	private void updateSpeechIn2013PerSpeeker (Speech speech) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(speech.getDate());
		if ( calendar.get(Calendar.YEAR) == StatisticsReportConstants.MOST_SPEECHES_YEAR) {
			Long mergedValue = speechesIn2013PerSpeaker.merge(speech.getSpeaker(), Long.valueOf(1), Long::sum);
			speakerWithMostSpeechesIn2013.ifPresentOrElse(
					speaker -> {
						Long totalSpeechesIn2013 = speechesIn2013PerSpeaker.get(speaker);
						if (mergedValue >= totalSpeechesIn2013) speakerWithMostSpeechesIn2013 = Optional.of(speech.getSpeaker());
					},
					() -> {
						speakerWithMostSpeechesIn2013 = Optional.of(speech.getSpeaker());
					});
		}	
	}
	
	private void updateInternalSecuritySpeechesPerSpeeker (Speech speech) {
		if (StatisticsReportConstants.INTERNAL_SECURITY_THEME.equals(speech.getTheme())) {
			Long mergedValue = internalSecuritySpeechesPerSpeaker.merge(speech.getSpeaker(), Long.valueOf(1), Long::sum);
			speakerWithMostSecuritySpeeches.ifPresentOrElse(
					speaker -> {
						Long totalSecuritySpeeches = internalSecuritySpeechesPerSpeaker.get(speaker);
						if (mergedValue >= totalSecuritySpeeches) speakerWithMostSecuritySpeeches = Optional.of(speech.getSpeaker());
					},
					() -> {
						speakerWithMostSecuritySpeeches = Optional.of(speech.getSpeaker());
					});
		}
	}
	
	public StatisticReport toStatisticReport () {
		return new StatisticReport(
				speakerWithMostSpeechesIn2013.orElse(StatisticsReportConstants.NO_CLEAR_ANSWER), 
				speakerWithMostSecuritySpeeches.orElse(StatisticsReportConstants.NO_CLEAR_ANSWER),
				leastWordySpeaker.orElse(StatisticsReportConstants.NO_CLEAR_ANSWER));
		
	}
	
}
