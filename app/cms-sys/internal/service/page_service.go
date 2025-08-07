package service

import (
	"errors"
	"fmt"
	"math"

	"github.com/sirupsen/logrus"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/mapper"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/repository"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/request"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/response"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/pkg/utils"
	"gorm.io/gorm"
)

type PageService interface {
	Create(req request.PageCreateRequest) (*response.PageResponse, error)
	Update(id uint, req request.PageUpdateRequest) (*response.PageResponse, error)
	GetAll(req *request.PaginateRequest) ([]*response.PageResponse, *utils.Pagination, error)
	GetByID(id uint) (*response.PageResponse, error)
	Delete(ids []uint) error
	ChangeStatus(req request.ChangeStatusPage) error
}

type pageServiceImpl struct {
	log  *logrus.Logger
	repo repository.PageRepository
}

var _ PageService = (*pageServiceImpl)(nil)

func NewPageService(log *logrus.Logger, repo repository.PageRepository) PageService {
	return &pageServiceImpl{
		log:  log,
		repo: repo,
	}
}

func (s *pageServiceImpl) Create(req request.PageCreateRequest) (*response.PageResponse, error) {
	newPage := &models.Page{
		Title:              &req.Title,
		ImageURL:           &req.ImageURL,
		OwnerID:            req.OwnerID,
		PublishedByStaffID: req.PublishedByStaffID,
		Status:             models.PageDraft,
	}

	if err := s.repo.CreatePage(newPage); err != nil {
		s.log.WithError(err).Error("Failed to create page in repository")
		return nil, err
	}

	createdPage, err := s.repo.GetPageByID(newPage.ID)
	if err != nil {
		s.log.WithError(err).Error("Failed to retrieve newly created page")
		return nil, err
	}

	return mapper.ToPageResponse(createdPage), nil
}

func (s *pageServiceImpl) Update(id uint, req request.PageUpdateRequest) (*response.PageResponse, error) {
	pageToUpdate, err := s.repo.GetPageByID(id)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, fmt.Errorf("page with ID %d not found", id)
		}
		s.log.WithError(err).Errorf("Failed to retrieve page with ID %d for update", id)
		return nil, err
	}

	if req.Title != nil {
		pageToUpdate.Title = req.Title
	}
	if req.ImageURL != nil {
		pageToUpdate.ImageURL = req.ImageURL
	}
	if req.Status != nil {
		pageToUpdate.Status = *req.Status
	}

	if err := s.repo.UpdatePage(pageToUpdate); err != nil {
		s.log.WithError(err).Errorf("Failed to update page with ID %d in repository", id)
		return nil, err
	}

	updatedPage, err := s.repo.GetPageByID(id)
	if err != nil {
		s.log.WithError(err).Error("Failed to retrieve updated page")
		return nil, err
	}

	return mapper.ToPageResponse(updatedPage), nil
}

func (s *pageServiceImpl) GetAll(req *request.PaginateRequest) ([]*response.PageResponse, *utils.Pagination, error) {
	total, err := s.repo.CountPages()
	if err != nil {
		s.log.WithError(err).Error("Failed to count pages")
		return nil, nil, err
	}

	offset := utils.CalculateOffset(req.Page, req.Limit)
	pages, err := s.repo.GetAllPages(offset, req.Limit)
	if err != nil {
		s.log.WithError(err).Error("Failed to get all pages from repository")
		return nil, nil, err
	}

	pagination := &utils.Pagination{
		Page:       req.Page,
		Limit:      req.Limit,
		Total:      total,
		TotalPages: int(math.Ceil(float64(total) / float64(req.Limit))),
	}

	return mapper.ToPageListResponse(pages), pagination, nil
}

func (s *pageServiceImpl) GetByID(id uint) (*response.PageResponse, error) {
	page, err := s.repo.GetPageByID(id)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, fmt.Errorf("page with ID %d not found", id)
		}
		s.log.WithError(err).Errorf("Failed to get page with ID %d from repository", id)
		return nil, err
	}

	return mapper.ToPageResponse(page), nil
}

func (s *pageServiceImpl) Delete(ids []uint) error {
	for _, id := range ids {
		if err := s.repo.DeletePageByID(id); err != nil {
			s.log.WithError(err).Errorf("Failed to delete page with ID %d", id)
			return err
		}
		s.log.Infof("Successfully deleted page with ID %d", id)
	}
	return nil
}

func (s *pageServiceImpl) ChangeStatus(req request.ChangeStatusPage) error {
	s.log.WithFields(logrus.Fields{
		"pageId": req.PageID,
		"userId": req.UserID,
		"status": req.Status,
	}).Info("Received request to change page status")
	page, err := s.repo.GetPageByID(req.PageID)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return fmt.Errorf("page with ID %d not found", req.PageID)
		}
		s.log.WithError(err).Errorf("Failed to retrieve page with ID %d", req.PageID)
		return err
	}

	page.Status = req.Status

	if req.Status == models.PagePublished {
		page.PublishedByStaffID = &req.UserID
	}

	if err := s.repo.UpdatePage(page); err != nil {
		s.log.WithError(err).Errorf("Failed to update status for page ID %d", req.PageID)
		return err
	}

	s.log.Infof("Successfully updated status of page %d to %s", req.PageID, req.Status)
	return nil
}
