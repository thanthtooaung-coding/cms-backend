package service

import (
	"errors"
	"fmt"
	"gorm.io/gorm"
	"math"
	"strings"
	"time"

	"github.com/sirupsen/logrus"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/mapper"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/repository"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/request"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/response"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/pkg/utils"
)

func safeDerefString(s *string) string {
	if s != nil {
		return *s
	}
	return ""
}

type PageRequestService interface {
	CreatePageRequest(req request.CreatePageRequest) (*response.PageRequestResponse, error)
	GetAllPageRequests(req *request.PaginateRequest) ([]*response.PageRequestResponse, *utils.Pagination, error)
	ChangeStatus(req request.ChangeStatusPageRequest, currentUserID uint) error
	GetTenantInfoBySlug(urlSlug string) (*response.TenantInfoResponse, error)
}

type PageRequestServiceImpl struct {
	logger      *logrus.Logger
	repo        repository.PageRequestRepository
	pageService PageService
	lmsService  LmsService
	emailService EmailService
	ownerRepo   repository.OwnerRepository
}

var _ PageRequestService = (*PageRequestServiceImpl)(nil)

func NewPageRequestService(logger *logrus.Logger, repo repository.PageRequestRepository, pageService PageService, lmsService LmsService, emailService EmailService, ownerRepo repository.OwnerRepository) *PageRequestServiceImpl {
	return &PageRequestServiceImpl{
		logger:      logger,
		repo:        repo,
		pageService: pageService,
		lmsService:  lmsService,
		emailService: emailService,
		ownerRepo:   ownerRepo,
	}
}

func (s *PageRequestServiceImpl) CreatePageRequest(req request.CreatePageRequest) (*response.PageRequestResponse, error) {

	pageRequest := &models.PageRequest{
		OwnerID:     req.OwnerID,
		RequestType: req.RequestType,
		Title:       req.Title,
		PageUrl:     req.PageUrl,
		LogoUrl:     req.LogoUrl,
		CreatedAt:   time.Now(),
		UpdatedAt:   time.Now(),
	}

	if err := s.repo.CreatePageRequest(pageRequest); err != nil {
		s.logger.WithError(err).Error("Failed to create page request")
		return nil, err
	}

	return mapper.ToPageRequestResponse(pageRequest), nil
}

func (s *PageRequestServiceImpl) GetAllPageRequests(req *request.PaginateRequest) ([]*response.PageRequestResponse, *utils.Pagination, error) {
	total, err := s.repo.CountPageRequests()
	if err != nil {
		s.logger.WithError(err).Error("Failed to count page requests")
		return nil, nil, err
	}

	offset := utils.CalculateOffset(req.Page, req.Limit)

	pageRequests, err := s.repo.GetAllPageRequests(offset, req.Limit)
	if err != nil {
		s.logger.WithError(err).Error("Failed to get all page requests")
		return nil, nil, err
	}

	var responses []*response.PageRequestResponse
	for _, pr := range pageRequests {
		responses = append(responses, mapper.ToPageRequestResponse(pr))
	}

	pagination := &utils.Pagination{
		Page:       req.Page,
		Limit:      req.Limit,
		Total:      total,
		TotalPages: int(math.Ceil(float64(total) / float64(req.Limit))),
	}

	return responses, pagination, nil
}

func (s *PageRequestServiceImpl) ChangeStatus(req request.ChangeStatusPageRequest, currentUserID uint) error {

	pageRequest, err := s.repo.GetById(req.RequestID)
	if err != nil || errors.Is(err, gorm.ErrRecordNotFound) {
		return fmt.Errorf("invalid pageRequestId: %w", err)
	}

	pageRequest.AdminID = &currentUserID

	if err := s.repo.UpdateStatus(req.RequestID, req.Status); err != nil {
		return err
	}

	if req.Status == models.RequestApproved {
		s.logger.Infof("Page request %d approved. Attempting to create a new page.", req.RequestID)

		pageCreateReq := request.PageCreateRequest{
			Title:              pageRequest.Title,
			ImageURL:           pageRequest.LogoUrl,
			OwnerID:            pageRequest.OwnerID,
			PublishedByStaffID: &currentUserID,
		}

		createdPage, err := s.pageService.Create(pageCreateReq)
		if err != nil {
			s.logger.WithError(err).Errorf("Failed to create page from approved request ID %d", req.RequestID)
			return fmt.Errorf("failed to create page after approval: %w", err)
		}

		s.logger.Infof("Successfully created page with ID %d from approved request ID %d", createdPage.ID, req.RequestID)

		switch pageRequest.RequestType {
		case "LMS":
			s.logger.Infof("RequestType is LMS. Triggering setup for new LMS tenant for Owner %d", pageRequest.OwnerID)

			owner, err := s.ownerRepo.GetOwnerByID(pageRequest.OwnerID)
			if err != nil {
				s.logger.WithError(err).Errorf("Failed to find Owner with ID %d for LMS setup", pageRequest.OwnerID)
				return fmt.Errorf("failed to find owner %d: %w", pageRequest.OwnerID, err)
			}
			
			lmsReq := LmsTenantRequest{
				Name:    pageRequest.Title,
				OwnerID: pageRequest.OwnerID,
				CmsPageID: createdPage.ID,
			}

			createdTenant, err := s.lmsService.CreateTenant(lmsReq)
			if err != nil {
				s.logger.WithError(err).Error("Failed to setup new LMS tenant")
				return fmt.Errorf("failed to setup LMS tenant: %w", err)
			}
			s.logger.Infof("Successfully triggered LMS tenant creation for Page ID %d", createdPage.ID)

			generatedPassword, err := utils.GenerateSecurePassword(16)
			if err != nil {
				s.logger.WithError(err).Error("Failed to generate secure password for new LMS owner")
				return fmt.Errorf("failed to generate password: %w", err)
			}
			s.logger.Warnf("GENERATED PASSWORD FOR owner %d: %s", owner.ID, generatedPassword)

			// Get the Owner role ID from LMS service
			ownerRole, err := s.lmsService.GetRoleByName("Owner")
			if err != nil {
				s.logger.WithError(err).Error("Failed to get Owner role from LMS service")
				return fmt.Errorf("failed to get Owner role: %w", err)
			}

			lmsOwnerReq := LmsOwnerRequest{
				Username:    owner.Email,
				Password:    generatedPassword,
				Email:       owner.Email,
				Name:        safeDerefString(owner.Name),
				TenantID:    createdTenant.ID,
				RoleID:      ownerRole.ID,
				Address:     safeDerefString(owner.Address),
				PhoneNumber: safeDerefString(owner.PhoneNumber),
			}

			if err := s.lmsService.CreateLmsOwner(lmsOwnerReq); err != nil {
				s.logger.WithError(err).Error("Failed to create LMS owner user after tenant creation")
				return fmt.Errorf("failed to create LMS owner user: %w", err)
			}

			s.logger.Infof("Successfully created LMS owner for Tenant ID %d", createdTenant.ID)

			// Send email with LMS credentials
			emailSubject := fmt.Sprintf("Your LMS Dashboard Access - %s", pageRequest.Title)
			emailBody := fmt.Sprintf(`Dear %s,

Your page request "%s" has been approved!

Your LMS Dashboard and Client access credentials:

Username: %s
Password: %s

LMS Dashboard: http://localhost:5176/lms/%s
LMS Client: http://localhost:5175/lms/%s

Please keep these credentials secure and do not share them with anyone.

Best regards,
CMS Team`, 
				safeDerefString(owner.Name),
				pageRequest.Title,
				owner.Email,
				generatedPassword,
				extractSlugFromUrl(pageRequest.PageUrl),
				extractSlugFromUrl(pageRequest.PageUrl))

			if err := s.emailService.SendEmail(owner.Email, emailSubject, emailBody); err != nil {
				s.logger.WithError(err).Error("Failed to send email with LMS credentials")
				// Don't fail the whole operation if email fails
			} else {
				s.logger.Infof("Successfully sent LMS credentials email to %s", owner.Email)
			}

		case "E-COMMERCE":
			s.logger.Infof("RequestType is E-COMMERCE. Setup logic not implemented yet.")

		case "BOOKING":
			s.logger.Infof("RequestType is BOOKING. Setup logic not implemented yet.")

		default:
			s.logger.Warnf("No specific setup logic for RequestType: %s", pageRequest.RequestType)
		}
	}
	return nil
}

func (s *PageRequestServiceImpl) GetTenantInfoBySlug(urlSlug string) (*response.TenantInfoResponse, error) {
	pageRequest, err := s.repo.GetByUrlSlug(urlSlug)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, fmt.Errorf("page request not found for slug: %s", urlSlug)
		}
		s.logger.WithError(err).Errorf("Failed to get page request by URL slug: %s", urlSlug)
		return nil, err
	}

	if pageRequest.Status != models.RequestApproved {
		return nil, fmt.Errorf("page request is not approved")
	}

	// Get tenant info from LMS by name (tenant name is the page request title)
	tenant, err := s.lmsService.GetTenantByName(pageRequest.Title)
	if err != nil {
		s.logger.WithError(err).Errorf("Failed to get tenant by name: %s", pageRequest.Title)
		return nil, fmt.Errorf("failed to get tenant info: %w", err)
	}

	tenantInfo := &response.TenantInfoResponse{
		TenantID:   tenant.ID,
		TenantName: tenant.Name,
		PageTitle:  pageRequest.Title,
		PageUrl:    pageRequest.PageUrl,
		LogoUrl:    pageRequest.LogoUrl,
		OwnerID:    pageRequest.OwnerID,
		OwnerEmail: pageRequest.Owner.Email,
	}

	return tenantInfo, nil
}

func extractSlugFromUrl(pageUrl string) string {
	// Extract slug from URL like http://localhost:5176/lms/triple-a-language-school
	// or /lms/triple-a-language-school
	parts := strings.Split(pageUrl, "/lms/")
	if len(parts) > 1 {
		return strings.TrimSuffix(parts[1], "/")
	}
	return ""
}
