package request

import (
    "github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"
    "mime/multipart"
)

type CreatePageRequest struct {
	OwnerID     uint                  `json:"ownerId" validate:"required"`
	RequestType string                `json:"requestType"`
	Title       string                `json:"title"`
	Description string                `json:"description"`
	PageUrl     string               `json:"pageUrl"`
	LogoFile    *multipart.FileHeader `json:"logo"`
}

type ChangeStatusPageRequest struct {
	RequestID uint                      `json:"requestId" validate:"required"`
	UserID    uint                      `json:"userId" validate:"required"`
	Status    models.RequestStatus      `json:"status" validate:"required"`
}