FROM gradle as builder
WORKDIR /app
COPY . /app
RUN gradle stage

FROM openjdk:8-alpine as runtime
LABEL maintainer=rico
WORKDIR /app

ENV WORKSPACE_ID ''
ENV API_TOKEN ''
ENV TOGGL_SECRET ''
ENV APPLICATION_SECURITY_PASSWORD ''
ENV APPLICATION_SECURITY_USERNAME ''
ENV JAVA_OPTS '-Xmx100m'
ENV PORT 8080

COPY --from=builder /app/server/build/libs/*.jar /app/reporting.jar
CMD ["java", "$JAVA_OPTS", "-Dspring.profiles.active=prod", "-jar", "/app/reporting.jar", "--server.port=$PORT"]
EXPOSE 8080
