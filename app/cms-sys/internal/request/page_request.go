package request

import "github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"

type PageCreateRequest struct {
	Title              string `json:"title" validate:"required"`
	ImageURL           string `json:"imageUrl"`
	OwnerID            uint   `json:"ownerId" validate:"required"`
	PublishedByStaffID *uint  `json:"publishedByStaffId"`
}

type PageUpdateRequest struct {
	Title    *string            `json:"title"`
	ImageURL *string            `json:"imageUrl"`
	Status   *models.PageStatus `json:"status"`
}

type PageDeleteRequest struct {
	IDs []uint `json:"ids" validate:"required"`
}

type ChangeStatusPage struct {
	PageID uint              `json:"pageId" validate:"required"`
	UserID uint              `json:"userId" validate:"required"`
	Status models.PageStatus `json:"status" validate:"required"`
}
