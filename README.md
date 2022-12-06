![Build](https://img.shields.io/github/workflow/status/hulkdx/findprofessional-backend-user/deploy-to-kubernetes?style=for-the-badge)

[![Docker Status](https://badgen.net/docker/size/hulkdx/ff-user/v1/amd64?icon=docker&label=docker&url)](https://hub.docker.com/repository/docker/hulkdx/ff-user)

# User microservice

## To run

Locally
```sh
./gradlew bootRun
```

Docker 
```sh
./gradlew bootBuildImage
docker run --rm -p 8080:8080 docker.io/library/findprofessional:0.0.1-SNAPSHOT
```

## Deployment
### Production
Spring boot using [GraalVM Native Image](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#native-image), it requires more memory (~400Mi for hello world) but compiling java using ahead-of-time. which reduces the startup loads.

Can also use Dockerfile from [alpine](https://github.com/hulkdx/findprofessional-backend-user/blob/4fff3b93eef556a382eb807c6d9f49d40eaa8f64/deploy/Dockerfile)

