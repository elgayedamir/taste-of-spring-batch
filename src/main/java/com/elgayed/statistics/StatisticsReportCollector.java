package com.elgayed.statistics;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import com.elgayed.model.Speech;
import com.elgayed.model.StatisticsReport;

/**
 * Collects statistics from a stream of speeches:
 * It uses 3 internal Map data structures as data accumulators for this collector
 * <ul>
 * <li>Words per speaker</li>
 * <li>Speeches in 2013 per speaker</li>
 * <li>Speeches about internal security theme per speaker</li>
 * </ul>
 * 
 * This collectors finilizer returns an {@link StatisticsReport} instance holding information about:
 * least wordy speaker, the speaker with most speeches in 2013, and the speaker with most speeches about internal security
 */
public class StatisticsReportCollector implements Collector<Speech, StatisticsReportAccumulator, StatisticsReport> {
	
	@Override
	public Supplier<StatisticsReportAccumulator> supplier() {
		return StatisticsReportAccumulator::new;
	}

	@Override
	public BiConsumer<StatisticsReportAccumulator, Speech> accumulator() {
		return StatisticsReportAccumulator::accumulate;
	}
	
	//TODO if this collector is to be used in a parallel stream this implementation should be changed
	//to merge accumulation maps from two different instants
	@Override
	public BinaryOperator<StatisticsReportAccumulator> combiner() {
		return (accumulator, other) -> accumulator;
	}

	@Override
	public Function<StatisticsReportAccumulator, StatisticsReport> finisher() {
		return StatisticsReportAccumulator::toStatisticsReport;
	}

	@Override
	public Set<Characteristics> characteristics() {
		return Set.of(
				Characteristics.UNORDERED, 
				Characteristics.CONCURRENT);
	}
	
}
