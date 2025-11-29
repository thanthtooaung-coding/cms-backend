package com.movie.celestix.common.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class CmsService {

    private static final Logger logger = LoggerFactory.getLogger(CmsService.class);
    private final RestTemplate restTemplate;
    
    @Value("${cms.base-url:http://localhost:4001}")
    private String cmsBaseUrl;

    public CmsService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Get tenant info from CMS by slug
     */
    public TenantInfo getTenantInfoBySlug(String slug) {
        try {
            String url = cmsBaseUrl + "/api/cms/page-request/tenant/" + slug;
            logger.debug("Calling CMS API: {}", url);
            
            ResponseEntity<CmsApiResponse> response = restTemplate.getForEntity(url, CmsApiResponse.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                CmsApiResponse apiResponse = response.getBody();
                if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                    logger.debug("Successfully retrieved tenant info from CMS for slug: {}", slug);
                    return apiResponse.getData();
                }
            }
            logger.warn("CMS API returned unsuccessful response for slug: {}", slug);
            return null;
        } catch (RestClientException e) {
            // Log as warning instead of error to avoid cluttering logs if CMS is unavailable
            logger.warn("Failed to call CMS API for slug: {} - {}. Email will be sent without logo.", slug, e.getMessage());
            return null;
        } catch (Exception e) {
            logger.warn("Unexpected error calling CMS API for slug: {} - {}. Email will be sent without logo.", slug, e.getMessage());
            return null;
        }
    }

    /**
     * Get tenant info from CMS by tenant name
     * Converts tenant name to slug format (e.g., "JCGV Yangon" -> "jcgv-yangon")
     */
    public TenantInfo getTenantInfoByName(String tenantName) {
        if (tenantName == null || tenantName.trim().isEmpty()) {
            return null;
        }
        String slug = convertToSlug(tenantName);
        return getTenantInfoBySlug(slug);
    }

    /**
     * Convert tenant name to URL slug format
     * Examples: "JCGV Yangon" -> "jcgv-yangon", "jcgv-mandalay" -> "jcgv-mandalay"
     */
    private String convertToSlug(String name) {
        if (name == null) {
            return "";
        }
        // Convert to lowercase and replace spaces/special chars with hyphens
        return name.toLowerCase()
                .trim()
                .replaceAll("\\s+", "-")  // Replace spaces with hyphens
                .replaceAll("[^a-z0-9-]", "")  // Remove special characters except hyphens
                .replaceAll("-+", "-")  // Replace multiple hyphens with single hyphen
                .replaceAll("^-|-$", "");  // Remove leading/trailing hyphens
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CmsApiResponse {
        private boolean success;
        private String message;
        private TenantInfo data;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TenantInfo {
        private Long tenantId;
        private String tenantName;
        private String logoUrl;
        private String pageTitle;
        private String pageUrl;
    }
}

