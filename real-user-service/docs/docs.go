// Package docs Code generated by swaggo/swag. DO NOT EDIT
package docs

import "github.com/swaggo/swag"

const docTemplate = `{
    "schemes": {{ marshal .Schemes }},
    "swagger": "2.0",
    "info": {
        "description": "{{escape .Description}}",
        "title": "{{.Title}}",
        "termsOfService": "http://swagger.io/terms/",
        "contact": {
            "name": "API Support",
            "url": "http://www.swagger.io/support",
            "email": "support@swagger.io"
        },
        "license": {
            "name": "MIT",
            "url": "https://opensource.org/licenses/MIT"
        },
        "version": "{{.Version}}"
    },
    "host": "{{.Host}}",
    "basePath": "{{.BasePath}}",
    "paths": {
        "/user-service/api/v1/register": {
            "post": {
                "description": "Создает нового пользователя в системе",
                "consumes": [
                    "application/json"
                ],
                "produces": [
                    "application/json"
                ],
                "tags": [
                    "user-service"
                ],
                "summary": "Регистрация нового пользователя",
                "parameters": [
                    {
                        "description": "Данные пользователя",
                        "name": "user",
                        "in": "body",
                        "required": true,
                        "schema": {
                            "$ref": "#/definitions/models.UserCreateRequest"
                        }
                    }
                ],
                "responses": {
                    "201": {
                        "description": "Пользователь успешно создан",
                        "schema": {
                            "$ref": "#/definitions/models.UserResponse"
                        }
                    },
                    "400": {
                        "description": "Пользователь с таким email или username уже существует",
                        "schema": {
                            "$ref": "#/definitions/handlers.ErrorResponse"
                        }
                    },
                    "422": {
                        "description": "Некорректные данные запроса",
                        "schema": {
                            "$ref": "#/definitions/handlers.ErrorResponse"
                        }
                    },
                    "500": {
                        "description": "Внутренняя ошибка сервера",
                        "schema": {
                            "$ref": "#/definitions/handlers.ErrorResponse"
                        }
                    }
                }
            }
        },
        "/user-service/api/v1/users": {
            "get": {
                "security": [
                    {
                        "BearerAuth": []
                    }
                ],
                "description": "Возвращает список всех пользователей в системе",
                "consumes": [
                    "application/json"
                ],
                "produces": [
                    "application/json"
                ],
                "tags": [
                    "user-service"
                ],
                "summary": "Получение списка всех пользователей",
                "responses": {
                    "200": {
                        "description": "Список пользователей",
                        "schema": {
                            "type": "array",
                            "items": {
                                "$ref": "#/definitions/models.UserResponse"
                            }
                        }
                    },
                    "500": {
                        "description": "Внутренняя ошибка сервера",
                        "schema": {
                            "$ref": "#/definitions/handlers.ErrorResponse"
                        }
                    }
                }
            }
        },
        "/user-service/api/v1/users/{id}": {
            "get": {
                "security": [
                    {
                        "BearerAuth": []
                    }
                ],
                "description": "Возвращает пользователя по его ID",
                "consumes": [
                    "application/json"
                ],
                "produces": [
                    "application/json"
                ],
                "tags": [
                    "user-service"
                ],
                "summary": "Получение пользователя по ID",
                "parameters": [
                    {
                        "type": "string",
                        "description": "ID пользователя (UUID)",
                        "name": "id",
                        "in": "path",
                        "required": true
                    }
                ],
                "responses": {
                    "200": {
                        "description": "Информация о пользователе",
                        "schema": {
                            "$ref": "#/definitions/models.UserResponse"
                        }
                    },
                    "404": {
                        "description": "Пользователь не найден",
                        "schema": {
                            "$ref": "#/definitions/handlers.ErrorResponse"
                        }
                    },
                    "500": {
                        "description": "Внутренняя ошибка сервера",
                        "schema": {
                            "$ref": "#/definitions/handlers.ErrorResponse"
                        }
                    }
                }
            },
            "put": {
                "security": [
                    {
                        "BearerAuth": []
                    }
                ],
                "description": "Обновляет данные пользователя по его ID",
                "consumes": [
                    "application/json"
                ],
                "produces": [
                    "application/json"
                ],
                "tags": [
                    "user-service"
                ],
                "summary": "Обновление пользователя",
                "parameters": [
                    {
                        "type": "string",
                        "description": "ID пользователя (UUID)",
                        "name": "id",
                        "in": "path",
                        "required": true
                    },
                    {
                        "description": "Данные для обновления",
                        "name": "user",
                        "in": "body",
                        "required": true,
                        "schema": {
                            "$ref": "#/definitions/models.UserUpdateRequest"
                        }
                    }
                ],
                "responses": {
                    "200": {
                        "description": "Пользователь успешно обновлен",
                        "schema": {
                            "$ref": "#/definitions/models.UserResponse"
                        }
                    },
                    "400": {
                        "description": "Некорректные данные запроса",
                        "schema": {
                            "$ref": "#/definitions/handlers.ErrorResponse"
                        }
                    },
                    "403": {
                        "description": "Недостаточно прав для обновления пользователя",
                        "schema": {
                            "$ref": "#/definitions/handlers.ErrorResponse"
                        }
                    },
                    "404": {
                        "description": "Пользователь не найден",
                        "schema": {
                            "$ref": "#/definitions/handlers.ErrorResponse"
                        }
                    },
                    "422": {
                        "description": "Некорректные данные запроса",
                        "schema": {
                            "$ref": "#/definitions/handlers.ErrorResponse"
                        }
                    },
                    "500": {
                        "description": "Внутренняя ошибка сервера",
                        "schema": {
                            "$ref": "#/definitions/handlers.ErrorResponse"
                        }
                    }
                }
            },
            "delete": {
                "security": [
                    {
                        "BearerAuth": []
                    }
                ],
                "description": "Удаляет пользователя по его ID",
                "consumes": [
                    "application/json"
                ],
                "produces": [
                    "application/json"
                ],
                "tags": [
                    "user-service"
                ],
                "summary": "Удаление пользователя",
                "parameters": [
                    {
                        "type": "string",
                        "description": "ID пользователя (UUID)",
                        "name": "id",
                        "in": "path",
                        "required": true
                    }
                ],
                "responses": {
                    "200": {
                        "description": "Пользователь успешно удалён",
                        "schema": {
                            "$ref": "#/definitions/handlers.SuccessResponse"
                        }
                    },
                    "404": {
                        "description": "Пользователь не найден",
                        "schema": {
                            "$ref": "#/definitions/handlers.ErrorResponse"
                        }
                    },
                    "500": {
                        "description": "Внутренняя ошибка сервера",
                        "schema": {
                            "$ref": "#/definitions/handlers.ErrorResponse"
                        }
                    }
                }
            }
        }
    },
    "definitions": {
        "handlers.ErrorResponse": {
            "type": "object",
            "properties": {
                "code": {
                    "type": "integer"
                },
                "message": {
                    "type": "string"
                }
            }
        },
        "handlers.SuccessResponse": {
            "type": "object",
            "properties": {
                "data": {},
                "message": {
                    "type": "string"
                }
            }
        },
        "models.UserCreateRequest": {
            "type": "object",
            "required": [
                "email",
                "password",
                "role",
                "username"
            ],
            "properties": {
                "email": {
                    "type": "string",
                    "maxLength": 256
                },
                "password": {
                    "type": "string",
                    "maxLength": 64,
                    "minLength": 8
                },
                "role": {
                    "type": "string",
                    "enum": [
                        "admin",
                        "user",
                        "hr"
                    ]
                },
                "team": {
                    "type": "string",
                    "maxLength": 100
                },
                "username": {
                    "type": "string",
                    "maxLength": 25,
                    "minLength": 3
                }
            }
        },
        "models.UserResponse": {
            "type": "object",
            "properties": {
                "created_at": {
                    "type": "string"
                },
                "email": {
                    "type": "string"
                },
                "id": {
                    "type": "string"
                },
                "role": {
                    "type": "string"
                },
                "team": {
                    "type": "string"
                },
                "updated_at": {
                    "type": "string"
                },
                "username": {
                    "type": "string"
                }
            }
        },
        "models.UserUpdateRequest": {
            "type": "object",
            "required": [
                "email",
                "role",
                "username"
            ],
            "properties": {
                "email": {
                    "type": "string",
                    "maxLength": 256
                },
                "password": {
                    "type": "string",
                    "maxLength": 64,
                    "minLength": 8
                },
                "role": {
                    "type": "string",
                    "enum": [
                        "admin",
                        "user",
                        "hr"
                    ]
                },
                "team": {
                    "type": "string",
                    "maxLength": 100
                },
                "username": {
                    "type": "string",
                    "maxLength": 25,
                    "minLength": 3
                }
            }
        }
    },
    "securityDefinitions": {
        "BearerAuth": {
            "type": "apiKey",
            "name": "Authorization",
            "in": "header"
        }
    }
}`

// SwaggerInfo holds exported Swagger Info so clients can modify it
var SwaggerInfo = &swag.Spec{
	Version:          "1.0",
	Host:             "localhost:8080",
	BasePath:         "",
	Schemes:          []string{},
	Title:            "User Service API",
	Description:      "Сервис Пользователей управляет информацией о пользователях системы.",
	InfoInstanceName: "swagger",
	SwaggerTemplate:  docTemplate,
	LeftDelim:        "{{",
	RightDelim:       "}}",
}

func init() {
	swag.Register(SwaggerInfo.InstanceName(), SwaggerInfo)
}
