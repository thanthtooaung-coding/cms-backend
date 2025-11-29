package repository

import (
	"github.com/sirupsen/logrus"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"
	"gorm.io/gorm"
)

type PageRequestRepository interface {
	CreatePageRequest(pageRequest *models.PageRequest) error
	GetAllPageRequests(offset, limit int) ([]*models.PageRequest, error)
	CountPageRequests() (int64, error)
	GetById(id uint) (*models.PageRequest, error)
	UpdateStatus(id uint, status models.RequestStatus) error
	GetByOwnerID(ownerID uint) ([]*models.PageRequest, error)
	GetByUrlSlug(urlSlug string) (*models.PageRequest, error)
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
	if err := r.db.Preload("Owner").Offset(offset).Limit(limit).Find(&pageRequests).Error; err != nil {
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

func (r *PageRequestRepositoryImpl) GetByOwnerID(ownerID uint) ([]*models.PageRequest, error) {
	var pageRequests []*models.PageRequest
	if err := r.db.Where("owner_id = ?", ownerID).Order("created_at DESC").Find(&pageRequests).Error; err != nil {
		r.logger.WithError(err).Error("Failed to get page requests by owner ID")
		return nil, err
	}
	return pageRequests, nil
}

func (r *PageRequestRepositoryImpl) GetByUrlSlug(urlSlug string) (*models.PageRequest, error) {
	var pageRequest models.PageRequest
	// Match URL patterns like:
	// - http://localhost:5176/lms/triple-a-language-school or /lms/triple-a-language-school
	// - http://localhost:5177/bms/triple-a-language-school or /bms/triple-a-language-school
	// - http://localhost:5178/ecommerce/vezada (base ECS URL)
	// - http://localhost:5178/ecommerce/vezada/ecs-client/ or /ecommerce/vezada/ecs-client/
	// - http://localhost:5179/ecommerce/vezada/ecs-dashboard/ or /ecommerce/vezada/ecs-dashboard/
	// - http://localhost:5178/ecs-client/vezada or /ecs-client/vezada
	// - http://localhost:5179/ecs-dashboard/vezada or /ecs-dashboard/vezada
	lmsUrlPattern := "%/lms/" + urlSlug
	bmsUrlPattern := "%/bms/" + urlSlug
	ecsClientUrlPattern := "%/ecs-client/" + urlSlug
	ecsDashboardUrlPattern := "%/ecs-dashboard/" + urlSlug
	ecommerceBasePattern := "%/ecommerce/" + urlSlug
	ecommerceEcsClientPattern := "%/ecommerce/" + urlSlug + "/ecs-client%"
	ecommerceEcsDashboardPattern := "%/ecommerce/" + urlSlug + "/ecs-dashboard%"
	if err := r.db.Where("(page_url LIKE ? OR page_url LIKE ? OR page_url LIKE ? OR page_url LIKE ? OR page_url LIKE ? OR page_url LIKE ? OR page_url LIKE ?) AND status = ?",
		lmsUrlPattern, bmsUrlPattern, ecsClientUrlPattern, ecsDashboardUrlPattern, ecommerceBasePattern, ecommerceEcsClientPattern, ecommerceEcsDashboardPattern, models.RequestApproved).
		Preload("Owner").
		First(&pageRequest).Error; err != nil {
		r.logger.WithError(err).Errorf("Failed to get page request by URL slug: %s", urlSlug)
		return nil, err
	}
	return &pageRequest, nil
}
