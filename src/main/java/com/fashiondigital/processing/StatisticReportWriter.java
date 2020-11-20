package com.fashiondigital.processing;

import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.fashiondigital.model.Speech;
import com.fashiondigital.model.StatisticReport;
import com.fashiondigital.statistics.StatisticReportCollector;

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
		StatisticReport statisticReport = items.stream().collect(StatisticReportCollector.newCollector());
		jobExecution.getExecutionContext().put(STATISTIC_REPORT_KEY, statisticReport);
	}
}
