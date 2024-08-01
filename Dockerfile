FROM eclipse-temurin:17 as builder

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x ./gradlew && ./gradlew bootJar

FROM eclipse-temurin:17-jre as runtime

RUN groupadd --system --gid 1001 spring && \
    useradd --system --uid 1001 --gid spring --no-create-home --shell /bin/false spring

RUN mkdir -p /logs && \
    chown -R spring:spring /logs

COPY --from=builder /build/libs/*.jar app.jar

USER spring:spring

ENV PROFILE ${PROFILE}

EXPOSE 8080
EXPOSE 1010

ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}", "-jar", "/app.jar"]
