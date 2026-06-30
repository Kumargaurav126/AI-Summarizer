package com.summarise.AIRecap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Map;

@Service
public class SummariseService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public String processContent(SummariseRequest request) {
        RestTemplate restTemplate = new RestTemplate();

        String prompt = buildPrompt(request.getMessages());
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String urlWithKey = geminiApiUrl + geminiApiKey;

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(urlWithKey, entity, Map.class);
            return extractTextFromResponse(response);
        } catch (HttpClientErrorException.TooManyRequests e) {
            return "AI summary is temporarily unavailable due to rate limits. Please try again later.";
        } catch (HttpClientErrorException e) {
            System.out.println("Error status: " + e.getStatusCode());
            System.out.println("Error body: " + e.getResponseBodyAsString());
            return "AI service error: " + e.getStatusCode() + ". Please try again later.";
        } catch (Exception e) {
            return "Failed to generate summary: " + e.getMessage();
        }
    }

    private String extractTextFromResponse(ResponseEntity<Map> response) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, String>> parts = (List<Map<String, String>>) content.get("parts");
            return parts.get(0).get("text");
        } catch (Exception e) {
            return "Failed to summarize: " + e.getMessage();
        }
    }

    private String buildPrompt(List<String> messages) {
        StringBuilder prompt = new StringBuilder("Summarize the following student chat conversation:\n\n");
        for (String msg : messages) {
            prompt.append("- ").append(msg).append("\n");
        }
        return prompt.toString();
    }
}
