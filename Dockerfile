FROM openjdk:8-alpine
LABEL maintainer=rico
WORKDIR /app
COPY server/build/libs libs/
COPY server/build/resources resources/
COPY server/build/classes classes/
ENTRYPOINT ["java", "-Dspring.profiles.active=production", "-Xmx2048m", "-cp", "/app/resources:/app/classes:/app/libs/*", "io.github.titaniumcoder.toggl.reporting.TogglReportingApplication"]
EXPOSE 8080
