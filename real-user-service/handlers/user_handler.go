package handlers

import (
	"strconv"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
	"go.uber.org/zap"
	"golang.org/x/crypto/bcrypt"
	"gorm.io/gorm"
	"net/http"
	"real-user-service/config"

	"real-user-service/models"
)

type Handler struct {
	DB       *gorm.DB
	Validate *validator.Validate
}

func NewHandler(db *gorm.DB, validate *validator.Validate) *Handler {
	return &Handler{
		DB:       db,
		Validate: validate,
	}
}

// CreateUserHandler обрабатывает создание нового пользователя
// @Summary Регистрация нового пользователя
// @Description Создает нового пользователя в системе
// @Tags user-service
// @Accept json
// @Produce json
// @Param user body models.UserCreateRequest true "Данные пользователя"
// @Success 201 {object} models.UserResponse "Пользователь успешно создан"
// @Failure 400 {object} handlers.ErrorResponse "Пользователь с таким login или email уже существует"
// @Failure 422 {object} handlers.ErrorResponse "Некорректные данные запроса"
// @Failure 500 {object} handlers.ErrorResponse "Внутренняя ошибка сервера"
// @Router /user-service/api/v1/register [post]
func (h *Handler) CreateUserHandler(c *gin.Context) {
	var req models.UserCreateRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		config.Logger.Warn("Некорректные данные запроса при создании пользователя", zap.Error(err))
		c.JSON(http.StatusBadRequest, ErrorResponse{
			Code:    400,
			Message: "Некорректные данные запроса",
		})
		return
	}

	if err := h.Validate.Struct(&req); err != nil {
		config.Logger.Warn("Валидация данных при создании пользователя не прошла", zap.Error(err))
		c.JSON(http.StatusUnprocessableEntity, ErrorResponse{
			Code:    422,
			Message: err.Error(),
		})
		return
	}

	var existing models.User
	if err := h.DB.Where("login = ? OR email = ?", req.Login, req.Email).First(&existing).Error; err == nil {
		config.Logger.Warn("Пользователь с таким login или email уже существует", zap.String("login", req.Login), zap.String("email", req.Email))
		c.JSON(http.StatusBadRequest, ErrorResponse{
			Code:    400,
			Message: "Пользователь с таким login или email уже существует",
		})
		return
	} else if err != gorm.ErrRecordNotFound {
		config.Logger.Error("Ошибка при проверке существования пользователя", zap.Error(err))
		c.JSON(http.StatusInternalServerError, ErrorResponse{
			Code:    500,
			Message: "Ошибка при проверке существования пользователя",
		})
		return
	}

	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
	if err != nil {
		config.Logger.Error("Ошибка при хешировании пароля", zap.Error(err))
		c.JSON(http.StatusInternalServerError, ErrorResponse{
			Code:    500,
			Message: "Ошибка при хешировании пароля",
		})
		return
	}

	user := models.User{
		Login:        req.Login,
		Email:        req.Email,
		PasswordHash: string(hashedPassword),
		Role:         req.Role,
		Status:       "active", // Установка статуса по умолчанию
	}

	if err := h.DB.Create(&user).Error; err != nil {
		config.Logger.Error("Ошибка при создании пользователя", zap.Error(err))
		c.JSON(http.StatusInternalServerError, ErrorResponse{
			Code:    500,
			Message: "Ошибка при создании пользователя",
		})
		return
	}

	response := models.UserResponse{
		ID:        user.ID,
		Login:     user.Login,
		Email:     user.Email,
		Role:      user.Role,
		Status:    user.Status,
		CreatedAt: user.CreatedAt.Format(time.RFC3339),
		UpdatedAt: user.UpdatedAt.Format(time.RFC3339),
	}

	c.JSON(http.StatusCreated, response)

	config.Logger.Info("Пользователь создан", zap.Int("id", user.ID), zap.String("login", user.Login))
}

// GetAllUsersHandler обрабатывает получение списка всех пользователей
// @Summary Получение списка всех пользователей
// @Description Возвращает список всех пользователей в системе
// @Tags user-service
// @Security BearerAuth
// @Accept json
// @Produce json
// @Success 200 {array} models.UserResponse "Список пользователей"
// @Failure 500 {object} handlers.ErrorResponse "Внутренняя ошибка сервера"
// @Router /user-service/api/v1/users [get]
func (h *Handler) GetAllUsersHandler(c *gin.Context) {
	var users []models.User
	if err := h.DB.Find(&users).Error; err != nil {
		config.Logger.Error("Ошибка при получении списка пользователей", zap.Error(err))
		c.JSON(http.StatusInternalServerError, ErrorResponse{
			Code:    500,
			Message: "Ошибка при получении списка пользователей",
		})
		return
	}

	var response []models.UserResponse
	for _, user := range users {
		response = append(response, models.UserResponse{
			ID:        user.ID,
			Login:     user.Login,
			Email:     user.Email,
			Role:      user.Role,
			Status:    user.Status,
			CreatedAt: user.CreatedAt.Format(time.RFC3339),
			UpdatedAt: user.UpdatedAt.Format(time.RFC3339),
		})
	}

	c.JSON(http.StatusOK, response)

	config.Logger.Info("Получен список всех пользователей")
}

// GetUserByIDHandler обрабатывает получение пользователя по ID
// @Summary Получение пользователя по ID
// @Description Возвращает пользователя по его ID
// @Tags user-service
// @Security BearerAuth
// @Accept json
// @Produce json
// @Param id path int true "ID пользователя (integer)"
// @Success 200 {object} models.UserResponse "Информация о пользователе"
// @Failure 404 {object} handlers.ErrorResponse "Пользователь не найден"
// @Failure 500 {object} handlers.ErrorResponse "Внутренняя ошибка сервера"
// @Router /user-service/api/v1/users/{id} [get]
func (h *Handler) GetUserByIDHandler(c *gin.Context) {
	idStr := c.Param("id")
	userID, err := strconv.Atoi(idStr)
	if err != nil {
		config.Logger.Warn("Некорректный формат ID пользователя", zap.String("id", idStr))
		c.JSON(http.StatusBadRequest, ErrorResponse{
			Code:    400,
			Message: "Некорректный формат ID пользователя",
		})
		return
	}

	var user models.User
	if err := h.DB.First(&user, "id = ?", userID).Error; err != nil {
		if err == gorm.ErrRecordNotFound {
			config.Logger.Warn("Пользователь не найден при запросе по ID", zap.Int("id", userID))
			c.JSON(http.StatusNotFound, ErrorResponse{
				Code:    404,
				Message: "Пользователь не найден",
			})
			return
		}
		config.Logger.Error("Ошибка при получении пользователя по ID", zap.Error(err))
		c.JSON(http.StatusInternalServerError, ErrorResponse{
			Code:    500,
			Message: "Ошибка при получении пользователя",
		})
		return
	}

	response := models.UserResponse{
		ID:        user.ID,
		Login:     user.Login,
		Email:     user.Email,
		Role:      user.Role,
		Status:    user.Status,
		CreatedAt: user.CreatedAt.Format(time.RFC3339),
		UpdatedAt: user.UpdatedAt.Format(time.RFC3339),
	}

	c.JSON(http.StatusOK, response)

	config.Logger.Info("Получен пользователь по ID", zap.Int("id", user.ID), zap.String("login", user.Login))
}

// UpdateUserHandler обрабатывает обновление данных пользователя
// @Summary Обновление пользователя
// @Description Обновляет данные пользователя по его ID
// @Tags user-service
// @Security BearerAuth
// @Accept json
// @Produce json
// @Param id path int true "ID пользователя (integer)"
// @Param user body models.UserUpdateRequest true "Данные для обновления"
// @Success 200 {object} models.UserResponse "Пользователь успешно обновлен"
// @Failure 400 {object} handlers.ErrorResponse "Некорректные данные запроса"
// @Failure 403 {object} handlers.ErrorResponse "Недостаточно прав для обновления пользователя"
// @Failure 404 {object} handlers.ErrorResponse "Пользователь не найден"
// @Failure 422 {object} handlers.ErrorResponse "Некорректные данные запроса"
// @Failure 500 {object} handlers.ErrorResponse "Внутренняя ошибка сервера"
// @Router /user-service/api/v1/users/{id} [put]
func (h *Handler) UpdateUserHandler(c *gin.Context) {
	idStr := c.Param("id")
	userID, err := strconv.Atoi(idStr)
	if err != nil {
		config.Logger.Warn("Некорректный формат ID пользователя", zap.String("id", idStr))
		c.JSON(http.StatusBadRequest, ErrorResponse{
			Code:    400,
			Message: "Некорректный формат ID пользователя",
		})
		return
	}

	var req models.UserUpdateRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		config.Logger.Warn("Некорректные данные запроса при обновлении пользователя", zap.Error(err))
		c.JSON(http.StatusBadRequest, ErrorResponse{
			Code:    400,
			Message: "Некорректные данные запроса",
		})
		return
	}

	if err := h.Validate.Struct(&req); err != nil {
		config.Logger.Warn("Валидация данных при обновлении пользователя не прошла", zap.Error(err))
		c.JSON(http.StatusUnprocessableEntity, ErrorResponse{
			Code:    422,
			Message: err.Error(),
		})
		return
	}

	var user models.User
	if err := h.DB.First(&user, "id = ?", userID).Error; err != nil {
		if err == gorm.ErrRecordNotFound {
			config.Logger.Warn("Пользователь не найден при обновлении", zap.Int("id", userID))
			c.JSON(http.StatusNotFound, ErrorResponse{
				Code:    404,
				Message: "Пользователь не найден",
			})
			return
		}
		config.Logger.Error("Ошибка при получении пользователя для обновления", zap.Error(err))
		c.JSON(http.StatusInternalServerError, ErrorResponse{
			Code:    500,
			Message: "Ошибка при получении пользователя",
		})
		return
	}

	// Проверка прав пользователя
	//claims, exists := c.Get("claims")
	//if !exists {
	//	config.Logger.Error("Claims отсутствуют в контексте при обновлении пользователя")
	//	c.JSON(http.StatusInternalServerError, ErrorResponse{
	//		Code:    500,
	//		Message: "Claims отсутствуют в контексте",
	//	})
	//	return
	//}

	//jwtClaims, ok := claims.(jwt.MapClaims)
	//if !ok {
	//	config.Logger.Error("Некорректные claims при обновлении пользователя")
	//	c.JSON(http.StatusInternalServerError, ErrorResponse{
	//		Code:    500,
	//		Message: "Некорректные claims",
	//	})
	//	return
	//}
	//
	//userIDFloat, ok := jwtClaims["user_id"].(float64)
	//if !ok {
	//	config.Logger.Error("Некорректный user_id в claims при обновлении пользователя")
	//	c.JSON(http.StatusInternalServerError, ErrorResponse{
	//		Code:    500,
	//		Message: "Некорректный user_id в claims",
	//	})
	//	return
	//}
	//userIDClaim := int(userIDFloat)
	//
	//userRole, ok := jwtClaims["role"].(string)
	//if !ok {
	//	config.Logger.Error("Некорректная роль в claims при обновлении пользователя")
	//	c.JSON(http.StatusInternalServerError, ErrorResponse{
	//		Code:    500,
	//		Message: "Некорректная роль в claims",
	//	})
	//	return
	//}
	//
	//if userIDClaim != user.ID && userRole != "admin" {
	//	config.Logger.Warn("Недостаточно прав для обновления данных пользователя", zap.Int("user_id", userIDClaim), zap.Int("target_user_id", user.ID))
	//	c.JSON(http.StatusForbidden, ErrorResponse{
	//		Code:    403,
	//		Message: "Недостаточно прав для обновления данных этого пользователя",
	//	})
	//	return
	//}

	if user.Login != req.Login || user.Email != req.Email {
		var existing models.User
		if err := h.DB.Where("(login = ? OR email = ?) AND id <> ?", req.Login, req.Email, user.ID).First(&existing).Error; err == nil {
			config.Logger.Warn("Пользователь с таким login или email уже существует при обновлении", zap.String("login", req.Login), zap.String("email", req.Email))
			c.JSON(http.StatusBadRequest, ErrorResponse{
				Code:    400,
				Message: "Пользователь с таким login или email уже существует",
			})
			return
		} else if err != gorm.ErrRecordNotFound {
			config.Logger.Error("Ошибка при проверке существования пользователя при обновлении", zap.Error(err))
			c.JSON(http.StatusInternalServerError, ErrorResponse{
				Code:    500,
				Message: "Ошибка при проверке существования пользователя",
			})
			return
		}
	}

	user.Login = req.Login
	user.Email = req.Email
	user.Role = req.Role
	user.Status = req.Status

	if req.Password != "" {
		hashedPassword, err := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
		if err != nil {
			config.Logger.Error("Ошибка при хешировании пароля при обновлении пользователя", zap.Error(err))
			c.JSON(http.StatusInternalServerError, ErrorResponse{
				Code:    500,
				Message: "Ошибка при хешировании пароля",
			})
			return
		}
		user.PasswordHash = string(hashedPassword)
	}

	if err := h.DB.Save(&user).Error; err != nil {
		config.Logger.Error("Ошибка при обновлении пользователя", zap.Error(err))
		c.JSON(http.StatusInternalServerError, ErrorResponse{
			Code:    500,
			Message: "Ошибка при обновлении пользователя",
		})
		return
	}

	response := models.UserResponse{
		ID:        user.ID,
		Login:     user.Login,
		Email:     user.Email,
		Role:      user.Role,
		Status:    user.Status,
		CreatedAt: user.CreatedAt.Format(time.RFC3339),
		UpdatedAt: user.UpdatedAt.Format(time.RFC3339),
	}

	c.JSON(http.StatusOK, response)

	config.Logger.Info("Пользователь обновлён", zap.Int("id", user.ID), zap.String("login", user.Login))
}

// DeleteUserHandler обрабатывает удаление пользователя
// @Summary Удаление пользователя
// @Description Удаляет пользователя по его ID
// @Tags user-service
// @Security BearerAuth
// @Accept json
// @Produce json
// @Param id path int true "ID пользователя (integer)"
// @Success 200 {object} handlers.SuccessResponse "Пользователь успешно удалён"
// @Failure 404 {object} handlers.ErrorResponse "Пользователь не найден"
// @Failure 500 {object} handlers.ErrorResponse "Внутренняя ошибка сервера"
// @Router /user-service/api/v1/users/{id} [delete]
func (h *Handler) DeleteUserHandler(c *gin.Context) {
	idStr := c.Param("id")
	userID, err := strconv.Atoi(idStr)
	if err != nil {
		config.Logger.Warn("Некорректный формат ID пользователя", zap.String("id", idStr))
		c.JSON(http.StatusBadRequest, ErrorResponse{
			Code:    400,
			Message: "Некорректный формат ID пользователя",
		})
		return
	}

	var user models.User
	if err := h.DB.First(&user, "id = ?", userID).Error; err != nil {
		if err == gorm.ErrRecordNotFound {
			config.Logger.Warn("Пользователь не найден при удалении", zap.Int("id", userID))
			c.JSON(http.StatusNotFound, ErrorResponse{
				Code:    404,
				Message: "Пользователь не найден",
			})
			return
		}
		config.Logger.Error("Ошибка при получении пользователя для удаления", zap.Error(err))
		c.JSON(http.StatusInternalServerError, ErrorResponse{
			Code:    500,
			Message: "Ошибка при получении пользователя",
		})
		return
	}

	if err := h.DB.Delete(&user).Error; err != nil {
		config.Logger.Error("Ошибка при удалении пользователя", zap.Error(err))
		c.JSON(http.StatusInternalServerError, ErrorResponse{
			Code:    500,
			Message: "Ошибка при удалении пользователя",
		})
		return
	}

	config.Logger.Info("Пользователь успешно удалён", zap.Int("id", user.ID))
	c.JSON(http.StatusOK, SuccessResponse{
		Msg: "Пользователь успешно удален",
	})
}
