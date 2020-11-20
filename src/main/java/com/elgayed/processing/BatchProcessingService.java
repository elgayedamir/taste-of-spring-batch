package com.elgayed.processing;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elgayed.model.Speech;
import com.elgayed.model.StatisticReport;
import com.elgayed.statistics.StatisticReportCollector;
import com.elgayed.statistics.StatisticReportConstants;

@Service
public class BatchProcessingService {
	
	@Autowired
	private JobLauncher jobLauncher;
	@Autowired
	private Job job;
	
	/**
	 * Given CSV File URLs, it loads the CSV files and extracts statistics, a batch processing job is launched for the task
	 * The batch processing job has one step: 
	 * <ol>
	 * <li>Item reader: Reads all CSV Files using a {@link MultiResourceItemReader} (delegates reading each CSV file to a {@link FlatFileItemWriter}) and produces a {@link Speech} per line
	 * <li>Item writer: streams speeches and uses {@link StatisticReportCollector} to derive a {@link StatisticReport}
	 * </ol>
	 * @param csvFileUrls URLs of the CSV files to be processed
	 * @return {@link StatisticReport} containing stats derived from speeches read the given CSV File URLs
	 */
	public StatisticReport processCsvFiles (Map<String, String> csvFileUrls) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		JobParameters jobParams = new JobParameters(
				csvFileUrls.entrySet().stream().collect(
						Collectors.toMap(Map.Entry::getKey, entry -> new JobParameter(entry.getValue().toString()))
					)
		);
		JobExecution jobExecution = jobLauncher.run(job, jobParams);
		StatisticReport statisticReport = (StatisticReport) jobExecution.getExecutionContext().get(StatisticReportWriter.STATISTIC_REPORT_KEY);
		if (statisticReport == null)
			statisticReport = new StatisticReport(StatisticReportConstants.NO_CLEAR_ANSWER, StatisticReportConstants.NO_CLEAR_ANSWER, StatisticReportConstants.NO_CLEAR_ANSWER);
		return statisticReport;
	}
}
