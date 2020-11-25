package com.elgayed.rest;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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

import com.elgayed.model.StatisticsReport;
import com.elgayed.processing.BatchProcessingService;

@RestController
@RequestMapping(path = "evaluation")
public class EvaluationController {

	
	public static final Pattern QUERY_PARAM_URLS_PATTERN = Pattern.compile("url\\d+");
	
	@Autowired
	private BatchProcessingService batchProcessingService;
	
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StatisticsReport> evaluate (@RequestParam final Map<String, String> queryParams) throws 
		JobExecutionAlreadyRunningException, 
		JobRestartException, 
		JobInstanceAlreadyCompleteException, 
		JobParametersInvalidException {
		
		//Filter query params: only pramas that the key patches url{index}, e.g. url1, url2,...,url{n}, and the value is not blank
		final Map<String, String> csvFileUrls = queryParams.entrySet().stream()
					.filter(entry -> QUERY_PARAM_URLS_PATTERN.matcher(entry.getKey()).matches())
					.filter(entry -> StringUtils.isNotBlank(entry.getValue()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		
		StatisticsReport statisticsReport = batchProcessingService.processCsvFiles(csvFileUrls);
		
		return new ResponseEntity<StatisticsReport>(statisticsReport, HttpStatus.OK);
	}
	
	

	@ExceptionHandler(JobParametersInvalidException.class)
	@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
	@ResponseBody
	protected ApiError handleUserAlreadyExistsException(JobParametersInvalidException ex) {
		return new ApiError(HttpStatus.UNPROCESSABLE_ENTITY.name(), ex.getMessage());
	}
	
}
