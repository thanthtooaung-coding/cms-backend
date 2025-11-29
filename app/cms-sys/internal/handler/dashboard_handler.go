package handler

import (
	"github.com/gofiber/fiber/v2"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/service"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/pkg/utils"
)

type DashboardHandler struct {
	service service.DashboardService
}

func NewDashboardHandler(service service.DashboardService) *DashboardHandler {
	return &DashboardHandler{
		service: service,
	}
}

func (h *DashboardHandler) GetDashboardStats(c *fiber.Ctx) error {
	stats, err := h.service.GetDashboardStats()
	if err != nil {
		return utils.InternalServerErrorResponse(c, "Failed to get dashboard statistics", err.Error())
	}

	return utils.SuccessResponse(c, "Dashboard statistics retrieved successfully", stats)
}

