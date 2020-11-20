package com.fashiondigital.rest;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fashiondigital.model.StatisticReport;
import com.fashiondigital.processing.BatchProcessingService;

@RestController
@RequestMapping(path = "evaluation")
public class EvaluationController {

	
	public static final Pattern QUERY_PARAM_URLS_PATTERN = Pattern.compile("url\\d+");
	
	@Autowired
	private BatchProcessingService batchProcessingService;
	
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StatisticReport> evaluate (@RequestParam final Map<String, String> queryParams) throws 
		JobExecutionAlreadyRunningException, 
		JobRestartException, 
		JobInstanceAlreadyCompleteException, 
		JobParametersInvalidException {
		
		final Map<String, String> csvFileUrls = queryParams.entrySet().stream()
					.filter(entry -> QUERY_PARAM_URLS_PATTERN.matcher(entry.getKey()).matches())
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		
		StatisticReport statisticReport = batchProcessingService.processCsvFiles(csvFileUrls);
		
		return new ResponseEntity<StatisticReport>(statisticReport, HttpStatus.OK);
	}
	
	

	@ExceptionHandler(JobParametersInvalidException.class)
	@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
	@ResponseBody
	protected ApiError handleUserAlreadyExistsException(JobParametersInvalidException ex) {
		return new ApiError(HttpStatus.UNPROCESSABLE_ENTITY.name(), ex.getMessage());
	}
	
}
