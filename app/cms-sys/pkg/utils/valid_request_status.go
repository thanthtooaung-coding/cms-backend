package utils

import "github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"

func IsValidRequestStatus(status models.RequestStatus) bool {
	switch status {
	case models.RequestPending,
		models.RequestApproved,
		models.RequestRejected:
		return true
	default:
		return false
	}
}
