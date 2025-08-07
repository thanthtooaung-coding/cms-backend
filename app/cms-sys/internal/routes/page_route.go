package routes

import (
	"github.com/gofiber/fiber/v2"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/handler"
)

func SetupPageRoutes(app *fiber.App, handler handler.PageHandle) {
	pageRoute := app.Group("/cms/pages")
	pageRoute.Post("/", handler.Create)
	pageRoute.Put("/:id", handler.Update)
	pageRoute.Get("/", handler.GetAll)
	pageRoute.Get("/:id", handler.GetByID)
	pageRoute.Delete("/", handler.Delete)
	pageRoute.Patch("/status", handler.ChangePageStatus)
}
