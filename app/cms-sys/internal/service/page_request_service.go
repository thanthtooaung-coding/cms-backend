package service

import (
	"errors"
	"fmt"
	"gorm.io/gorm"
	"math"

	"github.com/sirupsen/logrus"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/mapper"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/repository"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/request"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/response"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/pkg/utils"
	"time"
)

type PageRequestService interface {
	CreatePageRequest(req request.CreatePageRequest) (*response.PageRequestResponse, error)
	GetAllPageRequests(req *request.PaginateRequest) ([]*response.PageRequestResponse, *utils.Pagination, error)
	ChangeStatus(req request.ChangeStatusPageRequest, currentUserID uint) error
}

type PageRequestServiceImpl struct {
	logger      *logrus.Logger
	repo        repository.PageRequestRepository
	pageService PageService
}

var _ PageRequestService = (*PageRequestServiceImpl)(nil)

func NewPageRequestService(logger *logrus.Logger, repo repository.PageRequestRepository, pageService PageService) *PageRequestServiceImpl {
	return &PageRequestServiceImpl{
		logger:      logger,
		repo:        repo,
		pageService: pageService,
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

		// 5. Call the page service to create the actual page.
		createdPage, err := s.pageService.Create(pageCreateReq)
		if err != nil {
			s.logger.WithError(err).Errorf("Failed to create page from approved request ID %d", req.RequestID)
			return fmt.Errorf("failed to create page after approval: %w", err)
		}

		s.logger.Infof("Successfully created page with ID %d from approved request ID %d", createdPage.ID, req.RequestID)
	}
	return nil
}
