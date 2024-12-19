FROM amazoncorretto:21.0.4-alpine3.18
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