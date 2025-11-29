package service

import (
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/repository"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/response"
	"github.com/sirupsen/logrus"
)

type DashboardService interface {
	GetDashboardStats() (*response.DashboardStatsResponse, error)
}

type dashboardServiceImpl struct {
	logger            *logrus.Logger
	ownerRepo         repository.OwnerRepository
	pageRequestRepo   repository.PageRequestRepository
	pageRepo          repository.PageRepository
}

func NewDashboardService(
	logger *logrus.Logger,
	ownerRepo repository.OwnerRepository,
	pageRequestRepo repository.PageRequestRepository,
	pageRepo repository.PageRepository,
) DashboardService {
	return &dashboardServiceImpl{
		logger:          logger,
		ownerRepo:       ownerRepo,
		pageRequestRepo: pageRequestRepo,
		pageRepo:        pageRepo,
	}
}

func (s *dashboardServiceImpl) GetDashboardStats() (*response.DashboardStatsResponse, error) {
	// Count total owners
	var totalOwners int64
	owners, err := s.ownerRepo.GetAllOwners()
	if err != nil {
		s.logger.WithError(err).Error("Failed to get all owners")
		return nil, err
	}
	totalOwners = int64(len(owners))

	// Count total page requests
	totalPageRequests, err := s.pageRequestRepo.CountPageRequests()
	if err != nil {
		s.logger.WithError(err).Error("Failed to count page requests")
		return nil, err
	}

	// Get all page requests to calculate status breakdowns
	allPageRequests, err := s.pageRequestRepo.GetAllPageRequests(0, 10000) // Get all requests
	if err != nil {
		s.logger.WithError(err).Error("Failed to get all page requests")
		return nil, err
	}

	var pendingRequests, approvedRequests, rejectedRequests int64
	var lmsRequests, bmsRequests, ecommerceRequests int64

	for _, pr := range allPageRequests {
		// Count by status
		switch pr.Status {
		case models.RequestPending:
			pendingRequests++
		case models.RequestApproved:
			approvedRequests++
		case models.RequestRejected:
			rejectedRequests++
		}

		// Count by request type
		switch pr.RequestType {
		case "LMS":
			lmsRequests++
		case "BOOKING":
			bmsRequests++
		case "E-COMMERCE":
			ecommerceRequests++
		}
	}

	// Count total pages
	totalPages, err := s.pageRepo.CountPages()
	if err != nil {
		s.logger.WithError(err).Error("Failed to count pages")
		return nil, err
	}

	return &response.DashboardStatsResponse{
		TotalOwners:          totalOwners,
		TotalPageRequests:    totalPageRequests,
		PendingRequests:      pendingRequests,
		ApprovedRequests:     approvedRequests,
		RejectedRequests:     rejectedRequests,
		TotalPages:           totalPages,
		LMSRequests:          lmsRequests,
		BMSRequests:          bmsRequests,
		ECommerceRequests:    ecommerceRequests,
	}, nil
}

