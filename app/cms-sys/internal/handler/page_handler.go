package handler

import (
	"github.com/go-playground/validator/v10"
	"github.com/gofiber/fiber/v2"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/request"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/service"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/pkg/utils"
)

type PageHandle interface {
	Create(c *fiber.Ctx) error
	Update(c *fiber.Ctx) error
	GetAll(c *fiber.Ctx) error
	GetByID(c *fiber.Ctx) error
	Delete(c *fiber.Ctx) error
	ChangePageStatus(c *fiber.Ctx) error
}

type pageHandlerImpl struct {
	service   service.PageService
	validator *validator.Validate
}

var _ PageHandle = (*pageHandlerImpl)(nil)

func NewPageHandler(service service.PageService) PageHandle {
	return &pageHandlerImpl{service: service, validator: validator.New()}
}

func (h *pageHandlerImpl) Create(c *fiber.Ctx) error {
	var req request.PageCreateRequest
	if err := c.BodyParser(&req); err != nil {
		return utils.BadRequestResponse(c, "Invalid request body", err.Error())
	}

	if err := h.validator.Struct(req); err != nil {
		return utils.BadRequestResponse(c, "Validation failed!", err.Error())
	}

	pageResponse, err := h.service.Create(req)
	if err != nil {
		return utils.InternalServerErrorResponse(c, "Failed to create page", err.Error())
	}

	return utils.CreatedResponse(c, "Page created", pageResponse)
}

func (h *pageHandlerImpl) Update(c *fiber.Ctx) error {
	id, err := utils.ParseUintParam(c, "id")
	if err != nil {
		return utils.BadRequestResponse(c, "Invalid page ID", err.Error())
	}

	var req request.PageUpdateRequest
	if err := c.BodyParser(&req); err != nil {
		return utils.BadRequestResponse(c, "Invalid request body", err.Error())
	}

	if err := h.validator.Struct(req); err != nil {
		return utils.BadRequestResponse(c, "Validation failed!", err.Error())
	}

	pageResponse, err := h.service.Update(id, req)
	if err != nil {
		return utils.InternalServerErrorResponse(c, "Failed to update page", err.Error())
	}

	return utils.SuccessResponse(c, "Page updated", pageResponse)
}

func (h *pageHandlerImpl) GetAll(c *fiber.Ctx) error {
	var req request.PaginateRequest
	if err := c.QueryParser(&req); err != nil {
		return utils.BadRequestResponse(c, "Invalid query parameters", err.Error())
	}

	if req.Page <= 0 {
		req.Page = 1
	}
	if req.Limit <= 0 {
		req.Limit = 10
	}

	pages, pagination, err := h.service.GetAll(&req)
	if err != nil {
		return utils.InternalServerErrorResponse(c, "Failed to get pages", err.Error())
	}

	return utils.PaginatedSuccessResponse(c, "Pages retrieved successfully", pages, *pagination)
}

func (h *pageHandlerImpl) GetByID(c *fiber.Ctx) error {
	id, err := utils.ParseUintParam(c, "id")
	if err != nil {
		return utils.BadRequestResponse(c, "Invalid page ID", err.Error())
	}

	pageResponse, err := h.service.GetByID(id)
	if err != nil {
		return utils.NotFoundResponse(c, "Page not found")
	}

	return utils.SuccessResponse(c, "Fetched page", pageResponse)
}

func (h *pageHandlerImpl) Delete(c *fiber.Ctx) error {
	var req request.PageDeleteRequest
	if err := c.BodyParser(&req); err != nil {
		return utils.BadRequestResponse(c, "Invalid request", err.Error())
	}

	if err := h.validator.Struct(req); err != nil {
		return utils.BadRequestResponse(c, "Validation failed", err.Error())
	}

	err := h.service.Delete(req.IDs)
	if err != nil {
		return utils.InternalServerErrorResponse(c, "Failed to delete pages", err.Error())
	}

	return utils.SuccessResponse(c, "Page(s) deleted", nil)
}

func (h *pageHandlerImpl) ChangePageStatus(c *fiber.Ctx) error {
	var req request.ChangeStatusPage

	if err := c.BodyParser(&req); err != nil {
		return utils.BadRequestResponse(c, "Invalid request body", err.Error())
	}

	if err := h.validator.Struct(req); err != nil {
		return utils.BadRequestResponse(c, "Validation failed", err.Error())
	}

	if !utils.IsValidPageStatus(req.Status) {
		return utils.BadRequestResponse(c, "Invalid page status", "Status must be Draft, Published, or Archived")
	}

	if err := h.service.ChangeStatus(req); err != nil {
		return utils.InternalServerErrorResponse(c, "Failed to change page status", err.Error())
	}

	return utils.SuccessResponse(c, "Page status updated successfully", nil)
}
