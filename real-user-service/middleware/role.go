package middleware

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/golang-jwt/jwt/v4"
	"go.uber.org/zap"
)

func RoleMiddleware(requiredRoles ...string) gin.HandlerFunc {
	return func(c *gin.Context) {
		claims, exists := c.Get("claims")
		if !exists {
			zap.L().Error("Claims отсутствуют в контексте при проверке роли")
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Claims отсутствуют в контексте"})
			c.Abort()
			return
		}

		jwtClaims, ok := claims.(jwt.MapClaims)
		if !ok {
			zap.L().Error("Некорректные claims при проверке роли")
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Некорректные claims"})
			c.Abort()
			return
		}

		userRole, exists := jwtClaims["role"].(string)
		if !exists {
			zap.L().Error("Роль пользователя не определена в claims")
			c.JSON(http.StatusForbidden, gin.H{"error": "Роль пользователя не определена"})
			c.Abort()
			return
		}

		for _, role := range requiredRoles {
			if userRole == role {
				c.Next()
				return
			}
		}

		zap.L().Warn("Недостаточно прав для выполнения действия", zap.String("user_role", userRole), zap.Strings("required_roles", requiredRoles))
		c.JSON(http.StatusForbidden, gin.H{"error": "Недостаточно прав для выполнения этого действия"})
		c.Abort()
	}
}
