FROM gradle:8.10.2-jdk21 AS builder

WORKDIR /app

COPY build.gradle settings.gradle* gradle.properties* ./
RUN gradle dependencies --no-daemon || true

COPY src ./src
RUN gradle clean bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

ENV TZ=America/Sao_Paulo

RUN apt-get update \
    && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends tzdata \
    && ln -snf /usr/share/zoneinfo/$TZ /etc/localtime \
    && echo $TZ > /etc/timezone \
    && useradd --system --create-home --shell /usr/sbin/nologin appuser \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/build/libs/*.jar app.jar

RUN chown -R appuser:appuser /app
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-Duser.timezone=America/Sao_Paulo", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]