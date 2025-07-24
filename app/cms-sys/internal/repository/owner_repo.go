package repository

import (
	"errors"
	"github.com/sirupsen/logrus"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"
	"gorm.io/gorm"
)

type OwnerRepository interface {
	GetAllOwners() ([]models.User, error)
	CreateOwner(owner *models.User) error
	GetOwnerByID(id uint) (*models.User, error)
	UpdateOwner(owner *models.User) error
	DeleteOwnerByID(id uint) error
	OwnerHasAssociations(id uint) (bool, error)
	ForceDeleteOwner(id uint) error
}

type ownerRepositoryImpl struct {
	logger *logrus.Logger
	db     *gorm.DB
}

var _ OwnerRepository = (*ownerRepositoryImpl)(nil)

func NewOwnerRepository(logger *logrus.Logger, db *gorm.DB) OwnerRepository {
	return &ownerRepositoryImpl{logger: logger, db: db}
}

func (r *ownerRepositoryImpl) CreateOwner(owner *models.User) error {
	var ownerRole models.Role
	if err := r.db.Where("name = ?", models.RoleOwner).First(&ownerRole).Error; err != nil {
		r.logger.WithError(err).Error("Failed to find 'Owner' role")
		return errors.New("owner role not found in database")
	}
	owner.RoleID = &ownerRole.ID

	if err := r.db.Create(owner).Error; err != nil {
		r.logger.WithError(err).Error("Failed to create owner")
		return err
	}
	return nil
}

func (r *ownerRepositoryImpl) GetOwnerByID(id uint) (*models.User, error) {
	var owner models.User

	err := r.db.Preload("Role").First(&owner, id).Error
	if err != nil {
		return nil, err
	}

	if owner.Role.Name != models.RoleOwner {
		return nil, gorm.ErrRecordNotFound
	}

	return &owner, nil
}

func (r *ownerRepositoryImpl) UpdateOwner(owner *models.User) error {
	if err := r.db.Save(owner).Error; err != nil {
		r.logger.WithError(err).Error("Failed to update owner")
		return err
	}
	return nil
}

func (r *ownerRepositoryImpl) GetAllOwners() ([]models.User, error) {
	var owners []models.User
	err := r.db.Joins("Role").Where(`"Role"."name" = ?`, models.RoleOwner).Find(&owners).Error
	if err != nil {
		return nil, err
	}
	return owners, nil
}

func (r *ownerRepositoryImpl) DeleteOwnerByID(id uint) error {
	if err := r.db.Delete(&models.User{}, id).Error; err != nil {
		r.logger.WithError(err).Error("Failed to delete owner by ID")
		return err
	}
	return nil
}

func (r *ownerRepositoryImpl) OwnerHasAssociations(id uint) (bool, error) {
	var count int64

	r.db.Model(&models.Page{}).Where("owner_id = ? OR published_by_staff_id = ?", id, id).Count(&count)
	if count > 0 {
		return true, nil
	}

	r.db.Model(&models.PageRequest{}).Where("owner_id = ? OR admin_id = ?", id, id).Count(&count)
	if count > 0 {
		return true, nil
	}

	r.db.Model(&models.Report{}).Where("generated_by_user_id = ?", id).Count(&count)
	if count > 0 {
		return true, nil
	}

	return false, nil
}

func (r *ownerRepositoryImpl) ForceDeleteOwner(id uint) error {
	return r.db.Transaction(func(tx *gorm.DB) error {
		if err := tx.Delete(&models.User{}, id).Error; err != nil {
			r.logger.WithError(err).Error("Failed to force delete user and their associations")
			return err
		}

		return nil
	})
}