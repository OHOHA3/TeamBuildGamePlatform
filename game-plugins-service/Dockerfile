# Используем базовый образ с JDK 17
FROM eclipse-temurin:17 AS base

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем Maven Wrapper и настройки
COPY .mvn .mvn/
COPY mvnw .

# Копируем файл pom.xml и весь исходный код проекта
COPY pom.xml .
COPY src ./src

# Делаем mvnw исполняемым
RUN chmod +x mvnw

# Команда для запуска приложения через Maven
ENTRYPOINT ["./mvnw", "spring-boot:run"]