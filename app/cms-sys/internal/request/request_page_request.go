package request

import (
    "github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"
)

type CreatePageRequest struct {
	OwnerID     uint                  `json:"ownerId"` // Will be set from JWT token, not required in request
	RequestType string                `json:"requestType" validate:"required"`
	Title       string                `json:"title" validate:"required"`
	Description string                `json:"description"`
	PageUrl     string               `json:"pageUrl" validate:"required"`
	LogoUrl    string               `json:"logoUrl" validate:"required"`
}

type ChangeStatusPageRequest struct {
	RequestID uint                      `json:"requestId" validate:"required"`
	Status    models.RequestStatus      `json:"status" validate:"required"`
}