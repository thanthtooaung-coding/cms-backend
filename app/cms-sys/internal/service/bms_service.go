package service

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"strings"
	"time"

	"github.com/sirupsen/logrus"
)

type BmsTenantRequest struct {
	Name     string `json:"name"`
	IsActive bool   `json:"isActive"`
}

type BmsTenantResponse struct {
	ID        uint   `json:"id"`
	Name      string `json:"name"`
	IsActive  bool   `json:"isActive"`
	CreatedAt string `json:"createdAt,omitempty"`
	UpdatedAt string `json:"updatedAt,omitempty"`
}

type BmsAdminRequest struct {
	Name     string `json:"name"`
	Email    string `json:"email"`
	Password string `json:"password"`
}

type BmsAdminResponse struct {
	ID    uint   `json:"id"`
	Name  string `json:"name"`
	Email string `json:"email"`
}

type BmsService interface {
	CreateTenant(req BmsTenantRequest) (*BmsTenantResponse, error)
	CreateBmsAdmin(req BmsAdminRequest, tenantID uint) error
	GetTenantByName(name string) (*BmsTenantResponse, error)
	GetTenantByID(id uint) (*BmsTenantResponse, error)
}

type bmsServiceImpl struct {
	logger     *logrus.Logger
	bmsBaseURL string
	httpClient *http.Client
}

func NewBmsService(logger *logrus.Logger, bmsBaseURL string) BmsService {
	return &bmsServiceImpl{
		logger:     logger,
		bmsBaseURL: bmsBaseURL,
		httpClient: &http.Client{
			Timeout: 10 * time.Second,
		},
	}
}

func (s *bmsServiceImpl) CreateTenant(req BmsTenantRequest) (*BmsTenantResponse, error) {
	jsonData, err := json.Marshal(req)
	if err != nil {
		s.logger.WithError(err).Error("Failed to marshal BmsTenantRequest")
		return nil, fmt.Errorf("failed to marshal request: %w", err)
	}

	targetURL := fmt.Sprintf("%s/tenants", s.bmsBaseURL)

	httpReq, err := http.NewRequest("POST", targetURL, bytes.NewBuffer(jsonData))
	if err != nil {
		s.logger.WithError(err).Error("Failed to create NewRequest for BMS tenant")
		return nil, fmt.Errorf("failed to create bms request: %w", err)
	}
	httpReq.Header.Set("Content-Type", "application/json")

	resp, err := s.httpClient.Do(httpReq)
	if err != nil {
		s.logger.WithError(err).Errorf("Failed to send request to BMS service at %s", targetURL)
		return nil, fmt.Errorf("bms service communication error: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusCreated {
		bodyBytes, _ := io.ReadAll(resp.Body)
		bodyStr := string(bodyBytes)
		s.logger.Errorf("BMS service returned non-success status for tenant creation: %d. Response body: %s", resp.StatusCode, bodyStr)
		return nil, fmt.Errorf("bms service returned status %d", resp.StatusCode)
	}

	var tenantResponse BmsTenantResponse
	if err := json.NewDecoder(resp.Body).Decode(&tenantResponse); err != nil {
		s.logger.WithError(err).Error("Failed to decode BMS tenant response")
		return nil, fmt.Errorf("failed to decode bms response: %w", err)
	}

	s.logger.Infof("Successfully created tenant in BMS with new TenantID %d", tenantResponse.ID)
	return &tenantResponse, nil
}

func (s *bmsServiceImpl) CreateBmsAdmin(req BmsAdminRequest, tenantID uint) error {
	jsonData, err := json.Marshal(req)
	if err != nil {
		s.logger.WithError(err).Error("Failed to marshal BmsAdminRequest")
		return fmt.Errorf("failed to marshal admin request: %w", err)
	}

	// Add tenantId to the request
	adminReqWithTenant := map[string]interface{}{
		"name":     req.Name,
		"email":    req.Email,
		"password": req.Password,
		"tenantId": tenantID,
	}
	jsonData, err = json.Marshal(adminReqWithTenant)
	if err != nil {
		s.logger.WithError(err).Error("Failed to marshal BmsAdminRequest with tenantId")
		return fmt.Errorf("failed to marshal admin request with tenantId: %w", err)
	}

	targetURL := fmt.Sprintf("%s/admins", s.bmsBaseURL)

	httpReq, err := http.NewRequest("POST", targetURL, bytes.NewBuffer(jsonData))
	if err != nil {
		s.logger.WithError(err).Error("Failed to create NewRequest for BMS admin")
		return fmt.Errorf("failed to create bms admin request: %w", err)
	}
	httpReq.Header.Set("Content-Type", "application/json")

	resp, err := s.httpClient.Do(httpReq)
	if err != nil {
		s.logger.WithError(err).Errorf("Failed to send request to BMS service at %s for TenantID %d", targetURL, tenantID)
		return fmt.Errorf("bms service communication error: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusCreated {
		bodyBytes, _ := io.ReadAll(resp.Body)
		bodyStr := string(bodyBytes)
		s.logger.Errorf("BMS service returned non-success status for admin creation: %d. Response body: %s. Request payload: %s", resp.StatusCode, bodyStr, string(jsonData))
		return fmt.Errorf("bms service returned status %d for admin creation: %s", resp.StatusCode, bodyStr)
	}

	s.logger.Infof("Successfully created BMS admin user for TenantID %d", tenantID)
	return nil
}

func (s *bmsServiceImpl) GetTenantByName(name string) (*BmsTenantResponse, error) {
	targetURL := fmt.Sprintf("%s/tenants", s.bmsBaseURL)

	httpReq, err := http.NewRequest("GET", targetURL, nil)
	if err != nil {
		s.logger.WithError(err).Error("Failed to create request for getting tenants")
		return nil, fmt.Errorf("failed to create request: %w", err)
	}

	resp, err := s.httpClient.Do(httpReq)
	if err != nil {
		s.logger.WithError(err).Errorf("Failed to send request to BMS service at %s", targetURL)
		return nil, fmt.Errorf("bms service communication error: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		s.logger.Errorf("BMS service returned non-success status: %d", resp.StatusCode)
		return nil, fmt.Errorf("bms service returned status %d", resp.StatusCode)
	}

	var tenants []BmsTenantResponse
	if err := json.NewDecoder(resp.Body).Decode(&tenants); err != nil {
		s.logger.WithError(err).Error("Failed to decode BMS tenants response")
		return nil, fmt.Errorf("failed to decode response: %w", err)
	}

	// Try exact match first
	for _, tenant := range tenants {
		if tenant.Name == name {
			return &tenant, nil
		}
	}

	// Try case-insensitive match
	nameLower := strings.ToLower(name)
	for _, tenant := range tenants {
		if strings.ToLower(tenant.Name) == nameLower {
			return &tenant, nil
		}
	}

	return nil, fmt.Errorf("tenant with name '%s' not found", name)
}

func (s *bmsServiceImpl) GetTenantByID(id uint) (*BmsTenantResponse, error) {
	targetURL := fmt.Sprintf("%s/tenants/%d", s.bmsBaseURL, id)

	httpReq, err := http.NewRequest("GET", targetURL, nil)
	if err != nil {
		s.logger.WithError(err).Error("Failed to create request for getting tenant")
		return nil, fmt.Errorf("failed to create request: %w", err)
	}

	resp, err := s.httpClient.Do(httpReq)
	if err != nil {
		s.logger.WithError(err).Errorf("Failed to send request to BMS service at %s", targetURL)
		return nil, fmt.Errorf("bms service communication error: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		s.logger.Errorf("BMS service returned non-success status: %d", resp.StatusCode)
		return nil, fmt.Errorf("bms service returned status %d", resp.StatusCode)
	}

	var tenantResponse BmsTenantResponse
	if err := json.NewDecoder(resp.Body).Decode(&tenantResponse); err != nil {
		s.logger.WithError(err).Error("Failed to decode BMS tenant response")
		return nil, fmt.Errorf("failed to decode response: %w", err)
	}

	return &tenantResponse, nil
}

