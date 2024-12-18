package models

import (
	"time"

	"github.com/google/uuid"
	"gorm.io/gorm"
)

type User struct {
	ID           uuid.UUID      `gorm:"type:uuid;primaryKey;" json:"id"`
	Username     string         `gorm:"uniqueIndex;not null" json:"username" validate:"required,min=3,max=25"`
	Email        string         `gorm:"uniqueIndex;not null" json:"email" validate:"required,email,max=256"`
	PasswordHash string         `gorm:"not null" json:"-"`
	Team         string         `json:"team" validate:"omitempty,max=100"`
	Role         string         `json:"role" validate:"required,oneof=admin user hr"`
	CreatedAt    time.Time      `json:"created_at" gorm:"autoCreateTime"`
	UpdatedAt    time.Time      `json:"updated_at" gorm:"autoUpdateTime"`
	DeletedAt    gorm.DeletedAt `json:"-" gorm:"index"`
}

type UserCreateRequest struct {
	Username string `json:"username" validate:"required,min=3,max=25"`
	Email    string `json:"email" validate:"required,email,max=256"`
	Password string `json:"password" validate:"required,min=8,max=64"`
	Team     string `json:"team" validate:"omitempty,max=100"`
	Role     string `json:"role" validate:"required,oneof=admin user hr"`
}

type UserUpdateRequest struct {
	Username string `json:"username" validate:"required,min=3,max=25"`
	Email    string `json:"email" validate:"required,email,max=256"`
	Password string `json:"password" validate:"omitempty,min=8,max=64"`
	Team     string `json:"team" validate:"omitempty,max=100"`
	Role     string `json:"role" validate:"required,oneof=admin user hr"`
}

type UserResponse struct {
	ID        string `json:"id"`
	Username  string `json:"username"`
	Email     string `json:"email"`
	Team      string `json:"team"`
	Role      string `json:"role"`
	CreatedAt string `json:"created_at"`
	UpdatedAt string `json:"updated_at"`
}
