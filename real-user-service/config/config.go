package config

import (
	"fmt"
	"go.uber.org/zap"
	"os"

	"github.com/joho/godotenv"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
)

var JWTSecret string

func LoadConfig() {
	err := godotenv.Load()
	if err != nil {
		Logger.Warn("Нет файла .env, используем переменные окружения из системы")
	}

	JWTSecret = os.Getenv("JWT_SECRET")
	if JWTSecret == "" {
		Logger.Fatal("JWT_SECRET не установлен в переменных окружения")
	}
}

func GetDB() *gorm.DB {
	dsn := fmt.Sprintf("host=%s user=%s password=%s dbname=%s port=%s sslmode=%s TimeZone=%s",
		os.Getenv("DB_HOST"),
		os.Getenv("DB_USER"),
		os.Getenv("DB_PASSWORD"),
		os.Getenv("DB_NAME"),
		os.Getenv("DB_PORT"),
		os.Getenv("DB_SSLMODE"),
		os.Getenv("DB_TIMEZONE"),
	)
	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})
	if err != nil {
		Logger.Fatal("Не удалось подключиться к БД", zap.Error(err))
	}
	Logger.Info("Успешно подключились к базе данных")
	return db
}
