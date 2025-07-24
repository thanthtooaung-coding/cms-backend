package response

import (
	"github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"
)

type RoleResponse struct {
	ID   uint   `json:"id"`
	Name string `json:"name"`
}

type OwnerResponse struct {
	ID          uint          `json:"id"`
	Username    string        `json:"username"`
	Email       string        `json:"email"`
	Role        *RoleResponse `json:"role,omitempty"`
	Address     *string       `json:"address,omitempty"`
	PhoneNumber *string       `json:"phone_number,omitempty"`
}

func ToOwnerResponse(user *models.User) OwnerResponse {
	ownerResp := OwnerResponse{
		ID:          user.ID,
		Username:    user.Username,
		Email:       user.Email,
		Address:     user.Address,
		PhoneNumber: user.PhoneNumber,
	}

	if user.RoleID != nil && user.Role.ID != 0 {
		ownerResp.Role = &RoleResponse{
			ID:   user.Role.ID,
			Name: string(user.Role.Name),
		}
	}

	return ownerResp
}

func ToOwnerListResponse(users []models.User) []OwnerResponse {
	var ownerList []OwnerResponse
	for _, user := range users {
		ownerList = append(ownerList, ToOwnerResponse(&user))
	}
	return ownerList
}
