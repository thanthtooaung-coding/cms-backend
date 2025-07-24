package mapper

import (
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/response"
)

func ToPageRequestResponse(model *models.PageRequest) *response.PageRequestResponse {
	return &response.PageRequestResponse{
		ID:          model.ID,
		OwnerID:     model.OwnerID,
		RequestType: model.RequestType,
		Title:       model.Title,
		Status:      model.Status,
		PageUrl:     model.PageUrl,
		LogoUrl:     model.LogoUrl,
		CreatedAt:   model.CreatedAt,
	}
}
