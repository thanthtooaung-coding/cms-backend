package utils

import (
	"github.com/gofiber/fiber/v2"
)

func AuthMiddleware(c *fiber.Ctx) error {
	// Get token from Authorization header
	authHeader := c.Get("Authorization")
	if authHeader == "" {
		return UnauthorizedResponse(c, "Authorization header is required")
	}

	// Extract token from "Bearer <token>"
	tokenString := ""
	if len(authHeader) > 7 && authHeader[:7] == "Bearer " {
		tokenString = authHeader[7:]
	} else {
		return UnauthorizedResponse(c, "Invalid authorization header format")
	}

	// Validate token
	claims, err := ValidateToken(tokenString)
	if err != nil {
		return UnauthorizedResponse(c, "Invalid or expired token")
	}

	// Set user information in context
	c.Locals("userID", claims.UserID)
	c.Locals("username", claims.Username)
	c.Locals("roleID", claims.RoleID)

	return c.Next()
}











