package response

type DashboardStatsResponse struct {
	TotalOwners          int64 `json:"totalOwners"`
	TotalPageRequests    int64 `json:"totalPageRequests"`
	PendingRequests      int64 `json:"pendingRequests"`
	ApprovedRequests     int64 `json:"approvedRequests"`
	RejectedRequests     int64 `json:"rejectedRequests"`
	TotalPages           int64 `json:"totalPages"`
	LMSRequests          int64 `json:"lmsRequests"`
	BMSRequests          int64 `json:"bmsRequests"`
	ECommerceRequests    int64 `json:"ecommerceRequests"`
}

