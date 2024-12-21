package config

import (
	"fmt"
	"github.com/go-playground/validator/v10"
	"go.uber.org/zap"
	"os"
	"regexp"

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

func matches(fl validator.FieldLevel) bool {
	// Регулярное выражение для поля 'Login'
	regex := regexp.MustCompile(`^[a-zA-Z0-9.]{3,20}$`)
	return regex.MatchString(fl.Field().String())
}

// Регистрация кастомных валидаторов
func InitValidator(validate *validator.Validate) {
	// Регистрация кастомного валидатора 'matches'
	err := validate.RegisterValidation("matches", matches)
	if err != nil {
		Logger.Fatal("Не удалось зарегистрировать валидатор 'matches'", zap.Error(err))
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
