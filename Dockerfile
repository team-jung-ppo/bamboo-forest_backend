FROM eclipse-temurin:17 as builder

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x ./gradlew && ./gradlew bootJar

FROM eclipse-temurin:17-jre as runtime

RUN mkdir -p /logs

COPY --from=builder /build/libs/*.jar app.jar

ENV TZ=Asia/Seoul
ENV PROFILE ${PROFILE}

EXPOSE 8080
EXPOSE 1010

ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}", "-jar", "/app.jar"]
