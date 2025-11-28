package service

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"time"

	"github.com/sirupsen/logrus"
)

type LmsTenantRequest struct {
	Name      string `json:"name"`
	OwnerID   uint   `json:"ownerId"`
	CmsPageID uint   `json:"cmsPageId"`
}

type LmsTenantResponse struct {
	ID   uint   `json:"id"`
	Name string `json:"name"`
}

type LmsOwnerRequest struct {
	Username    string `json:"username"`
	Password    string `json:"password"`
	Email       string `json:"email"`
	Name        string `json:"name"`
	Address     string `json:"address,omitempty"`
	PhoneNumber string `json:"phoneNumber,omitempty"`
	RoleID      uint   `json:"roleId"`
	TenantID    uint   `json:"tenantId"`
}

type EmailService interface {
	SendEmail(to, subject, body string) error
}

type LmsRoleResponse struct {
	ID   uint   `json:"id"`
	Name string `json:"name"`
}

type LmsService interface {
	CreateTenant(req LmsTenantRequest) (*LmsTenantResponse, error)
	CreateLmsOwner(req LmsOwnerRequest) error
	GetTenantByName(name string) (*LmsTenantResponse, error)
	GetTenantByID(id uint) (*LmsTenantResponse, error)
	GetRoleByName(name string) (*LmsRoleResponse, error)
}

type emailServiceImpl struct {
	logger      *logrus.Logger
	emailBaseURL string
	httpClient   *http.Client
}

type lmsServiceImpl struct {
	logger     *logrus.Logger
	lmsBaseURL string
	httpClient *http.Client
}

func NewLmsService(logger *logrus.Logger, lmsBaseURL string) LmsService {
	return &lmsServiceImpl{
		logger:     logger,
		lmsBaseURL: lmsBaseURL,
		httpClient: &http.Client{
			Timeout: 10 * time.Second,
		},
	}
}

func (s *lmsServiceImpl) CreateTenant(req LmsTenantRequest) (*LmsTenantResponse, error) {
	jsonData, err := json.Marshal(req)
	if err != nil {
		s.logger.WithError(err).Error("Failed to marshal LmsTenantRequest")
		return nil, fmt.Errorf("failed to marshal request: %w", err)
	}

	targetURL := fmt.Sprintf("%s/tenants", s.lmsBaseURL)

	httpReq, err := http.NewRequest("POST", targetURL, bytes.NewBuffer(jsonData))
	if err != nil {
		s.logger.WithError(err).Error("Failed to create NewRequest for LMS tenant")
		return nil, fmt.Errorf("failed to create lms request: %w", err)
	}
	httpReq.Header.Set("Content-Type", "application/json")

	resp, err := s.httpClient.Do(httpReq)
	if err != nil {
		s.logger.WithError(err).Errorf("Failed to send request to LMS service at %s", targetURL)
		return nil, fmt.Errorf("lms service communication error: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusCreated {
		s.logger.Errorf("LMS service returned non-success status for tenant creation: %d", resp.StatusCode)
		return nil, fmt.Errorf("lms service returned status %d", resp.StatusCode)
	}

	var tenantResponse LmsTenantResponse
	if err := json.NewDecoder(resp.Body).Decode(&tenantResponse); err != nil {
		s.logger.WithError(err).Error("Failed to decode LMS tenant response")
		return nil, fmt.Errorf("failed to decode lms response: %w", err)
	}

	s.logger.Infof("Successfully created tenant in LMS for owner %d with new TenantID %d", req.OwnerID, tenantResponse.ID)
	return &tenantResponse, nil
}

func (s *lmsServiceImpl) CreateLmsOwner(req LmsOwnerRequest) error {
	jsonData, err := json.Marshal(req)
	if err != nil {
		s.logger.WithError(err).Error("Failed to marshal LmsOwnerRequest")
		// FIX 1: Changed from 'return nil, fmt.Errorf(...)'
		return fmt.Errorf("failed to marshal owner request: %w", err)
	}

	targetURL := fmt.Sprintf("%s/users", s.lmsBaseURL) // Calls POST /users

	httpReq, err := http.NewRequest("POST", targetURL, bytes.NewBuffer(jsonData))
	if err != nil {
		s.logger.WithError(err).Error("Failed to create NewRequest for LMS user")
		return fmt.Errorf("failed to create lms user request: %w", err)
	}
	httpReq.Header.Set("Content-Type", "application/json")

	resp, err := s.httpClient.Do(httpReq)
	if err != nil {
		// FIX 2: Changed from 'req.OwnerID' to 'req.TenantID' in the log
		s.logger.WithError(err).Errorf("Failed to send request to LMS service at %s for TenantID %d", targetURL, req.TenantID)
		// FIX 3: Changed from 'return nil, fmt.Errorf(...)'
		return fmt.Errorf("lms service communication error: %w", err)
	}
	defer resp.Body.Close()

	// lms-sys returns 201 Created on success
	if resp.StatusCode != http.StatusCreated {
		bodyBytes, _ := io.ReadAll(resp.Body)
		bodyStr := string(bodyBytes)
		s.logger.Errorf("LMS service returned non-success status for user creation: %d. Response body: %s. Request payload: %s", resp.StatusCode, bodyStr, string(jsonData))
		return fmt.Errorf("lms service returned status %d for user creation: %s", resp.StatusCode, bodyStr)
	}

	s.logger.Infof("Successfully created LMS owner user for TenantID %d", req.TenantID)
	return nil
}

func (s *lmsServiceImpl) GetTenantByName(name string) (*LmsTenantResponse, error) {
	targetURL := fmt.Sprintf("%s/tenants", s.lmsBaseURL)

	httpReq, err := http.NewRequest("GET", targetURL, nil)
	if err != nil {
		s.logger.WithError(err).Error("Failed to create request for getting tenants")
		return nil, fmt.Errorf("failed to create request: %w", err)
	}

	resp, err := s.httpClient.Do(httpReq)
	if err != nil {
		s.logger.WithError(err).Errorf("Failed to send request to LMS service at %s", targetURL)
		return nil, fmt.Errorf("lms service communication error: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		s.logger.Errorf("LMS service returned non-success status: %d", resp.StatusCode)
		return nil, fmt.Errorf("lms service returned status %d", resp.StatusCode)
	}

	var tenants []LmsTenantResponse
	if err := json.NewDecoder(resp.Body).Decode(&tenants); err != nil {
		s.logger.WithError(err).Error("Failed to decode LMS tenants response")
		return nil, fmt.Errorf("failed to decode response: %w", err)
	}

	for _, tenant := range tenants {
		if tenant.Name == name {
			return &tenant, nil
		}
	}

	return nil, fmt.Errorf("tenant with name '%s' not found", name)
}

func (s *lmsServiceImpl) GetTenantByID(id uint) (*LmsTenantResponse, error) {
	targetURL := fmt.Sprintf("%s/tenants/%d", s.lmsBaseURL, id)

	httpReq, err := http.NewRequest("GET", targetURL, nil)
	if err != nil {
		s.logger.WithError(err).Error("Failed to create request for getting tenant")
		return nil, fmt.Errorf("failed to create request: %w", err)
	}

	resp, err := s.httpClient.Do(httpReq)
	if err != nil {
		s.logger.WithError(err).Errorf("Failed to send request to LMS service at %s", targetURL)
		return nil, fmt.Errorf("lms service communication error: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		s.logger.Errorf("LMS service returned non-success status: %d", resp.StatusCode)
		return nil, fmt.Errorf("lms service returned status %d", resp.StatusCode)
	}

	var tenant LmsTenantResponse
	if err := json.NewDecoder(resp.Body).Decode(&tenant); err != nil {
		s.logger.WithError(err).Error("Failed to decode LMS tenant response")
		return nil, fmt.Errorf("failed to decode response: %w", err)
	}

	return &tenant, nil
}

func (s *lmsServiceImpl) GetRoleByName(name string) (*LmsRoleResponse, error) {
	targetURL := fmt.Sprintf("%s/roles/name/%s", s.lmsBaseURL, name)

	httpReq, err := http.NewRequest("GET", targetURL, nil)
	if err != nil {
		s.logger.WithError(err).Error("Failed to create request for getting role")
		return nil, fmt.Errorf("failed to create request: %w", err)
	}

	resp, err := s.httpClient.Do(httpReq)
	if err != nil {
		s.logger.WithError(err).Errorf("Failed to send request to LMS service at %s", targetURL)
		return nil, fmt.Errorf("lms service communication error: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		bodyBytes, _ := io.ReadAll(resp.Body)
		bodyStr := string(bodyBytes)
		s.logger.Errorf("LMS service returned non-success status for role lookup: %d. Response body: %s", resp.StatusCode, bodyStr)
		return nil, fmt.Errorf("lms service returned status %d for role lookup: %s", resp.StatusCode, bodyStr)
	}

	var roleResponse LmsRoleResponse
	if err := json.NewDecoder(resp.Body).Decode(&roleResponse); err != nil {
		s.logger.WithError(err).Error("Failed to decode LMS role response")
		return nil, fmt.Errorf("failed to decode response: %w", err)
	}

	return &roleResponse, nil
}

func NewEmailService(logger *logrus.Logger, emailBaseURL string) EmailService {
	return &emailServiceImpl{
		logger:      logger,
		emailBaseURL: emailBaseURL,
		httpClient: &http.Client{
			Timeout: 10 * time.Second,
		},
	}
}

func (s *emailServiceImpl) SendEmail(to, subject, body string) error {
	emailReq := map[string]string{
		"to":      to,
		"subject": subject,
		"body":    body,
	}

	jsonData, err := json.Marshal(emailReq)
	if err != nil {
		s.logger.WithError(err).Error("Failed to marshal email request")
		return fmt.Errorf("failed to marshal email request: %w", err)
	}

	targetURL := fmt.Sprintf("%s/email/send", s.emailBaseURL)

	httpReq, err := http.NewRequest("POST", targetURL, bytes.NewBuffer(jsonData))
	if err != nil {
		s.logger.WithError(err).Error("Failed to create email request")
		return fmt.Errorf("failed to create email request: %w", err)
	}
	httpReq.Header.Set("Content-Type", "application/json")

	resp, err := s.httpClient.Do(httpReq)
	if err != nil {
		s.logger.WithError(err).Errorf("Failed to send email request to %s", targetURL)
		return fmt.Errorf("email service communication error: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusAccepted {
		s.logger.Errorf("Email service returned non-success status: %d", resp.StatusCode)
		return fmt.Errorf("email service returned status %d", resp.StatusCode)
	}

	s.logger.Infof("Successfully sent email to %s", to)
	return nil
}
