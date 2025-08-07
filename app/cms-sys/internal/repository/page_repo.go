package repository

import (
	"github.com/sirupsen/logrus"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"
	"gorm.io/gorm"
)

type PageRepository interface {
	CreatePage(page *models.Page) error
	GetPageByID(id uint) (*models.Page, error)
	GetAllPages(offset, limit int) ([]models.Page, error)
	UpdatePage(page *models.Page) error
	DeletePageByID(id uint) error
	CountPages() (int64, error)
	UpdateStatus(id uint, status models.PageStatus) error
}

type pageRepositoryImpl struct {
	logger *logrus.Logger
	db     *gorm.DB
}

var _ PageRepository = (*pageRepositoryImpl)(nil)

func NewPageRepository(logger *logrus.Logger, db *gorm.DB) PageRepository {
	return &pageRepositoryImpl{logger: logger, db: db}
}

func (r *pageRepositoryImpl) CreatePage(page *models.Page) error {
	if err := r.db.Create(page).Error; err != nil {
		r.logger.WithError(err).Error("Failed to create page")
		return err
	}
	return nil
}

func (r *pageRepositoryImpl) GetPageByID(id uint) (*models.Page, error) {
	var page models.Page
	err := r.db.Preload("Owner.Role").Preload("PublishedByStaff.Role").First(&page, id).Error
	if err != nil {
		return nil, err
	}
	return &page, nil
}

func (r *pageRepositoryImpl) GetAllPages(offset, limit int) ([]models.Page, error) {
	var pages []models.Page
	err := r.db.Preload("Owner.Role").Preload("PublishedByStaff.Role").Offset(offset).Limit(limit).Find(&pages).Error
	if err != nil {
		return nil, err
	}
	return pages, nil
}

func (r *pageRepositoryImpl) UpdatePage(page *models.Page) error {
	if err := r.db.Save(page).Error; err != nil {
		r.logger.WithError(err).Error("Failed to update page")
		return err
	}
	return nil
}

func (r *pageRepositoryImpl) DeletePageByID(id uint) error {
	if err := r.db.Delete(&models.Page{}, id).Error; err != nil {
		r.logger.WithError(err).Error("Failed to delete page by ID")
		return err
	}
	return nil
}

func (r *pageRepositoryImpl) CountPages() (int64, error) {
	var count int64
	if err := r.db.Model(&models.Page{}).Count(&count).Error; err != nil {
		r.logger.WithError(err).Error("Failed to count pages")
		return 0, err
	}
	return count, nil
}

func (r *pageRepositoryImpl) UpdateStatus(id uint, status models.PageStatus) error {
	result := r.db.Model(&models.Page{}).Where("id = ?", id).Update("status", status)
	if result.Error != nil {
		r.logger.WithError(result.Error).Error("Failed to update status of page")
		return result.Error
	}
	if result.RowsAffected == 0 {
		return gorm.ErrRecordNotFound
	}
	return nil
}
