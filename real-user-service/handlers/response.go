package handlers

type ErrorResponse struct {
	Code    int    `json:"code"`
	Message string `json:"message"`
}

type SuccessResponse struct {
	Data interface{} `json:"data,omitempty"`
	Msg  string      `json:"message,omitempty"`
}

//type CreateUserResponse struct {
//	ID        uuid.UUID `json:"id"`
//	Username  string    `json:"username"`
//	Email     string    `json:"email"`
//	Team      string    `json:"team"`
//	Role      string    `json:"role"`
//	CreatedAt string    `json:"created_at"`
//	UpdatedAt string    `json:"updated_at"`
//}

//type LoginResponse struct {
//	Token string `json:"token"`
//}

//type LoginRequest struct {
//	Email    string `json:"email" binding:"required,email"`
//	Password string `json:"password" binding:"required"`
//}
