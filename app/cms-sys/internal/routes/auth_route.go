package routes

import (
	"github.com/gofiber/fiber/v2"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/handler"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/pkg/utils"
)

func SetupAuthRoutes(app *fiber.App, authHandler handler.AuthHandle) {
	authRoute := app.Group("/cms/auth")
	
	// Public routes
	authRoute.Post("/login", authHandler.Login)
	authRoute.Post("/signup", authHandler.Signup)
	
	// Protected routes
	authRoute.Get("/me", utils.AuthMiddleware, authHandler.GetCurrentUser)
	authRoute.Get("/profile", utils.AuthMiddleware, authHandler.GetProfile)
}



