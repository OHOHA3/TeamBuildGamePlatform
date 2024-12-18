package middleware

import (
	"fmt"
	"net/http"
	"strings"

	"github.com/gin-gonic/gin"
	"github.com/golang-jwt/jwt/v4"
	"go.uber.org/zap"
	"real-user-service/config"
)

func AuthMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		authHeader := c.GetHeader("Authorization")
		if authHeader == "" {
			zap.L().Warn("Отсутствует заголовок авторизации")
			c.JSON(http.StatusUnauthorized, gin.H{"error": "Отсутствует заголовок авторизации"})
			c.Abort()
			return
		}

		if !strings.HasPrefix(authHeader, "Bearer ") {
			zap.L().Warn("Некорректный формат заголовка авторизации")
			c.JSON(http.StatusUnauthorized, gin.H{"error": "Некорректный формат заголовка авторизации"})
			c.Abort()
			return
		}

		tokenString := strings.TrimPrefix(authHeader, "Bearer ")

		token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
			if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
				return nil, fmt.Errorf("неверный метод подписи токена")
			}
			return []byte(config.JWTSecret), nil
		})

		if err != nil || !token.Valid {
			zap.L().Warn("Невалидный или просроченный токен", zap.Error(err))
			c.JSON(http.StatusUnauthorized, gin.H{"error": "Невалидный или просроченный токен"})
			c.Abort()
			return
		}

		if claims, ok := token.Claims.(jwt.MapClaims); ok && token.Valid {
			c.Set("claims", claims)
		} else {
			zap.L().Warn("Невалидные claims")
			c.JSON(http.StatusUnauthorized, gin.H{"error": "Невалидные claims"})
			c.Abort()
			return
		}

		c.Next()
	}
}
