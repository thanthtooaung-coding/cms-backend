package service

import (
	"errors"
	"fmt"
	"github.com/sirupsen/logrus"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/repository"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/request"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/response"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/pkg/utils"
	"gorm.io/gorm"
)

type OwnerService interface {
	Create(req request.OwnerCreateRequest) (*response.OwnerResponse, error)
	Update(id uint, req request.OwnerUpdateRequest) (*response.OwnerResponse, error)
	GetAllOwners() ([]response.OwnerResponse, error)
	GetOwnerByID(id uint) (*response.OwnerResponse, error)
	BulkDeleteOwners(ids []uint, force bool) error
}

type ownerServiceImpl struct {
	log  *logrus.Logger
	repo repository.OwnerRepository
}

var _ OwnerService = (*ownerServiceImpl)(nil)

func NewOwnerService(log *logrus.Logger, repo repository.OwnerRepository) OwnerService {
	return &ownerServiceImpl{
		log:  log,
		repo: repo,
	}
}

func (os *ownerServiceImpl) Create(req request.OwnerCreateRequest) (*response.OwnerResponse, error) {
	hashedPassword, err := utils.HashPassword(req.Password)
	if err != nil {
		os.log.WithError(err).Error("Failed to hash password")
		return nil, errors.New("failed to process request")
	}

	newUser := &models.User{
		Username: req.Name,
		Password: hashedPassword,
		Email:    req.Email,
		Name:     &req.Name,
	}

	if err := os.repo.CreateOwner(newUser); err != nil {
		os.log.WithError(err).Error("Failed to create owner in repository")
		return nil, err
	}

	createdOwner, err := os.repo.GetOwnerByID(newUser.ID)
	if err != nil {
		os.log.WithError(err).Error("Failed to retrieve newly created owner")
		return nil, err
	}

	ownerResponse := response.ToOwnerResponse(createdOwner)
	return &ownerResponse, nil
}

func (os *ownerServiceImpl) Update(id uint, req request.OwnerUpdateRequest) (*response.OwnerResponse, error) {
	ownerToUpdate, err := os.repo.GetOwnerByID(id)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, fmt.Errorf("owner with ID %d not found", id)
		}
		os.log.WithError(err).Errorf("Failed to retrieve owner with ID %d for update", id)
		return nil, err
	}

	ownerToUpdate.Username = req.Name
	ownerToUpdate.Name = &req.Name

	if err := os.repo.UpdateOwner(ownerToUpdate); err != nil {
		os.log.WithError(err).Errorf("Failed to update owner with ID %d in repository", id)
		return nil, err
	}

	ownerResponse := response.ToOwnerResponse(ownerToUpdate)
	return &ownerResponse, nil
}

func (os *ownerServiceImpl) GetAllOwners() ([]response.OwnerResponse, error) {
	owners, err := os.repo.GetAllOwners()
	if err != nil {
		os.log.WithError(err).Error("Failed to get all owners from repository")
		return nil, err
	}

	return response.ToOwnerListResponse(owners), nil
}

func (os *ownerServiceImpl) GetOwnerByID(id uint) (*response.OwnerResponse, error) {
	owner, err := os.repo.GetOwnerByID(id)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, fmt.Errorf("owner with ID %d not found", id)
		}
		os.log.WithError(err).Errorf("Failed to get owner with ID %d from repository", id)
		return nil, err
	}

	ownerResponse := response.ToOwnerResponse(owner)
	return &ownerResponse, nil
}

func (os *ownerServiceImpl) BulkDeleteOwners(ids []uint, force bool) error {
	for _, id := range ids {
		owner, err := os.repo.GetOwnerByID(id)
		if err != nil {
			if errors.Is(err, gorm.ErrRecordNotFound) {
				return fmt.Errorf("owner with ID %d not found", id)
			}
			os.log.WithError(err).Errorf("Failed to retrieve owner with ID %d", id)
			return err
		}

		if !force {
			hasAssociations, err := os.repo.OwnerHasAssociations(id)
			if err != nil {
				os.log.WithError(err).Errorf("Failed to check associations for owner ID %d", id)
				return fmt.Errorf("could not check associations for owner ID %d", id)
			}
			if hasAssociations {
				return fmt.Errorf("owner '%s' has associated records and cannot be deleted without force", owner.Username)
			}
		}

		if force {
			if err := os.repo.ForceDeleteOwner(id); err != nil {
				os.log.WithError(err).Errorf("Failed to force delete owner with ID %d", id)
				return err
			}
		} else {
			if err := os.repo.DeleteOwnerByID(id); err != nil {
				os.log.WithError(err).Errorf("Failed to delete owner with ID %d", id)
				return err
			}
		}
		os.log.Infof("Successfully deleted owner with ID %d", id)
	}
	return nil
}

