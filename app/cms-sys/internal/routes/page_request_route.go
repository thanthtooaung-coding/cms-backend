package routes

import (
	"github.com/gofiber/fiber/v2"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/handler"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/pkg/utils"
)

func SetupPageRequestRoutes(app *fiber.App, handler handler.PageRequestHandle) {
	pageRequest := app.Group("/cms/page-request")
	pageRequest.Post("/", utils.AuthMiddleware, handler.Create) // Protected route
	pageRequest.Get("/", handler.GetAll)
	pageRequest.Put("/status", utils.AuthMiddleware, handler.ChangeStatus) // Protected route
	pageRequest.Get("/tenant/:slug", handler.GetTenantInfoBySlug) // Public route for tenant lookup
}
