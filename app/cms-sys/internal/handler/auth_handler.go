package handler

import (
	"github.com/go-playground/validator/v10"
	"github.com/gofiber/fiber/v2"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/request"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/service"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/pkg/utils"
)

type AuthHandle interface {
	Login(c *fiber.Ctx) error
	Signup(c *fiber.Ctx) error
	GetCurrentUser(c *fiber.Ctx) error
	GetProfile(c *fiber.Ctx) error
}

type AuthHandler struct {
	service   service.AuthService
	validator *validator.Validate
}

var _ AuthHandle = (*AuthHandler)(nil)

func NewAuthHandler(service service.AuthService) AuthHandle {
	return &AuthHandler{service: service, validator: validator.New()}
}

func (a *AuthHandler) Login(c *fiber.Ctx) error {
	var req request.LoginRequest
	if err := c.BodyParser(&req); err != nil {
		return utils.BadRequestResponse(c, "Invalid request body", err.Error())
	}

	if err := a.validator.Struct(req); err != nil {
		return utils.BadRequestResponse(c, "Validation failed", err.Error())
	}

	loginResponse, err := a.service.Login(req)
	if err != nil {
		return utils.UnauthorizedResponse(c, err.Error())
	}

	return utils.SuccessResponse(c, "Login successful", loginResponse)
}

func (a *AuthHandler) Signup(c *fiber.Ctx) error {
	var req request.SignupRequest
	if err := c.BodyParser(&req); err != nil {
		return utils.BadRequestResponse(c, "Invalid request body", err.Error())
	}

	if err := a.validator.Struct(req); err != nil {
		return utils.BadRequestResponse(c, "Validation failed", err.Error())
	}

	signupResponse, err := a.service.Signup(req)
	if err != nil {
		return utils.BadRequestResponse(c, "Signup failed", err.Error())
	}

	return utils.CreatedResponse(c, "User created successfully", signupResponse)
}

func (a *AuthHandler) GetCurrentUser(c *fiber.Ctx) error {
	// Get user ID from context (set by auth middleware)
	userID, ok := c.Locals("userID").(uint)
	if !ok {
		return utils.UnauthorizedResponse(c, "User not authenticated")
	}

	userResponse, err := a.service.GetUserByID(userID)
	if err != nil {
		return utils.NotFoundResponse(c, "User not found")
	}

	return utils.SuccessResponse(c, "User fetched", userResponse)
}

func (a *AuthHandler) GetProfile(c *fiber.Ctx) error {
	// Get user ID from context (set by auth middleware)
	userID, ok := c.Locals("userID").(uint)
	if !ok {
		return utils.UnauthorizedResponse(c, "User not authenticated")
	}

	profileResponse, err := a.service.GetUserProfile(userID)
	if err != nil {
		return utils.NotFoundResponse(c, "Profile not found")
	}

	return utils.SuccessResponse(c, "Profile fetched", profileResponse)
}



