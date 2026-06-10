# Сборка проекта
FROM dockerhub.timeweb.cloud/eclipse-temurin:21-jdk-noble AS builder
WORKDIR /app
COPY . .

# Собираем дистрибутив со всеми зависимостями (включая Lanterna) в папку build/install/
RUN ./gradlew installDist --no-daemon -x test

FROM dockerhub.timeweb.cloud/eclipse-temurin:21-jre-noble
WORKDIR /app

# Копируем готовый дистрибутив, собранный плагином application
COPY --from=builder /app/build/install/rogue-game ./

# Запускаем игру через официальный исполняемый скрипт, созданный самим Gradle
CMD ["./bin/rogue-game"]
