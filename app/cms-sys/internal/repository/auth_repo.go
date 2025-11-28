package repository

import (
	"errors"
	"github.com/sirupsen/logrus"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"
	"gorm.io/gorm"
)

type AuthRepository interface {
	GetUserByUsername(username string) (*models.User, error)
	GetUserByEmail(email string) (*models.User, error)
	CreateUser(user *models.User) error
	GetUserByID(id uint) (*models.User, error)
}

type authRepositoryImpl struct {
	logger *logrus.Logger
	db     *gorm.DB
}

var _ AuthRepository = (*authRepositoryImpl)(nil)

func NewAuthRepository(logger *logrus.Logger, db *gorm.DB) AuthRepository {
	return &authRepositoryImpl{logger: logger, db: db}
}

func (r *authRepositoryImpl) GetUserByUsername(username string) (*models.User, error) {
	var user models.User
	err := r.db.Preload("Role").Where("username = ?", username).First(&user).Error
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New("user not found")
		}
		r.logger.WithError(err).Error("Failed to get user by username")
		return nil, err
	}
	return &user, nil
}

func (r *authRepositoryImpl) GetUserByEmail(email string) (*models.User, error) {
	var user models.User
	err := r.db.Preload("Role").Where("email = ?", email).First(&user).Error
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New("user not found")
		}
		r.logger.WithError(err).Error("Failed to get user by email")
		return nil, err
	}
	return &user, nil
}

func (r *authRepositoryImpl) CreateUser(user *models.User) error {
	// If role ID is not provided, default to Owner role
	if user.RoleID == nil {
		var ownerRole models.Role
		if err := r.db.Where("name = ?", models.RoleOwner).First(&ownerRole).Error; err != nil {
			r.logger.WithError(err).Error("Failed to find 'Owner' role")
			return errors.New("owner role not found in database")
		}
		user.RoleID = &ownerRole.ID
	}

	if err := r.db.Create(user).Error; err != nil {
		r.logger.WithError(err).Error("Failed to create user")
		return err
	}
	// Reload to get role relationship
	return r.db.Preload("Role").First(user, user.ID).Error
}

func (r *authRepositoryImpl) GetUserByID(id uint) (*models.User, error) {
	var user models.User
	err := r.db.Preload("Role").First(&user, id).Error
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, errors.New("user not found")
		}
		r.logger.WithError(err).Error("Failed to get user by ID")
		return nil, err
	}
	return &user, nil
}

