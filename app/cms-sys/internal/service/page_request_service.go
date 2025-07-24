package service

import (
	"errors"
	"fmt"
	"gorm.io/gorm"
	"math"

	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/mapper"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/repository"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/response"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/request"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/pkg/utils"
	"github.com/sirupsen/logrus"
	"time"
)

type PageRequestService interface {
	CreatePageRequest(req request.CreatePageRequest, logoUrl string) (*response.PageRequestResponse, error)
	GetAllPageRequests(req *request.PaginateRequest) ([]*response.PageRequestResponse, *utils.Pagination, error)
	ChangeStatus(req request.ChangeStatusPageRequest, currentUserID uint) error
}

type PageRequestServiceImpl struct {
	logger      *logrus.Logger
	repo        repository.PageRequestRepository
	ownerRepo   repository.OwnerRepository
}

var _ PageRequestService = (*PageRequestServiceImpl)(nil)

func NewPageRequestService(logger *logrus.Logger, repo repository.PageRequestRepository) *PageRequestServiceImpl {
	return &PageRequestServiceImpl{
		logger: logger,
		repo:   repo,
	}
}

func (s *PageRequestServiceImpl) CreatePageRequest(req request.CreatePageRequest, logoUrl string) (*response.PageRequestResponse, error) {

	pageRequest := &models.PageRequest{
		OwnerID:     req.OwnerID,
		RequestType: req.RequestType,
		Title:       req.Title,
		PageUrl:     req.PageUrl,
		LogoUrl:     logoUrl,
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

	_, err := s.repo.GetById(req.RequestID)
	if err != nil || errors.Is(err, gorm.ErrRecordNotFound) {
		return fmt.Errorf("invalid pageRequestId: %w", err)
	}

	if err := s.repo.UpdateStatus(req.RequestID, req.Status); err != nil {
		return err
	}

// 	if req.Status == "APPROVED" {
// 		requestUUID, err := uuid.Parse(req.RequestID)
// 		if err != nil {
// 			return errors.New("invalid request ID format")
// 		}
//
// 		pageRequest, err := s.repo.GetById(requestUUID)
// 		if err != nil {
// 			return fmt.Errorf("failed to fetch page request: %w", err)
// 		}

// 		createReq := &request.PageCreateRequest{
// 			PageRequestID: requestUUID,
// 			Title:         pageRequest.Title,
// 			Content:       utils.SafeString(pageRequest.Description),
// 			ImageUrl:      utils.SafeString(pageRequest.LogoUrl),
// 			OwnerId:       pageRequest.OwnerID,
// 			PublisherId:   currentUserID,
// 			Status:        utils.StringPtr("PUBLISHED"),
// 		}

// 		_, err = s.pageService.Create(createReq)
// 		if err != nil {
// 			return fmt.Errorf("failed to auto-create page: %w", err)
// 		}

// 		owner, err := s.ownerRepo.GetOwnerById(pageRequest.OwnerID)
// 		if err != nil {
// 			return fmt.Errorf("failed to fetch owner: %w", err)
// 		}
// 		log.Printf("Owner fetched: %+v\n", owner)
//
// 	}

	return nil
}
