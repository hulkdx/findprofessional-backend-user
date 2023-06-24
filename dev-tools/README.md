# Local development
Tools that is required for local development, running everything (e.g. db, service, ect) on the local machine.

Currently it is used:
- [skaffold](https://skaffold.dev)

## Ingress development
Using minikube
1. enable ingress controller
```sh
minikube addons enable ingress
```
1. apply ingress (remember to rename the service)
```
kubectl apply -f https://github.com/hulkdx/findprofessional-infra/blob/main/k8s/deploy/prod/ingress.yml
```
1. minikube tunnel
