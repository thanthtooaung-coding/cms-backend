package response

import (
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"
	"time"
)

type PageResponse struct {
	ID                 uint              `json:"id"`
	Title              *string           `json:"title"`
	ImageURL           *string           `json:"imageUrl"`
	Status             models.PageStatus `json:"status"`
	OwnerID            uint              `json:"ownerId"`
	Owner              *OwnerResponse    `json:"owner,omitempty"`
	PublishedByStaffID *uint             `json:"publishedByStaffId,omitempty"`
	PublishedByStaff   *OwnerResponse    `json:"publishedByStaff,omitempty"`
	CreatedAt          time.Time         `json:"createdAt"`
	UpdatedAt          time.Time         `json:"updatedAt"`
}
