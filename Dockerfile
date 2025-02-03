# 1st Docker build stage: build the project with Gradle
FROM gradle:8.12-jdk23-alpine AS builder
WORKDIR /project
COPY . /project/
RUN gradle assemble --no-daemon

# 2nd Docker build stage: copy builder output and configure entry point
FROM eclipse-temurin:23.0.1_11-jdk-alpine
ENV APP_DIR=/application
ENV APP_FILE=be-stack-game-vertx-1.0.0-SNAPSHOT.jar

WORKDIR $APP_DIR
COPY --from=builder /project/build/libs/*-fat.jar $APP_DIR/$APP_FILE

ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $APP_FILE"]
