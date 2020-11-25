package com.elgayed.statistics;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import com.elgayed.model.Speech;
import com.elgayed.model.StatisticsReport;

public class StatisticsReportAccumulator {
	
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
	 * Holds the name of the speaker with least words in his speeches
	 */
	private Optional<String> leastWordySpeaker = Optional.empty();
	
	/**
	 * Holds the name of the speaker with most speeches in 2013
	 */
	private Optional<String> speakerWithMostSpeechesIn2013 = Optional.empty();
	
	/**
	 * Holds the name of the speaker with most speeches about security
	 */
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
		Optional<Pair<String, Long>> speakerUpdatedSecuritySpeechesCount = updateInternalSecuritySpeechesPerSpeeker(speech);
		speakerUpdatedSecuritySpeechesCount.ifPresent(
				update -> updateSpeakerWithMostSecuritySpeeches(update.getLeft(), update.getRight())
				);
		
		Optional<Pair<String, Long>> speakerUpdated2013SpeechesCount = updateSpeechIn2013PerSpeeker(speech);
		speakerUpdated2013SpeechesCount.ifPresent(
				update -> updateSpeakerWithMostSpeechesIn2013(update.getLeft(), update.getRight())
				);
		
//		Pair<String, Long> speakerUpdatedWordsCount = updateWordsPerSpeeker(speech);
//		updateLeastWordySpeaker(speakerUpdatedWordsCount.getLeft(), speakerUpdatedWordsCount.getRight());
		updateLeastWordyFunction.apply(speech);
	}
	
/*
 * For the sake of experimentation: 
 * Implementing the update of leastWordy in a functional style: 
 * A composed function that updates wordsPerSpeakerMap then updates leastWordy field 
 * 
 * This function is not pure: it neither have the same return value for the same input and 
 * it does have side effects (updating object level variables) so it might be debatable to keep 
 * the implementation in OOP paradigm via message passing (method invocation in java)
 * like for updating speakerWithMostSpeechesIn2013 and speakerWithMostSecuritySpeeches
 */
	private Function<Speech, Pair<String, Long>> updateWordsPerSpeekerFunction = speech -> {
			Long mergedValue = wordsPerSpeaker.merge(speech.getSpeaker(), speech.getWords(), Long::sum);
			return Pair.of(speech.getSpeaker(), mergedValue);
		};
	
	private Function<Speech, String> updateLeastWordyFunction = updateWordsPerSpeekerFunction.andThen(
			updatedWordsCount -> {
				String speaker = updatedWordsCount.getLeft();
				Long wordsCount = updatedWordsCount.getRight();
				leastWordySpeaker.ifPresentOrElse(
						currentLeastWordy -> {
							Long currentLeastWordyCount = wordsPerSpeaker.get(currentLeastWordy);
							if (wordsCount <= currentLeastWordyCount) leastWordySpeaker = Optional.of(speaker);
						},
						() -> {
							leastWordySpeaker = Optional.of(speaker);
						});
				return leastWordySpeaker.get();
			}
		);
	
	/**
	 * Given a speech, this method updates {@link #wordsPerSpeaker} accumulation map, by summing
	 * the given speech words count for the giving speaker 
	 * @param speech Speech Object
	 * @return a {@link Pair} holding in left the speaker's name and in right his total words count
	 */
	@SuppressWarnings("unused")
	private Pair<String, Long> updateWordsPerSpeeker (Speech speech) {
		String speaker = speech.getSpeaker();
		Long mergedValue = wordsPerSpeaker.merge(speaker, speech.getWords(), Long::sum);
		return Pair.of(speaker, mergedValue);
	}
	
	/**
	 * Given a speaker and his total words count, updates {@link #leastWordySpeaker} field by comparing existing and 
	 * new given values
	 * @param speaker Speaker's name
	 * @param wordsCount Speaker's total words count
	 */
	@SuppressWarnings("unused")
	private void updateLeastWordySpeaker (String speaker, Long wordsCount) {
		leastWordySpeaker.ifPresentOrElse(
				currentLeastWordy -> {
					Long currentLeastWordyWordsCount = wordsPerSpeaker.get(currentLeastWordy);
					if (wordsCount <= currentLeastWordyWordsCount) leastWordySpeaker = Optional.of(speaker);
				},
				() -> {
					leastWordySpeaker = Optional.of(speaker);
				});
	}
	
	/**
	 * Given a speech, this method updates {@link #speechesIn2013PerSpeaker} accumulation map, by 
	 * incrementing the count of speeches in 2013 for the giving speaker 
	 * @param speech Speech Object
	 * @return an optional {@link Pair} holding in left the speaker's name and in right the total count
	 * of his speeches in 2013
	 */
	private Optional<Pair<String, Long>> updateSpeechIn2013PerSpeeker (Speech speech) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(speech.getDate());
		if ( calendar.get(Calendar.YEAR) == StatisticsReportConstants.MOST_SPEECHES_YEAR) {
			Long mergedValue = speechesIn2013PerSpeaker.merge(speech.getSpeaker(), Long.valueOf(1), Long::sum);
			return Optional.of(Pair.of(speech.getSpeaker(), mergedValue));
		}
		return Optional.empty();
	}
	
	/**
	 * Given a speaker and the count of his speeches in 2013, updates {@link #speakerWithMostSpeechesIn2013} field by comparing existing and 
	 * new given values
	 * @param speaker Speaker's name
	 * @param wordsCount Speaker's count of speeches given in 2013
	 */
	private void updateSpeakerWithMostSpeechesIn2013 (String speaker, Long speechesCountIn2013) {
		speakerWithMostSpeechesIn2013.ifPresentOrElse(
				currentSpeakerWithMostSpeechesIn2013 -> {
					Long currentMostSpeechesIn2013Count = speechesIn2013PerSpeaker.get(currentSpeakerWithMostSpeechesIn2013);
					if (speechesCountIn2013 >= currentMostSpeechesIn2013Count) speakerWithMostSpeechesIn2013 = Optional.of(speaker);
				},
				() -> {
					speakerWithMostSpeechesIn2013 = Optional.of(speaker);
				});
	}
	
	/**
	 * Given a speech, this method updates {@link #internalSecuritySpeechesPerSpeaker} accumulation map, by 
	 * incrementing the count of internal security speeches for the giving speaker 
	 * @param speech Speech Object
	 * @return an optional {@link Pair} holding in left the speaker's name and in right the total count
	 * of his internal security speeches
	 */
	private Optional<Pair<String, Long>> updateInternalSecuritySpeechesPerSpeeker (Speech speech) {
		if (StatisticsReportConstants.INTERNAL_SECURITY_THEME.equals(speech.getTheme())) {
			Long mergedValue = internalSecuritySpeechesPerSpeaker.merge(speech.getSpeaker(), Long.valueOf(1), Long::sum);
			return Optional.of(Pair.of(speech.getSpeaker(), mergedValue));
		}
		return Optional.empty();
	}
	
	/**
	 * Given a speaker and the count of his speeches about internal security, updates {@link #speakerWithMostSecuritySpeeches} field by comparing existing and 
	 * new given values
	 * @param speaker Speaker's name
	 * @param wordsCount Speaker's count of speeches about internal security
	 */
	private void updateSpeakerWithMostSecuritySpeeches(String speaker, Long securitySpeechesCount) {
		speakerWithMostSecuritySpeeches.ifPresentOrElse(
				currentSpeakerWithMostSecuritySpeeches -> {
					Long currentMostSecuritySpeechesCount = internalSecuritySpeechesPerSpeaker.get(currentSpeakerWithMostSecuritySpeeches);
					if (securitySpeechesCount >= currentMostSecuritySpeechesCount) speakerWithMostSecuritySpeeches = Optional.of(speaker);
				},
				() -> {
					speakerWithMostSecuritySpeeches = Optional.of(speaker);
				});
	}
	
	public StatisticsReport toStatisticsReport () {
		return new StatisticsReport(
				speakerWithMostSpeechesIn2013.orElse(StatisticsReportConstants.NO_CLEAR_ANSWER), 
				speakerWithMostSecuritySpeeches.orElse(StatisticsReportConstants.NO_CLEAR_ANSWER),
				leastWordySpeaker.orElse(StatisticsReportConstants.NO_CLEAR_ANSWER));
		
	}
	
}
