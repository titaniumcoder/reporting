FROM gradle as builder
WORKDIR /app
COPY . /app
RUN gradle stage

FROM adoptopenjdk/openjdk11-openj9:jdk-11.0.1.13-alpine-slim as runtie
LABEL maintainer=rico
WORKDIR /app

ENV WORKSPACE_ID ''
ENV API_TOKEN ''
ENV TOGGL_SECRET ''
ENV APPLICATION_SECURITY_PASSWORD ''
ENV APPLICATION_SECURITY_USERNAME ''
ENV JAVA_OPTS '-Xmx100m'
ENV PORT 8080

COPY --from=builder /app/server/build/libs/*-all.jar /app/reporting.jar
CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar /app/reporting.jar
EXPOSE 8080
