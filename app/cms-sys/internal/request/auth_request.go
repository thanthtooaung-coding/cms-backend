package request

type LoginRequest struct {
	Username string `json:"username" validate:"required,min=3"`
	Password string `json:"password" validate:"required,min=7"`
}

type SignupRequest struct {
	Username    string  `json:"username" validate:"required,min=3"`
	Password    string  `json:"password" validate:"required,min=7"`
	Email       string  `json:"email" validate:"required,email"`
	Name        *string `json:"name"`
	Address     *string `json:"address"`
	PhoneNumber *string `json:"phone_number"`
	RoleID      *uint   `json:"role_id"` // Optional, defaults to Owner if not provided
}









