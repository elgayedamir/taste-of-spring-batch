package com.elgayed.processing;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.validation.BindException;

import com.elgayed.model.Speech;

@Configuration
@EnableBatchProcessing
public class BatchProcessingConfiguration {
	
	public static final String CSV_FILES_BATCH_PROCESSING_JOB_NAME = "csvFilesProcessingJob";
	public static final String CSV_FILES_BATCH_PROCESSING_STEP_NAME = "csvFilesProcessingStep";
	public static final Integer CSV_FILES_BATCH_PROCESSING_STEP_CHUNK = 100;
	
	@Bean
	public JobParametersValidator jobParametersValidator() {
		return new JobParametersValidator() {
			@Override
			public void validate(JobParameters parameters) throws JobParametersInvalidException {
				Map<String, JobParameter> paramsMap = parameters.getParameters();
				if (paramsMap.isEmpty())
					throw new JobParametersInvalidException("At least one url should be provided");
				List<String> invalidUrls = paramsMap.entrySet().stream()
					.map(Entry::getValue)
					.map(Objects::toString)
					//returns invalid urls
					.filter(value -> {try {new URL(value);return false;} catch (MalformedURLException e) {return true;}})
					.collect(Collectors.toList());
				if (!invalidUrls.isEmpty()) {
					throw new JobParametersInvalidException(String.format("The following URLs are not valid: %s", String.join(", ", invalidUrls)));
				}
			}
		};
	}

	@Bean
	public Job csvProcessingJob(JobBuilderFactory jobBuilderFactory, Step step, JobParametersValidator validator) {
		return jobBuilderFactory
				.get(CSV_FILES_BATCH_PROCESSING_JOB_NAME)
				.validator(validator)
				.flow(step)
				.end()
				.build();
	}
	
	@Bean
	public Step csvFilesProcessingStep(StepBuilderFactory stepBuilderFactory, 
			MultiResourceItemReader<Speech> itemReader,
			ItemWriter<Speech> itemWriter) {
		
		return stepBuilderFactory.get(CSV_FILES_BATCH_PROCESSING_STEP_NAME)
				.<Speech, Speech>chunk(CSV_FILES_BATCH_PROCESSING_STEP_CHUNK)
				.reader(itemReader)
				.writer(itemWriter)
				.faultTolerant()
				//skip if a speech record is not valid in the CSV file
				.skip(FlatFileParseException.class)
				.skipLimit(100)
				.build();
	}
	
	@Bean
	@JobScope
	public MultiResourceItemReader<Speech> multiResourceItemReader(
			FlatFileItemReader<Speech> csvFileReader,
			@Value("#{jobParameters}") Map<String, String> csvUrls) {
		
		MultiResourceItemReader<Speech> resourceItemReader = new MultiResourceItemReader<>();
		resourceItemReader.setResources(
				csvUrls.entrySet().stream()
					.map(Entry::getValue)
					.map(t -> {
						try {
							return new UrlResource(t);
						} catch (MalformedURLException e) {
							return null;
						}
					})
					.filter(Objects::nonNull)
					.collect(Collectors.toSet())
					.toArray(new Resource[0])
				);
		resourceItemReader.setDelegate(csvFileReader);
		return resourceItemReader;
	}

	@Bean
	public FlatFileItemReader<Speech> csvFileReader(LineMapper<Speech> mapper) {
		FlatFileItemReader<Speech> reader = new FlatFileItemReader<>();
		reader.setLinesToSkip(1);
		reader.setLineMapper(mapper);
		reader.setEncoding(StandardCharsets.UTF_8.name());
		reader.setStrict(Boolean.FALSE);
		return reader;
	}

	@Bean
	public LineMapper<Speech> lineMapper(FieldSetMapper<Speech> speechRecordFieldSetMapper) {
		DefaultLineMapper<Speech> lineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setNames(new String[] { 
					SpeechRecordConstants.SPEAKER_COLUMN_NAME, 
					SpeechRecordConstants.SPEECH_THEME_COLUMN_NAME,
					SpeechRecordConstants.SPEECH_DATE_COLUMN_NAME,
					SpeechRecordConstants.SPEECH_WORDS_COLUMN_NAME
					});
		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(speechRecordFieldSetMapper);
		return lineMapper;
	}
	
	@Bean
	public FieldSetMapper<Speech> speechRecordFieldSetMapper() throws BindException {
		return 
			fieldSet -> {
				Speech speech = new Speech();
				speech.setSpeaker(fieldSet.readString(SpeechRecordConstants.SPEAKER_COLUMN_NAME));
				speech.setTheme(fieldSet.readString(SpeechRecordConstants.SPEECH_THEME_COLUMN_NAME));
				speech.setDate(fieldSet.readDate(SpeechRecordConstants.SPEECH_DATE_COLUMN_NAME));
				speech.setWords(fieldSet.readLong(SpeechRecordConstants.SPEECH_WORDS_COLUMN_NAME));
				return speech;
			};
	}
}
