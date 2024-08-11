FROM eclipse-temurin:17 as builder

COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle

RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon

COPY src src

RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:17-jre as runtime

RUN mkdir -p /logs

COPY --from=builder /build/libs/*.jar /app.jar

ENV TZ=Asia/Seoul
ENV PROFILE=${PROFILE}

EXPOSE 8080
EXPOSE 1010

CMD ["java", "-Dspring.profiles.active=${PROFILE}", "-jar", "/app.jar"]
