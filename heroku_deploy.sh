#!/bin/bash
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker push registry.heroku.com/titaniumcoder/kotlin-reporting/web
