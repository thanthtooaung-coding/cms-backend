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
	Name    string `json:"name"`
	OwnerID uint   `json:"ownerId"`
	CmsPageID uint `json:"cmsPageId"`
}

type LmsService interface {
	CreateTenant(req LmsTenantRequest) error
}

type lmsServiceImpl struct {
	logger  *logrus.Logger
	lmsBaseURL string
	httpClient *http.Client
}

func NewLmsService(logger *logrus.Logger, lmsBaseURL string) LmsService {
	return &lmsServiceImpl{
		logger:  logger,
		lmsBaseURL: lmsBaseURL,
		httpClient: &http.Client{
			Timeout: 10 * time.Second,
		},
	}
}

func (s *lmsServiceImpl) CreateTenant(req LmsTenantRequest) error {
	// 1. Marshal the request body
	jsonData, err := json.Marshal(req)
	if err != nil {
		s.logger.WithError(err).Error("Failed to marshal LmsTenantRequest")
		return fmt.Errorf("failed to marshal request: %w", err)
	}

	targetURL := fmt.Sprintf("%s/tenants", s.lmsBaseURL)

	httpReq, err := http.NewRequest("POST", targetURL, bytes.NewBuffer(jsonData))
	if err != nil {
		s.logger.WithError(err).Error("Failed to create NewRequest for LMS tenant")
		return fmt.Errorf("failed to create lms request: %w", err)
	}
	httpReq.Header.Set("Content-Type", "application/json")

	resp, err := s.httpClient.Do(httpReq)
	if err != nil {
		s.logger.WithError(err).Errorf("Failed to send request to LMS service at %s", targetURL)
		return fmt.Errorf("lms service communication error: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusCreated && resp.StatusCode != http.StatusOK {
		s.logger.Errorf("LMS service returned non-success status: %d", resp.StatusCode)
		return fmt.Errorf("lms service returned status %d", resp.StatusCode)
	}

	s.logger.Infof("Successfully triggered tenant creation in LMS for owner %d", req.OwnerID)
	return nil
}
