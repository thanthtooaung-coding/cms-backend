package request

type OwnerCreateRequest struct {
	Name      string `json:"name" validate:"required,min=2"`
	Email     string `json:"email" validate:"required,email"`
	Password  string `json:"password" validate:"required,min=6"`
}

type OwnerUpdateRequest struct {
	Name      string `json:"name" validate:"required,min=2"`
}

type OwnerDeleteRequest struct {
	IDs         []uint `json:"ids" validate:"required"`
	ForceDelete bool     `json:"forceDelete"`
}