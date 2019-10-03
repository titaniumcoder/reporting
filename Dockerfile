FROM gradle as builder
WORKDIR /app
COPY . /app
RUN gradle assembleServerAndClient

FROM adoptopenjdk/openjdk11-openj9:jdk-11.0.1.13-alpine-slim as runtime
LABEL maintainer=rico
WORKDIR /app

ENV WORKSPACE_ID ''
ENV API_TOKEN ''
ENV APPLICATION_SECURITY_PASSWORD ''
ENV APPLICATION_SECURITY_USERNAME ''
ENV JAVA_OPTS '-Xmx100m'
ENV PORT 5000

COPY --from=builder /app/server-new/build/libs/*.jar /app/reporting.jar
CMD java -Dcom.sun.management.jmxremote -noverify -Dserver.port=$PORT -Dapplication.security.username="$APPLICATION_SECURITY_USERNAME" -Dapplication.security.password="$APPLICATION_SECURITY_PASSWORD" -Dapplication.apiToken="$API_TOKEN" -Dapplication.workspaceId="$WORKSPACE_ID" ${JAVA_OPTS} -jar /app/reporting.jar
EXPOSE 5000
