package request

type PaginateRequest struct {
	Page  int `query:"page"`
	Limit int `query:"limit"`
}