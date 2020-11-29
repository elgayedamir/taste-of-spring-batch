package com.elgayed.it;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import com.elgayed.model.Speech;
import com.elgayed.processing.SpeechRecordConstants;

/**
 * Utility class for generating political speeches large CSV file to be used for testing 
 */
public class DataGenerator {
	
	public static final Set<String> SPEAKERS = Set.of("Amir Elgayed", "Alexander Abel", "Caesare Collins",
			"Bernhard Belling", "John Doe", "Lee Sin");

	public static final Set<String> THEMES = Set.of("Kohlesubventionen", "Innere Sicherheit", "Bildungspolitik",
			"Global Warming", "World Peace", "External Policies");
	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final String SPEECHS_CSV_RAW_FORMAT = "%s, %s, %s, %s\n";
	
	/**
	 * Generates a random {@link Date} between 2011-01-01 and 2014-12-31
	 */
	public static final Supplier<Date> DATE_SUPPLIER = () -> {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2011, 1, 1);
		Date startDate = calendar.getTime();
		calendar.set(2014, 12, 31);
		Date endDate = calendar.getTime();
		long startMillis = startDate.getTime();
		long endMillis = endDate.getTime();
		long randomMillisSinceEpoch = ThreadLocalRandom.current().nextLong(startMillis, endMillis);
		return new Date(randomMillisSinceEpoch);
	};

	public static final Supplier<Long> WORDS_COUNT_SUPPLIER = () -> {
		return ThreadLocalRandom.current().nextLong(500, 10000);
	};

	public static final Supplier<String> SPEAKER_SUPPLIER = () -> {
		return SPEAKERS.stream().skip(new Random().nextInt(SPEAKERS.size())).findFirst().get();
	};

	public static final Supplier<String> THEME_SUPPLIER = () -> {
		return THEMES.stream().skip(new Random().nextInt(THEMES.size())).findFirst().get();
	};
	
	public static final Supplier<Speech> SPEECH_SUPPLIER = () -> {
		Speech speech = new Speech();
		speech.setSpeaker(SPEAKER_SUPPLIER.get());
		speech.setTheme(THEME_SUPPLIER.get());
		speech.setWords(WORDS_COUNT_SUPPLIER.get());
		speech.setDate(DATE_SUPPLIER.get());
		return speech;
	};
	
	public static void main(String[] args) throws Exception {
		File largeCsvFile = FileUtils.getFile(FileUtils.getUserDirectory(), "/Desktop/large-speeches.csv");
		
		try (FileOutputStream out = new FileOutputStream(largeCsvFile);) {
			Stream<Speech> stream = Stream.generate(SPEECH_SUPPLIER);
			
			//Append columns header to CSV file
			out.write(String.format(SPEECHS_CSV_RAW_FORMAT, 
					SpeechRecordConstants.SPEAKER_COLUMN_NAME,
					SpeechRecordConstants.SPEECH_THEME_COLUMN_NAME,
					SpeechRecordConstants.SPEECH_DATE_COLUMN_NAME,
					SpeechRecordConstants.SPEECH_WORDS_COLUMN_NAME).getBytes());
			
			stream.limit(10000).forEach(speech -> {
					try {
						out.write(String.format(SPEECHS_CSV_RAW_FORMAT, speech.getSpeaker(), speech.getTheme(), DATE_FORMAT.format(speech.getDate()),
								speech.getWords()).getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
			});
		}
	};
}
