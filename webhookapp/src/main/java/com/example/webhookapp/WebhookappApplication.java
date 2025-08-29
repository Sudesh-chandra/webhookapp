package com.example.webhookapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class WebhookappApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(WebhookappApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		// 1. Prepare request body
		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("name", "Valikala Sudesh Chandra");
		requestBody.put("regNo", "22BCE7267");
		requestBody.put("email", "sudesh.22bce7267@vitapstudent.ac.in");

		// 2. Prepare headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// 3. Combine body + headers
		HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

		// 4. Send POST request to generate webhook
		String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
		ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url, entity, ApiResponse.class);

		ApiResponse apiResponse = response.getBody();
		System.out.println("Webhook URL: " + apiResponse.getWebhook());
		System.out.println("Access Token: " + apiResponse.getAccessToken());

		// ============================
		// Q3: Submit Final SQL Query
		// ============================

		String finalQuery = "WITH HighestValidPayment AS ( " +
				"SELECT AMOUNT, EMP_ID " +
				"FROM PAYMENTS " +
				"WHERE EXTRACT(DAY FROM PAYMENT_TIME) <> 1 " +
				"ORDER BY AMOUNT DESC " +
				"LIMIT 1 ) " +
				"SELECT hvp.AMOUNT AS SALARY, " +
				"CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
				"(2025 - EXTRACT(YEAR FROM e.DOB)) AS AGE, " +
				"d.DEPARTMENT_NAME " +
				"FROM HighestValidPayment hvp " +
				"JOIN EMPLOYEE e ON hvp.EMP_ID = e.EMP_ID " +
				"JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID;";

		// 2. Prepare answer body
		Map<String, String> answer = new HashMap<>();
		answer.put("finalQuery", finalQuery);

		// 3. Prepare headers with JWT token
		HttpHeaders headers2 = new HttpHeaders();
		headers2.setContentType(MediaType.APPLICATION_JSON);
		headers2.set("Authorization", apiResponse.getAccessToken());

		// 4. Send POST request to webhook
		HttpEntity<Map<String, String>> entity2 = new HttpEntity<>(answer, headers2);
		ResponseEntity<String> finalResponse = restTemplate.postForEntity(apiResponse.getWebhook(), entity2, String.class);

		// 5. Print final submission response
		System.out.println("Final Submission Response: " + finalResponse.getBody());
	}
}
