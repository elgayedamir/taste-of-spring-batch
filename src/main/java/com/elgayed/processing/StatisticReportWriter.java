package com.elgayed.processing;

import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.elgayed.model.Speech;
import com.elgayed.model.StatisticsReport;
import com.elgayed.statistics.StatisticsReportCollector;
import com.elgayed.statistics.StatisticsReportConstants;

@Component
@JobScope
public class StatisticReportWriter implements ItemWriter<Speech> {
	
	public static final String STATISTIC_REPORT_KEY = "statistic_report";
	
	private JobExecution jobExecution;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        jobExecution = stepExecution.getJobExecution();
        jobExecution.getExecutionContext().put(STATISTIC_REPORT_KEY, new StatisticsReport(StatisticsReportConstants.NO_CLEAR_ANSWER, StatisticsReportConstants.NO_CLEAR_ANSWER, StatisticsReportConstants.NO_CLEAR_ANSWER));
    }
	
	@Override
	public void write(List<? extends Speech> items) throws Exception {
		//Collects StatisticsReport using StatisticsReportCollector
		StatisticsReport statisticsReport = items.stream().collect(new StatisticsReportCollector());
		jobExecution.getExecutionContext().put(STATISTIC_REPORT_KEY, statisticsReport);
	}
}
