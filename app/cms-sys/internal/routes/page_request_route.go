package routes

import (
	"github.com/gofiber/fiber/v2"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/handler"
)

func SetupPageRequestRoutes(app *fiber.App, handler handler.PageRequestHandle) {
	pageRequest := app.Group("/cms/page-request")
	pageRequest.Post("/", handler.Create)
	pageRequest.Get("/", handler.GetAll)
	pageRequest.Put("/status", handler.ChangeStatus)
}
