package com.content_management_system.lms.features.course.service.impl;

import com.content_management_system.lms.features.course.dto.RelatedTopicsRequest;
import com.content_management_system.lms.features.course.dto.RelatedTopicsResponse;
import com.content_management_system.lms.features.course.service.RelatedTopicsService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelatedTopicsServiceImpl implements RelatedTopicsService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ai.service.url:http://ai-service:8086}")
    private String aiServiceUrl;

    // Try to use service discovery first, fallback to direct URL
    private String getAiServiceUrl() {
        // In Docker, use service name; in local, use localhost
        String serviceUrl = System.getenv("AI_SERVICE_URL");
        if (serviceUrl != null && !serviceUrl.isEmpty()) {
            return serviceUrl;
        }
        // Try service discovery via Consul (lb://ai-service) or direct URL
        return aiServiceUrl;
    }

    @Override
    public RelatedTopicsResponse generateRelatedTopics(RelatedTopicsRequest request) {
        try {
            String url = getAiServiceUrl() + "/generate-related-topics";
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("course_title", request.getCourseTitle());
            requestBody.put("course_description", request.getCourseDescription());
            requestBody.put("category", request.getCategory());
            
            // Add lesson content
            if (request.getLessons() != null && !request.getLessons().isEmpty()) {
                List<Map<String, String>> lessonsData = new ArrayList<>();
                for (RelatedTopicsRequest.LessonContent lesson : request.getLessons()) {
                    Map<String, String> lessonData = new HashMap<>();
                    lessonData.put("title", lesson.getTitle());
                    lessonData.put("content", lesson.getContent() != null ? lesson.getContent() : "");
                    lessonData.put("material_type", lesson.getMaterialType() != null ? lesson.getMaterialType() : "Unknown");
                    lessonsData.add(lessonData);
                }
                requestBody.put("lessons", lessonsData);
            } else {
                requestBody.put("lessons", new ArrayList<>());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                JsonNode topicsNode = jsonNode.get("topics");

                RelatedTopicsResponse result = new RelatedTopicsResponse();
                List<RelatedTopicsResponse.Topic> topics = new ArrayList<>();

                if (topicsNode != null && topicsNode.isArray()) {
                    for (JsonNode topicNode : topicsNode) {
                        RelatedTopicsResponse.Topic topic = new RelatedTopicsResponse.Topic();
                        topic.setId(topicNode.get("id") != null ? topicNode.get("id").asInt() : null);
                        topic.setName(topicNode.get("name") != null ? topicNode.get("name").asText() : null);
                        topics.add(topic);
                    }
                }

                result.setTopics(topics);
                return result;
            } else {
                log.error("Failed to generate related topics. Status: {}, Body: {}", 
                        response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to generate related topics from AI service");
            }
        } catch (Exception e) {
            log.error("Error calling AI service for related topics generation", e);
            throw new RuntimeException("Error generating related topics: " + e.getMessage(), e);
        }
    }
}

