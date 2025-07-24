package handler

import (
	"github.com/go-playground/validator/v10"
	"github.com/gofiber/fiber/v2"

	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/service"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/request"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/pkg/utils"
)

type PageRequestHandle interface {
	Create(c *fiber.Ctx) error
	GetAll(c *fiber.Ctx) error
	ChangeStatus(c *fiber.Ctx) error
}

type PageRequestHandler struct {
	service   service.PageRequestService
	validator *validator.Validate
}

var _ PageRequestHandle = (*PageRequestHandler)(nil)

func NewPageRequestHandler(
	service service.PageRequestService,
) PageRequestHandle {
	return &PageRequestHandler{
		service:   service,
		validator: validator.New(),
	}
}

func (h *PageRequestHandler) Create(c *fiber.Ctx) error {
	var req request.CreatePageRequest
	if form, err := c.MultipartForm(); err != nil {
		if form == nil {
			return utils.BadRequestResponse(c, "Invalid request body", err.Error())
		}
		if files := form.File["logo"]; len(files) > 0 {
			req.LogoFile = files[0]
		}
	}

	if err := c.BodyParser(&req); err != nil {
		return utils.BadRequestResponse(c, "Invalid request body", err.Error())
	}

	if err := h.validator.Struct(req); err != nil {
		return utils.BadRequestResponse(c, "Validation failed", err.Error())
	}

	var logoURL string
	if req.LogoFile != nil {
// 		uploadedURL, err := h.s3Service.UploadFile(req.LogoFile)
// 		if err != nil {
// 			return utils.InternalServerErrorResponse(c, "Failed to upload logo", err.Error())
// 		}
		logoURL = "abcd"
	}

	pageRequestResponse, err := h.service.CreatePageRequest(req, logoURL)
	if err != nil {
		return utils.InternalServerErrorResponse(c, "Failed to create page request", err.Error())
	}

	return utils.CreatedResponse(c, "Page request created", pageRequestResponse)
}

func (h *PageRequestHandler) GetAll(c *fiber.Ctx) error {
	var req request.PaginateRequest
	if err := c.QueryParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"error": "Invalid query parameters",
		})
	}

	if req.Page <= 0 {
		req.Page = 1
	}
	if req.Limit <= 0 {
		req.Limit = 10
	}

	pageRequests, pagination, err := h.service.GetAllPageRequests(&req)
	if err != nil {
		return utils.InternalServerErrorResponse(c, "Failed to get page requests", err.Error())
	}

	return utils.PaginatedSuccessResponse(c, "Page requests retrieved successfully", pageRequests, *pagination)
}

func (h *PageRequestHandler) ChangeStatus(c *fiber.Ctx) error {
	var req request.ChangeStatusPageRequest

	if err := c.BodyParser(&req); err != nil {
		return utils.BadRequestResponse(c, "Invalid request body", err.Error())
	}

	if err := h.validator.Struct(req); err != nil {
		return utils.BadRequestResponse(c, "Validation failed", err.Error())
	}

	if !utils.IsValidRequestStatus(req.Status) {
		return utils.BadRequestResponse(c, "Invalid request status", "Status must be PENDING, APPROVED or REJECTED")
	}

	if err := h.service.ChangeStatus(req, req.UserID); err != nil {
		return utils.InternalServerErrorResponse(c, "Failed to change page request status", err.Error())
	}

	return utils.SuccessResponse(c, "Page request status updated successfully", nil)
}
