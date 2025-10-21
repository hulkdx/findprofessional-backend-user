![Build](https://img.shields.io/github/actions/workflow/status/hulkdx/findprofessional-backend-user/push.yaml?branch=main)
[![Docker Status](https://badgen.net/docker/size/hulkdx/ff-user/v1/amd64?icon=docker&label=docker&url)](https://hub.docker.com/repository/docker/hulkdx/ff-user)
[![kotlin](https://img.shields.io/badge/kotlin-1.8.22-blue.svg?logo=kotlin)](http://kotlinlang.org)

# User microservice

## Development
Requirements
- [Skaffold](https://skaffold.dev/docs/install/)
- [Minikube](https://minikube.sigs.k8s.io/docs/start/) (or any other localhost kubernetes)
- [Kubectl](https://kubernetes.io/docs/tasks/tools/)
- [Docker](https://docs.docker.com/get-docker/)
- Java 19

To start development, run `make dev`
## Deployment
### Production
- Using [GraalVM Native Image](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#native-image)
  - Alternative can use [Dockerfile alpine](https://github.com/hulkdx/findprofessional-backend-user/blob/4fff3b93eef556a382eb807c6d9f49d40eaa8f64/deploy/Dockerfile)

## Troubleshooting

### Could not find a valid Docker environment
If running integrationTest on MacOS, this would fix it:
```sh
sudo ln -s $HOME/.docker/run/docker.sock /var/run/docker.sock
```

## TODO
- Currently all SQL migrations happen in this project for all microservices with `liquibase`, research how to do it properly.
- Host swagger docs to some url
