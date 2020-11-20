package com.fashiondigital.it;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.NginxContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fashiondigital.model.StatisticReport;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class EvaluationControllerIntegrationTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EvaluationControllerIntegrationTest.class);
	
	@Autowired
	private TestRestTemplate restTemplate;
	@LocalServerPort
	private int port;
	
	/**
	 * NGINX docker container to host CSV files for integration tests
	 */
	@Container
	public static NginxContainer<?> nginxContainer = new NginxContainer<>("nginx:1.9.4")
		.withClasspathResourceMapping("csvTestFiles", "/usr/share/nginx/html", BindMode.READ_ONLY)
	    .withLogConsumer(new Slf4jLogConsumer(LOGGER).withSeparateOutputStreams());
	
	@Test
	public void evaluationEndpointTest () throws UnsupportedOperationException, IOException, InterruptedException {
		StatisticReport statisticReport = restTemplate.getForObject(
				String.format("http://localhost:%d/evaluation?url1=%s", port, getNginxFileUrl("/speeches.csv").toString()),
				StatisticReport.class);
		
		assertEquals("zero", statisticReport.getMostSpeeches());
		assertEquals("Alexander Abel", statisticReport.getMostSecurity());
		assertEquals("Caesare Collins", statisticReport.getLeastWordy());
	}
	
	@Test
	public void evaluationEndpointWithTwoCSVFilesTest () throws UnsupportedOperationException, IOException, InterruptedException {
		StatisticReport statisticReport = restTemplate.getForObject(
				String.format("http://localhost:%d/evaluation?url1=%s&url2=%s", port, getNginxFileUrl("/speeches.csv").toString(), getNginxFileUrl("/speeches1.csv").toString()),
				StatisticReport.class);
		
		assertEquals("Amir Elgayed", statisticReport.getMostSpeeches());
		assertEquals("Alexander Abel", statisticReport.getMostSecurity());
		assertEquals("Amir Elgayed", statisticReport.getLeastWordy());
	}
	
	static URL getNginxFileUrl (String filePath) throws MalformedURLException {
		return new URL (nginxContainer.getBaseUrl("http", 80), filePath);
	}
}
