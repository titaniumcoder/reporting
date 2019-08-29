#!/bin/bash
echo "In Heroku-Deploy.sh, deploying to heroku with $DOCKER_USERNAME"
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin registry.heroku.com
docker push registry.heroku.com/kotlin-reporting/web
