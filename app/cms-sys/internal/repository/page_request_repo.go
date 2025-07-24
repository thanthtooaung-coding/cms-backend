package repository

import (
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"
	"github.com/sirupsen/logrus"
	"gorm.io/gorm"
)

type PageRequestRepository interface {
	CreatePageRequest(pageRequest *models.PageRequest) error
	GetAllPageRequests(offset, limit int) ([]*models.PageRequest, error)
	CountPageRequests() (int64, error)
	GetById(id uint) (*models.PageRequest, error)
	UpdateStatus(id uint, status models.RequestStatus) error
}

type PageRequestRepositoryImpl struct {
	logger *logrus.Logger
	db     *gorm.DB
}

var _ PageRequestRepository = (*PageRequestRepositoryImpl)(nil)

func NewPageRequestRepository(logger *logrus.Logger, db *gorm.DB) PageRequestRepository {
	return &PageRequestRepositoryImpl{
		logger: logger,
		db:     db,
	}
}

func (r *PageRequestRepositoryImpl) CreatePageRequest(pageRequest *models.PageRequest) error {
	if err := r.db.Create(pageRequest).Error; err != nil {
		r.logger.WithError(err).Error("Failed to create page request")
		return err
	}
	return nil
}

func (r *PageRequestRepositoryImpl) GetAllPageRequests(offset, limit int) ([]*models.PageRequest, error) {
	var pageRequests []*models.PageRequest
	if err := r.db.Offset(offset).Limit(limit).Find(&pageRequests).Error; err != nil {
		r.logger.WithError(err).Error("Failed to get all page requests")
		return nil, err
	}
	return pageRequests, nil
}

func (r *PageRequestRepositoryImpl) CountPageRequests() (int64, error) {
	var count int64
	if err := r.db.Model(&models.PageRequest{}).Count(&count).Error; err != nil {
		r.logger.WithError(err).Error("Failed to count page requests")
		return 0, err
	}
	return count, nil
}


func (r *PageRequestRepositoryImpl) GetById(id uint) (*models.PageRequest, error) {
	var pageRequest models.PageRequest
	if err := r.db.Where("id = ?", id).First(&pageRequest).Error; err != nil {
		r.logger.WithError(err).Error("Failed to get page request")
		return nil, err
	}
	return &pageRequest, nil
}

func (r *PageRequestRepositoryImpl) UpdateStatus(id uint, status models.RequestStatus) error {
    result := r.db.Model(&models.PageRequest{}).Where("id = ?", id).Update("status", status)
    if result.Error != nil {
        r.logger.WithError(result.Error).Error("Failed to update status of page request")
        return result.Error
    }
    if result.RowsAffected == 0 {
        return gorm.ErrRecordNotFound
    }
    return nil
}
