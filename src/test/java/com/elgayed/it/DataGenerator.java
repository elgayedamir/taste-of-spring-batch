package com.elgayed.it;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import com.elgayed.model.Speech;

/**
 * Utility class for generating political speeches large CSV file to be used for testing 
 */
public class DataGenerator {
	
	public static final Set<String> SPEAKERS = Set.of("Amir Elgayed", "Alexander Abel", "Caesare Collins",
			"Bernhard Belling", "John Doe", "Lee Sin");

	public static final Set<String> THEMES = Set.of("Kohlesubventionen", "Innere Sicherheit", "Bildungspolitik",
			"Global Warming", "World Peace", "External Policies");
	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final Supplier<Date> DATE_SUPPLIER = () -> {
		Date startDate = new Date(111, 1, 1);
		Date endDate = new Date(114, 12, 31);
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

			out.write("edner, Thema, Datum, Wörter\n".getBytes());
			
			stream.limit(10000).forEach(speech -> {
					try {
						out.write(String.format("%s, %s, %s, %s\n", speech.getSpeaker(), speech.getTheme(), DATE_FORMAT.format(speech.getDate()),
								speech.getWords()).getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
			});
		}
	};
}
