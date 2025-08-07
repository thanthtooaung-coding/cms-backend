package utils

import "github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/models"

func IsValidPageStatus(status models.PageStatus) bool {
	switch status {
	case models.PageDraft,
		models.PagePublished,
		models.PageArchived:
		return true
	default:
		return false
	}
}
