package io.elgayed.processing;

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

import io.elgayed.model.Speech;
import io.elgayed.model.StatisticsReport;
import io.elgayed.statistics.StatisticsReportCollector;

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
	 * <li>Item writer: streams speeches and uses {@link StatisticsReportCollector} to derive a {@link StatisticsReport}
	 * </ol>
	 * @param csvFileUrls URLs of the CSV files to be processed
	 * @return {@link StatisticsReport} containing stats derived from speeches read the given CSV File URLs
	 * 
	 * @throws JobExecutionAlreadyRunningException
	 * @throws JobRestartException
	 * @throws JobInstanceAlreadyCompleteException
	 * @throws JobParametersInvalidException if {@link JobParameters} are not valid, {@code JobParameters} are validated using {@link BatchProcessingConfiguration#jobParametersValidator()}
	 */
	public StatisticsReport processCsvFiles (Map<String, String> csvFileUrls) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		JobParameters jobParams = new JobParameters(
				csvFileUrls.entrySet().stream().collect(
						Collectors.toMap(Map.Entry::getKey, entry -> new JobParameter(entry.getValue().toString()))
					)
		);
		JobExecution jobExecution = jobLauncher.run(job, jobParams);
		StatisticsReport statisticsReport = (StatisticsReport) jobExecution.getExecutionContext().get(StatisticReportWriter.STATISTIC_REPORT_KEY);
		return statisticsReport;
	}
}
