package com.elgayed.processing;

import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.elgayed.model.Speech;
import com.elgayed.model.StatisticReport;
import com.elgayed.statistics.StatisticsReportCollector;

@Component
@JobScope
public class StatisticReportWriter implements ItemWriter<Speech> {
	
	public static final String STATISTIC_REPORT_KEY = "statistic_report";
	
	private JobExecution jobExecution;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        jobExecution = stepExecution.getJobExecution();
    }
	
	@Override
	public void write(List<? extends Speech> items) throws Exception {
		//Collects StatisticReport using StatisticReportCollector
		StatisticReport statisticReport = items.stream().collect(new StatisticsReportCollector());
		jobExecution.getExecutionContext().put(STATISTIC_REPORT_KEY, statisticReport);
	}
}
