# Используем официальный образ Golang
FROM golang:1.22.2-alpine

# Устанавливаем необходимые пакеты
RUN apk update && apk add --no-cache git

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем go.mod и go.sum
COPY go.mod go.sum ./

# Загружаем зависимости
RUN go mod download

# Копируем исходный код
COPY .. .

# Генерируем Swagger документацию
RUN go install github.com/swaggo/swag/cmd/swag@latest
RUN swag init

# Сборка приложения
RUN go build -o main .

# Определяем переменные окружения
ENV PORT=8080

# Экспонируем порт
EXPOSE 8080

# Запускаем приложение
CMD ["./main"]