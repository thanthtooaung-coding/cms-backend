package handler

import (
	"github.com/go-playground/validator/v10"
	"github.com/gofiber/fiber/v2"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/service"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/request"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/pkg/utils"
)

type OwnerHandle interface {
	Create(c *fiber.Ctx) error
	Update(c *fiber.Ctx) error
	GetAll(c *fiber.Ctx) error
	GetOwnerByID(c *fiber.Ctx) error
	Delete(c *fiber.Ctx) error
}
type OwnerHandler struct {
	service   service.OwnerService
	validator *validator.Validate
}

var _ OwnerHandle = (*OwnerHandler)(nil)

func NewOwnerHandler(service service.OwnerService) OwnerHandle {
	return &OwnerHandler{service: service, validator: validator.New()}
}

func (o OwnerHandler) Create(c *fiber.Ctx) error {
	var req request.OwnerCreateRequest
	if err := c.BodyParser(&req); err != nil {
		return utils.BadRequestResponse(c, "Invalid request body", err.Error())
	}

	if err := o.validator.Struct(req); err != nil {
		return utils.BadRequestResponse(c, "Validation failed!", err.Error())
	}

	ownerResponse, err := o.service.Create(req)
	if err != nil {
		return utils.InternalServerErrorResponse(c, "Failed to create owner", err.Error())
	}

	return utils.CreatedResponse(c, "Owner created", ownerResponse)
}

func (o OwnerHandler) Update(c *fiber.Ctx) error {
	id, err := utils.ParseUintParam(c, "id")
	var req request.OwnerUpdateRequest

	if err := c.BodyParser(&req); err != nil {
		return utils.BadRequestResponse(c, "Invalid request body", err.Error())
	}
	if err := o.validator.Struct(req); err != nil {
		return utils.BadRequestResponse(c, "Validation failed!", err.Error())
	}

	ownerResponse, err := o.service.Update(id, req)
	if err != nil {
		return utils.InternalServerErrorResponse(c, "Failed to update owner", err.Error())
	}

	return utils.SuccessResponse(c, "Owner updated", ownerResponse)
}

func (o OwnerHandler) GetAll(c *fiber.Ctx) error {
	owners, err := o.service.GetAllOwners()
	if err != nil {
		return utils.InternalServerErrorResponse(c, "Failed to get owners", err.Error())
	}

	return utils.SuccessResponse(c, "Owners fetched", owners)
}

func (o OwnerHandler) GetOwnerByID(c *fiber.Ctx) error {
	id, err := utils.ParseUintParam(c, "id")

	ownerResponse, err := o.service.GetOwnerByID(id)
	if err != nil {
		return utils.InternalServerErrorResponse(c, "Failed to fetch owner", err.Error())
	}

	return utils.SuccessResponse(c, "Fetched owner", ownerResponse)
}

func (o OwnerHandler) Delete(c *fiber.Ctx) error {
	var req request.OwnerDeleteRequest

	if err := c.BodyParser(&req); err != nil {
		return utils.BadRequestResponse(c, "Invalid request", err.Error())
	}

	if err := o.validator.Struct(req); err != nil {
		return utils.BadRequestResponse(c, "Validation failed", err.Error())
	}

	err := o.service.BulkDeleteOwners(req.IDs, req.ForceDelete)
	if err != nil {
		return utils.InternalServerErrorResponse(c, "Failed to delete owners", err.Error())
	}

	return utils.SuccessResponse(c, "Owner(s) deleted", nil)
}
