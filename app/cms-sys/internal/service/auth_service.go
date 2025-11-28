package service

import (
	"errors"
	"github.com/sirupsen/logrus"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/repository"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/request"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/response"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/pkg/utils"
)

type AuthService interface {
	Login(req request.LoginRequest) (*response.LoginResponse, error)
	Signup(req request.SignupRequest) (*response.SignupResponse, error)
	GetUserByID(userID uint) (*response.UserResponse, error)
	GetUserProfile(userID uint) (*response.ProfileResponse, error)
}

type authServiceImpl struct {
	logger           *logrus.Logger
	repo             repository.AuthRepository
	pageRequestRepo  repository.PageRequestRepository
}

var _ AuthService = (*authServiceImpl)(nil)

func NewAuthService(logger *logrus.Logger, repo repository.AuthRepository, pageRequestRepo repository.PageRequestRepository) AuthService {
	return &authServiceImpl{
		logger:          logger,
		repo:            repo,
		pageRequestRepo: pageRequestRepo,
	}
}

func (s *authServiceImpl) Login(req request.LoginRequest) (*response.LoginResponse, error) {
	// Get user by username
	user, err := s.repo.GetUserByUsername(req.Username)
	if err != nil {
		s.logger.WithError(err).Error("Failed to get user by username")
		return nil, errors.New("invalid username or password")
	}

	// Check password
	if err := utils.CheckPassword(req.Password, user.Password); err != nil {
		s.logger.WithError(err).Error("Invalid password")
		return nil, errors.New("invalid username or password")
	}

	// Generate JWT token
	token, err := utils.GenerateToken(user.ID, user.Username, user.RoleID)
	if err != nil {
		s.logger.WithError(err).Error("Failed to generate token")
		return nil, errors.New("failed to generate token")
	}

	return &response.LoginResponse{
		Token: token,
		User:  response.ToUserResponse(*user),
	}, nil
}

func (s *authServiceImpl) Signup(req request.SignupRequest) (*response.SignupResponse, error) {
	// Check if username already exists
	existingUser, _ := s.repo.GetUserByUsername(req.Username)
	if existingUser != nil {
		return nil, errors.New("username already exists")
	}

	// Check if email already exists
	existingEmail, _ := s.repo.GetUserByEmail(req.Email)
	if existingEmail != nil {
		return nil, errors.New("email already exists")
	}

	// Hash password
	hashedPassword, err := utils.HashPassword(req.Password)
	if err != nil {
		s.logger.WithError(err).Error("Failed to hash password")
		return nil, errors.New("failed to process password")
	}

	// Determine role - default to Owner if not provided
	var roleID *uint
	if req.RoleID != nil {
		roleID = req.RoleID
	} else {
		// Default to Owner role - we'll need to get it from a role repository
		// For now, we'll set it to nil and let the repository handle it
		// The repository should set default role
		roleID = nil
	}

	// Create user
	user := &models.User{
		Username:    req.Username,
		Password:    hashedPassword,
		Email:       req.Email,
		Name:        req.Name,
		Address:     req.Address,
		PhoneNumber: req.PhoneNumber,
		RoleID:      roleID,
	}

	if err := s.repo.CreateUser(user); err != nil {
		s.logger.WithError(err).Error("Failed to create user")
		return nil, errors.New("failed to create user")
	}

	// Generate JWT token
	token, err := utils.GenerateToken(user.ID, user.Username, user.RoleID)
	if err != nil {
		s.logger.WithError(err).Error("Failed to generate token")
		return nil, errors.New("failed to generate token")
	}

	return &response.SignupResponse{
		Token: token,
		User:  response.ToUserResponse(*user),
	}, nil
}

func (s *authServiceImpl) GetUserByID(userID uint) (*response.UserResponse, error) {
	user, err := s.repo.GetUserByID(userID)
	if err != nil {
		s.logger.WithError(err).Error("Failed to get user by ID")
		return nil, errors.New("user not found")
	}

	userResp := response.ToUserResponse(*user)
	return &userResp, nil
}

func (s *authServiceImpl) GetUserProfile(userID uint) (*response.ProfileResponse, error) {
	// Get user
	user, err := s.repo.GetUserByID(userID)
	if err != nil {
		s.logger.WithError(err).Error("Failed to get user by ID")
		return nil, errors.New("user not found")
	}

	// Get user's page requests
	pageRequests, err := s.pageRequestRepo.GetByOwnerID(userID)
	if err != nil {
		s.logger.WithError(err).Error("Failed to get page requests by owner ID")
		return nil, errors.New("failed to get page requests")
	}

	profileResp := response.ToProfileResponse(*user, pageRequests)
	return &profileResp, nil
}

