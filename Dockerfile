####################
### build client ###
####################

# base image
FROM node:12.18.3 as client

# set working directory
WORKDIR /app

# add app
COPY ./client /app

# skip tests until I have a setup with a running Postgresql image as dependency
RUN npm install
RUN npm run build

####################
### build server ###
####################

# base image
FROM gradle:6.5-jdk11 as builder

ENV GRADLE_OPTS '-Dorg.gradle.daemon=false'

# set working directory
WORKDIR /app

# add app
COPY . /app
COPY --from=client /app/build/ /app/src/main/resources/static/

# skip tests until I have a setup with a running Postgresql image as dependency
RUN gradle build

############
### prod ###
############

# base image (test whether it works with jre too)
FROM azul/zulu-openjdk-alpine:11 as runtime
EXPOSE 8080

ENV APP_HOME /app

ENV OAUTH_CLIENT_ID=""
ENV OAUTH_CLIENT_SECRET=""
ENV JAVA_OPTS=""

RUN mkdir $APP_HOME

WORKDIR $APP_HOME

COPY --from=builder /app/server/build/libs/server-all.jar reporting.jar

CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar reporting.jar
