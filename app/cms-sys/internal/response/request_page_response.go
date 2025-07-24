package response

import (
    "github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"
    "time"
)

type PageRequestResponse struct {
	ID          uint      `json:"id"`
	OwnerID     uint      `json:"ownerId"`
	RequestType string    `json:"requestType"`
	Title       string    `json:"title"`
	Status		models.RequestStatus	`json:"status"`
	PageUrl     string   `json:"pageUrl"`
	LogoUrl     string   `json:"logoUrl"`
	CreatedAt   time.Time `json:"createdAt"`
}