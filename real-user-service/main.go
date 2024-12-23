package main

import (
	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
	"go.uber.org/zap"
	"os"

	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
	"real-user-service/config"
	"real-user-service/handlers"

	_ "real-user-service/docs" // Импортируйте сгенерированные Swagger документы
)

// @title          User Service API
// @version        1.0
// @description    Сервис Пользователей управляет информацией о пользователях системы.
// @termsOfService http://swagger.io/terms/

// @contact.name   API Support
// @contact.url    http://www.swagger.io/support
// @contact.email  support@swagger.io

// @license.name  MIT
// @license.url   https://opensource.org/licenses/MIT

// @host      localhost:8080
// @BasePath

// @securityDefinitions.apikey BearerAuth
// @in header
// @name Authorization
func main() {
	config.InitLogger()
	defer config.Logger.Sync()

	config.LoadConfig()

	db := config.GetDB()

	validate := validator.New()
	config.InitValidator(validate)

	h := handlers.NewHandler(db, validate)

	router := gin.Default()

	//router.Use(gin.Recovery())
	//router.Use(gin.LoggerWithWriter(zap.NewStdLog(config.Logger).Writer()))

	router.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))

	users := router.Group("/user-service/api/v1")
	{
		users.POST("/register", h.CreateUserHandler)
		users.GET("/users", h.GetAllUsersHandler)
		users.GET("/users/:id", h.GetUserByIDHandler)
		users.PUT("/users/:id", h.UpdateUserHandler)
		users.DELETE("/users/:id", h.DeleteUserHandler) // Роль "admin" больше не требуется
	}

	//users := router.Group("/user-service/api/v1")
	//{
	//	users.POST("/register", h.CreateUserHandler)
	//	users.GET("/users", middleware.AuthMiddleware(), h.GetAllUsersHandler)
	//	users.GET("/users/:id", middleware.AuthMiddleware(), h.GetUserByIDHandler)
	//	users.PUT("/users/:id", middleware.AuthMiddleware(), h.UpdateUserHandler)
	//	users.DELETE("/users/:id", middleware.AuthMiddleware(), middleware.RoleMiddleware("admin"), h.DeleteUserHandler)
	//}

	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}
	config.Logger.Info("Сервис запущен", zap.String("port", port))
	if err := router.Run(":" + port); err != nil {
		config.Logger.Fatal("Не удалось запустить сервер", zap.Error(err))
	}
}
