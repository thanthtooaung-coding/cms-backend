package response

type TenantInfoResponse struct {
	TenantID   uint   `json:"tenantId"`
	TenantName string `json:"tenantName"`
	PageID     uint   `json:"pageId"`
	PageTitle  string `json:"pageTitle"`
	PageUrl    string `json:"pageUrl"`
	LogoUrl    string `json:"logoUrl"`
	OwnerID    uint   `json:"ownerId"`
	OwnerEmail string `json:"ownerEmail"`
}

