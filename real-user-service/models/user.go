package models

import (
	"time"
)

type User struct {
	ID           int       `gorm:"primaryKey;autoIncrement" json:"id"`
	Login        string    `gorm:"type:varchar(255);unique;not null" json:"login" validate:"required,min=3,max=20,matches"`
	Email        string    `gorm:"type:varchar(255);unique;not null" json:"email" validate:"required,email,max=255"`
	PasswordHash string    `gorm:"not null" json:"-"`
	Role         string    `gorm:"type:user_role;not null;default:user" json:"role" validate:"required,oneof=admin user hr"`
	Status       string    `gorm:"type:user_status;not null;default:active" json:"status" validate:"required,oneof=active blocked"`
	CreatedAt    time.Time `gorm:"autoCreateTime" json:"created_at"`
	UpdatedAt    time.Time `gorm:"autoUpdateTime" json:"updated_at"`
}

type UserCreateRequest struct {
	Login    string `json:"login" validate:"required,min=3,max=20,matches"`
	Email    string `json:"email" validate:"required,email,max=255"`
	Password string `json:"password" validate:"required,min=8,max=64"`
	Role     string `json:"role" validate:"required,oneof=admin user hr"`
}

type UserUpdateRequest struct {
	Login    string `json:"login" validate:"required,min=3,max=20,matches"`
	Email    string `json:"email" validate:"required,email,max=255"`
	Password string `json:"password" validate:"omitempty,min=8,max=64"`
	Role     string `json:"role" validate:"required,oneof=admin user hr"`
	Status   string `json:"status" validate:"required,oneof=active blocked"`
}

type UserResponse struct {
	ID        int    `json:"id"`
	Login     string `json:"login"`
	Email     string `json:"email"`
	Role      string `json:"role"`
	Status    string `json:"status"`
	CreatedAt string `json:"created_at"`
	UpdatedAt string `json:"updated_at"`
}
