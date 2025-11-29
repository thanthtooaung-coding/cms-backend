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

type EcsTenantRequest struct {
	Name     string `json:"name"`
	IsActive bool   `json:"isActive"`
}

type EcsTenantResponse struct {
	ID        uint   `json:"id"`
	Name      string `json:"name"`
	IsActive  bool   `json:"isActive"`
	CreatedAt string `json:"createdAt,omitempty"`
	UpdatedAt string `json:"updatedAt,omitempty"`
}

type EcsOwnerRequest struct {
	Username    string `json:"username"`
	Password    string `json:"password"`
	Email       string `json:"email"`
	Name        string `json:"name"`
	Address     string `json:"address,omitempty"`
	PhoneNumber string `json:"phoneNumber,omitempty"`
	RoleID      uint   `json:"roleId"`
	TenantID    uint   `json:"tenantId"`
}

type EcsRoleResponse struct {
	ID   uint   `json:"id"`
	Name string `json:"name"`
}

type EcsService interface {
	CreateTenant(req EcsTenantRequest) (*EcsTenantResponse, error)
	CreateEcsOwner(req EcsOwnerRequest) error
	GetTenantByName(name string) (*EcsTenantResponse, error)
	GetTenantByID(id uint) (*EcsTenantResponse, error)
	GetRoleByName(name string) (*EcsRoleResponse, error)
}

type ecsServiceImpl struct {
	logger     *logrus.Logger
	ecsBaseURL string
	httpClient *http.Client
}

func NewEcsService(logger *logrus.Logger, ecsBaseURL string) EcsService {
	return &ecsServiceImpl{
		logger:     logger,
		ecsBaseURL: ecsBaseURL,
		httpClient: &http.Client{
			Timeout: 10 * time.Second,
		},
	}
}

func (s *ecsServiceImpl) CreateTenant(req EcsTenantRequest) (*EcsTenantResponse, error) {
	jsonData, err := json.Marshal(req)
	if err != nil {
		s.logger.WithError(err).Error("Failed to marshal EcsTenantRequest")
		return nil, fmt.Errorf("failed to marshal request: %w", err)
	}

	targetURL := fmt.Sprintf("%s/tenants", s.ecsBaseURL)

	httpReq, err := http.NewRequest("POST", targetURL, bytes.NewBuffer(jsonData))
	if err != nil {
		s.logger.WithError(err).Error("Failed to create NewRequest for ECS tenant")
		return nil, fmt.Errorf("failed to create ecs request: %w", err)
	}
	httpReq.Header.Set("Content-Type", "application/json")

	resp, err := s.httpClient.Do(httpReq)
	if err != nil {
		s.logger.WithError(err).Errorf("Failed to send request to ECS service at %s", targetURL)
		return nil, fmt.Errorf("ecs service communication error: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusCreated {
		bodyBytes, _ := io.ReadAll(resp.Body)
		s.logger.Errorf("ECS service returned non-success status for tenant creation: %d, body: %s", resp.StatusCode, string(bodyBytes))
		return nil, fmt.Errorf("ecs service returned status %d", resp.StatusCode)
	}

	var tenantResponse EcsTenantResponse
	if err := json.NewDecoder(resp.Body).Decode(&tenantResponse); err != nil {
		s.logger.WithError(err).Error("Failed to decode ECS tenant response")
		return nil, fmt.Errorf("failed to decode ecs response: %w", err)
	}

	s.logger.Infof("Successfully created tenant in ECS with TenantID %d", tenantResponse.ID)
	return &tenantResponse, nil
}

func (s *ecsServiceImpl) CreateEcsOwner(req EcsOwnerRequest) error {
	jsonData, err := json.Marshal(req)
	if err != nil {
		s.logger.WithError(err).Error("Failed to marshal EcsOwnerRequest")
		return fmt.Errorf("failed to marshal request: %w", err)
	}

	targetURL := fmt.Sprintf("%s/auth/register", s.ecsBaseURL)

	httpReq, err := http.NewRequest("POST", targetURL, bytes.NewBuffer(jsonData))
	if err != nil {
		s.logger.WithError(err).Error("Failed to create NewRequest for ECS owner")
		return fmt.Errorf("failed to create ecs request: %w", err)
	}
	httpReq.Header.Set("Content-Type", "application/json")

	resp, err := s.httpClient.Do(httpReq)
	if err != nil {
		s.logger.WithError(err).Errorf("Failed to send request to ECS service at %s", targetURL)
		return fmt.Errorf("ecs service communication error: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusCreated && resp.StatusCode != http.StatusOK {
		bodyBytes, _ := io.ReadAll(resp.Body)
		s.logger.Errorf("ECS service returned non-success status for owner creation: %d, body: %s", resp.StatusCode, string(bodyBytes))
		return fmt.Errorf("ecs service returned status %d", resp.StatusCode)
	}

	s.logger.Infof("Successfully created ECS owner for Tenant ID %d", req.TenantID)
	return nil
}

func (s *ecsServiceImpl) GetTenantByName(name string) (*EcsTenantResponse, error) {
	targetURL := fmt.Sprintf("%s/tenants/name/%s", s.ecsBaseURL, name)

	resp, err := s.httpClient.Get(targetURL)
	if err != nil {
		s.logger.WithError(err).Errorf("Failed to send request to ECS service at %s", targetURL)
		return nil, fmt.Errorf("ecs service communication error: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		s.logger.Errorf("ECS service returned non-success status for get tenant by name: %d", resp.StatusCode)
		return nil, fmt.Errorf("ecs service returned status %d", resp.StatusCode)
	}

	var tenantResponse EcsTenantResponse
	if err := json.NewDecoder(resp.Body).Decode(&tenantResponse); err != nil {
		s.logger.WithError(err).Error("Failed to decode ECS tenant response")
		return nil, fmt.Errorf("failed to decode ecs response: %w", err)
	}

	return &tenantResponse, nil
}

func (s *ecsServiceImpl) GetTenantByID(id uint) (*EcsTenantResponse, error) {
	targetURL := fmt.Sprintf("%s/tenants/%d", s.ecsBaseURL, id)

	resp, err := s.httpClient.Get(targetURL)
	if err != nil {
		s.logger.WithError(err).Errorf("Failed to send request to ECS service at %s", targetURL)
		return nil, fmt.Errorf("ecs service communication error: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		s.logger.Errorf("ECS service returned non-success status for get tenant by ID: %d", resp.StatusCode)
		return nil, fmt.Errorf("ecs service returned status %d", resp.StatusCode)
	}

	var tenantResponse EcsTenantResponse
	if err := json.NewDecoder(resp.Body).Decode(&tenantResponse); err != nil {
		s.logger.WithError(err).Error("Failed to decode ECS tenant response")
		return nil, fmt.Errorf("failed to decode ecs response: %w", err)
	}

	return &tenantResponse, nil
}

func (s *ecsServiceImpl) GetRoleByName(name string) (*EcsRoleResponse, error) {
	// Get all tenants first, then find role
	// For now, return a default role based on name
	// In a real implementation, you'd have a roles endpoint
	roleMap := map[string]uint{
		"Owner":   1,
		"Admin":   2,
		"Staff":   3,
		"Customer": 4,
	}

	roleID, exists := roleMap[strings.Title(name)]
	if !exists {
		return nil, fmt.Errorf("role '%s' not found", name)
	}

	return &EcsRoleResponse{
		ID:   roleID,
		Name: strings.Title(name),
	}, nil
}

