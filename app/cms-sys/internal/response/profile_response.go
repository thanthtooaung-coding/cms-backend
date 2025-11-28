package response

import (
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"
)

type ProfileResponse struct {
	User         UserResponse            `json:"user"`
	PageRequests []PageRequestResponse   `json:"pageRequests"`
	Stats        ProfileStats            `json:"stats"`
}

type ProfileStats struct {
	TotalRequests    int `json:"totalRequests"`
	ApprovedRequests int `json:"approvedRequests"`
	PendingRequests  int `json:"pendingRequests"`
	RejectedRequests int `json:"rejectedRequests"`
}

func ToPageRequestResponseFromModel(pr *models.PageRequest) PageRequestResponse {
	resp := PageRequestResponse{
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

func ToProfileResponse(user models.User, pageRequests []*models.PageRequest) ProfileResponse {
	pageRequestResponses := make([]PageRequestResponse, 0) // Initialize as empty slice, not nil
	stats := ProfileStats{
		TotalRequests: len(pageRequests),
	}

	for _, pr := range pageRequests {
		prResp := ToPageRequestResponseFromModel(pr)
		pageRequestResponses = append(pageRequestResponses, prResp)
		
		switch pr.Status {
		case models.RequestApproved:
			stats.ApprovedRequests++
		case models.RequestPending:
			stats.PendingRequests++
		case models.RequestRejected:
			stats.RejectedRequests++
		}
	}

	return ProfileResponse{
		User:         ToUserResponse(user),
		PageRequests: pageRequestResponses,
		Stats:        stats,
	}
}

