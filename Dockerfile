# Многостадийная сборка Docker образа

# === СТАДИЯ 1: Сборка приложения ===
# Используем образ Maven с Java 17 для компиляции
FROM maven:3.8.6-eclipse-temurin-17 AS builder

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем только pom.xml сначала (для кэширования зависимостей)
COPY pom.xml .

# Скачиваем все зависимости (этот слой закэшируется, если pom.xml не менялся)
RUN mvn dependency:go-offline

# Копируем исходный код
COPY src ./src

# Собираем приложение (создаем JAR файл)
RUN mvn clean package -DskipTests

# === СТАДИЯ 2: Создание легковесного образа для запуска ===
# Используем легковесный JRE образ
FROM eclipse-temurin:17-jre-jammy

# Создаем рабочую директорию
WORKDIR /app

# Создаем непривилегированного пользователя для безопасности
RUN addgroup --system --gid 1001 appuser && \
    adduser --system --uid 1001 --gid 1001 appuser

# Копируем JAR файл из стадии сборки
COPY --from=builder /app/target/*.jar app.jar

# Даем права на выполнение (хотя JAR не требуется, но для порядка)
RUN chown appuser:appuser app.jar

# Переключаемся на непривилегированного пользователя
USER appuser

# Добавляем проверку здоровья контейнера
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Указываем порт, который будет использовать приложение
EXPOSE 8080

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]