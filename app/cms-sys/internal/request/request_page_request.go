package request

import (
    "github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"
)

type CreatePageRequest struct {
	OwnerID     uint                  `json:"ownerId" validate:"required"`
	RequestType string                `json:"requestType"`
	Title       string                `json:"title"`
	Description string                `json:"description"`
	PageUrl     string               `json:"pageUrl"`
	LogoUrl    string               `json:"logoUrl"`
}

type ChangeStatusPageRequest struct {
	RequestID uint                      `json:"requestId" validate:"required"`
	UserID    uint                      `json:"userId" validate:"required"`
	Status    models.RequestStatus      `json:"status" validate:"required"`
}