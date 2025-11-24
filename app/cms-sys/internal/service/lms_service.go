package service

import (
	"bytes"
	"encoding/json"
	"fmt"
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

type LmsService interface {
	CreateTenant(req LmsTenantRequest) (*LmsTenantResponse, error)
	CreateLmsOwner(req LmsOwnerRequest) error
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
		s.logger.Errorf("LMS service returned non-success status for user creation: %d", resp.StatusCode)
		return fmt.Errorf("lms service returned status %d for user creation", resp.StatusCode)
	}

	s.logger.Infof("Successfully created LMS owner user for TenantID %d", req.TenantID)
	return nil
}
