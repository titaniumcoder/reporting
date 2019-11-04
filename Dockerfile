#############
### build ###
#############

# base image
FROM gradle:5.6-jdk11 as builder

# set working directory
WORKDIR /app

# add app
COPY . /app

# skip tests until I have a setup with a running Postgresql image as dependency
RUN gradle assembleServerAndClient

############
### prod ###
############

# base image (test whether it works with jre too)
FROM azul/zulu-openjdk-alpine:11 as runtime
EXPOSE 5000

ENV APP_HOME /app

ENV API_TOKEN=""
ENV JAVA_OPTS=""

RUN mkdir $APP_HOME

WORKDIR $APP_HOME

COPY --from=builder /app/server/build/libs/*.jar reporting.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=prod -jar /app/reporting.jar --server.port=5000" ]
CMD ''
