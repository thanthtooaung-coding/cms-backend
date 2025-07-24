package utils

import (
	"fmt"
	"github.com/gofiber/fiber/v2"
	"strconv"
)

func ParseUintParam(c *fiber.Ctx, paramName string) (uint, error) {
	paramStr := c.Params(paramName)
	if paramStr == "" {
		return 0, fmt.Errorf("missing required URL parameter: %s", paramName)
	}

	id64, err := strconv.ParseUint(paramStr, 10, 32)
	if err != nil {
		return 0, fmt.Errorf("invalid format for URL parameter '%s': expected a positive number", paramName)
	}

	return uint(id64), nil
}
