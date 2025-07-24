package models

import (
    "time"
)

type RoleName string
type PageStatus string
type RequestStatus string

const (
    RoleOwner RoleName = "Owner"
    RoleAdmin RoleName = "Admin"
    RoleStaff RoleName = "Staff"
)

const (
    PageDraft    PageStatus = "Draft"
    PagePublished PageStatus = "Published"
    PageArchived  PageStatus = "Archived"
)

const (
    RequestPending  RequestStatus = "Pending"
    RequestApproved RequestStatus = "Approved"
    RequestRejected RequestStatus = "Rejected"
)

type Role struct {
    ID        uint      `gorm:"primaryKey" json:"id"`
    Name      RoleName  `gorm:"type:role_name;not null;unique" json:"name"`
    CreatedAt time.Time `json:"created_at"`
    UpdatedAt time.Time `json:"updated_at"`
}

func (Role) TableName() string {
    return "Role"
}

type User struct {
    ID          uint      `gorm:"primaryKey" json:"id"`
    Username    string    `gorm:"size:255;unique;not null" json:"username"`
    Password    string    `gorm:"size:255;not null" json:"-"`
    Email       string    `gorm:"size:255;unique;not null" json:"email"`
    Name        *string   `gorm:"size:255" json:"name"`
    RoleID      *uint     `json:"role_id"`
    Address     *string   `gorm:"type:text" json:"address"`
    PhoneNumber *string   `gorm:"size:50" json:"phone_number"`
    CreatedAt   time.Time `json:"created_at"`
    UpdatedAt   time.Time `json:"updated_at"`

    Role Role `gorm:"foreignKey:RoleID" json:"role"`
}

func (User) TableName() string {
    return "User"
}

type Page struct {
    ID                 uint       `gorm:"primaryKey" json:"id"`
    Title              *string    `gorm:"size:255" json:"title"`
    ImageURL           *string    `gorm:"size:2048" json:"image_url"`
    Status             PageStatus `gorm:"type:page_status;default:'Draft'" json:"status"`
    OwnerID            uint       `gorm:"not null" json:"owner_id"`
    PublishedByStaffID *uint      `json:"published_by_staff_id"`
    CreatedAt          time.Time  `json:"created_at"`
    UpdatedAt          time.Time  `json:"updated_at"`

    Owner            User `gorm:"foreignKey:OwnerID" json:"owner"`
    PublishedByStaff User `gorm:"foreignKey:PublishedByStaffID" json:"published_by_staff"`
}

func (Page) TableName() string {
    return "Page"
}

type PageRequest struct {
    ID             uint          `gorm:"primaryKey" json:"id"`
    OwnerID        uint          `gorm:"not null" json:"owner_id"`
    RequestType    string        `gorm:"size:255;not null" json:"request_type"`
    RequestDetails *string       `gorm:"type:text" json:"request_details"`
    RequestDate    time.Time     `json:"request_date"`
    Status         RequestStatus `gorm:"type:request_status;default:'Pending'" json:"status"`
    AdminID        *uint         `json:"admin_id"`
    CreatedAt      time.Time     `json:"created_at"`
    UpdatedAt      time.Time     `json:"updated_at"`

    Owner User `gorm:"foreignKey:OwnerID" json:"owner"`
    Admin User `gorm:"foreignKey:AdminID" json:"admin"`
}

func (PageRequest) TableName() string {
    return "Page_Request"
}

type Report struct {
    ID                 uint      `gorm:"primaryKey" json:"id"`
    ReportName         *string   `gorm:"size:255" json:"report_name"`
    GeneratedDate      time.Time `json:"generated_date"`
    GeneratedByUserID  uint      `gorm:"not null" json:"generated_by_user_id"`
    CreatedAt          time.Time `json:"created_at"`
    UpdatedAt          time.Time `json:"updated_at"`

    GeneratedByUser User `gorm:"foreignKey:GeneratedByUserID" json:"generated_by_user"`
}

func (Report) TableName() string {
    return "Report"
}