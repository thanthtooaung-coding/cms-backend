package routes

import (
	"github.com/gofiber/fiber/v2"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/handler"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/pkg/utils"
)

func SetupDashboardRoutes(app *fiber.App, dashboardHandler *handler.DashboardHandler) {
	dashboardRoute := app.Group("/cms/dashboard")
	
	// Protected route - requires authentication
	dashboardRoute.Get("/stats", utils.AuthMiddleware, dashboardHandler.GetDashboardStats)
}

