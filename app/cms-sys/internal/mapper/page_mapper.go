package mapper

import (
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/response"
)

func ToPageResponse(page *models.Page, pageUrl *string) *response.PageResponse {
	resp := &response.PageResponse{
		ID:                 page.ID,
		Title:              page.Title,
		ImageURL:           page.ImageURL,
		PageUrl:            pageUrl,
		Status:             page.Status,
		OwnerID:            page.OwnerID,
		PublishedByStaffID: page.PublishedByStaffID,
		CreatedAt:          page.CreatedAt,
		UpdatedAt:          page.UpdatedAt,
	}

	if page.Owner.ID != 0 {
		ownerResp := response.ToOwnerResponse(&page.Owner)
		resp.Owner = &ownerResp
	}

	if page.PublishedByStaffID != nil && page.PublishedByStaff.ID != 0 {
		staffResp := response.ToOwnerResponse(&page.PublishedByStaff)
		resp.PublishedByStaff = &staffResp
	}

	return resp
}

