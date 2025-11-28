package response

import "github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"

type LoginResponse struct {
	Token string       `json:"token"`
	User  UserResponse `json:"user"`
}

type SignupResponse struct {
	Token string       `json:"token"`
	User  UserResponse `json:"user"`
}

type UserResponse struct {
	ID          uint    `json:"id"`
	Username    string  `json:"username"`
	Email       string  `json:"email"`
	Name        *string `json:"name"`
	RoleID      *uint   `json:"role_id"`
	Role        *RoleResponse `json:"role"`
	Address     *string `json:"address"`
	PhoneNumber *string `json:"phone_number"`
}

// RoleResponse is defined in owner_response.go to avoid duplication

func ToUserResponse(user models.User) UserResponse {
	var roleResponse *RoleResponse
	if user.Role.ID != 0 {
		roleResponse = &RoleResponse{
			ID:   user.Role.ID,
			Name: string(user.Role.Name),
		}
	}

	return UserResponse{
		ID:          user.ID,
		Username:    user.Username,
		Email:       user.Email,
		Name:        user.Name,
		RoleID:      user.RoleID,
		Role:        roleResponse,
		Address:     user.Address,
		PhoneNumber: user.PhoneNumber,
	}
}

