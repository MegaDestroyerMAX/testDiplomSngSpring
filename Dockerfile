# Используем базовый образ OpenJDK
FROM eclipse-temurin:17-jdk-jammy

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем собранный JAR-файл
COPY /testDiplomSngSpring/target/testDiplomSngSpring-0.0.1-SNAPSHOT.jar app.jar

COPY src/main/resources/ /app/resources/

# Открываем порт, который использует приложение
EXPOSE 8080

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]