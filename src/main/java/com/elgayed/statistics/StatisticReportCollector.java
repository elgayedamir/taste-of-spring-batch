package com.elgayed.statistics;

import java.util.Comparator;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
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
public class StatisticReportCollector implements Collector<Speech, StatisticReportAccumulator, StatisticReport> {
	
	@Override
	public Supplier<StatisticReportAccumulator> supplier() {
		return StatisticReportAccumulator::new;
	}

	@Override
	public BiConsumer<StatisticReportAccumulator, Speech> accumulator() {
		return StatisticReportAccumulator::accumulate;
	}
	
	//TODO if this collector is to be used in a parallel stream this implementation should be changed
	//to merge accumulation maps from two different instants
	@Override
	public BinaryOperator<StatisticReportAccumulator> combiner() {
		return (accumulator, other) -> accumulator;
	}

	@Override
	public Function<StatisticReportAccumulator, StatisticReport> finisher() {
		return accumulator -> {
			//TODO update each of these fields in the accumulator each time when item is accepted
			String leastWordy = accumulator.getWordsPerSpeaker()
					.entrySet()
					.stream()
					.min(Comparator.comparing(Entry::getValue))
					.map(Entry::getKey)
					.orElse(StatisticReportConstants.NO_CLEAR_ANSWER);
			
			String mostSecurity = accumulator.getInternalSecuritySpeechesPerSpeaker()
					.entrySet()
					.stream()
					.max(Comparator.comparing(Entry::getValue))
					.map(Entry::getKey)
					.orElse(StatisticReportConstants.NO_CLEAR_ANSWER);
			
			String mostSpeeches = accumulator.getSpeechesIn2013PerSpeaker()
				.entrySet()
				.stream()
				.max(Comparator.comparing(Entry::getValue))
				.map(Entry::getKey)
				.orElse(StatisticReportConstants.NO_CLEAR_ANSWER);
			
			return new StatisticReport(mostSpeeches, mostSecurity, leastWordy);
		};
	}

	@Override
	public Set<Characteristics> characteristics() {
		return Set.of(
				Characteristics.UNORDERED, 
				Characteristics.CONCURRENT);
	}
	
}
