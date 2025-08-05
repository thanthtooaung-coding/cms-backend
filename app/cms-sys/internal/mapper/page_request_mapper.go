package mapper

import (
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/response"
)

func ToPageRequestResponse(pr *models.PageRequest) *response.PageRequestResponse {
	resp := &response.PageRequestResponse{
		ID:          pr.ID,
		OwnerID:     pr.OwnerID,
		RequestType: pr.RequestType,
		Title:       pr.Title,
		Status:      pr.Status,
		PageUrl:     pr.PageUrl,
		LogoUrl:     pr.LogoUrl,
		CreatedAt:   pr.CreatedAt,
	}
	
	if pr.Owner.ID != 0 {
		resp.UserName = pr.Owner.Username
		resp.UserEmail = pr.Owner.Email
	}

	return resp
}
