# Local development
Tools that is required for local development, running everything (e.g. db, service, ect) on the local machine.

Currently it is used:
- [skaffold](https://skaffold.dev)

## Ingress development
Using minikube
- enable ingress controller
```sh
minikube addons enable ingress
```
- apply ingress (remember to rename the service)
```
kubectl apply -f https://github.com/hulkdx/findprofessional-infra/blob/main/k8s/deploy/prod/ingress.yml
```
- minikube tunnel

### Android
For android develeopment to android localhost to works with machine localhost run this
```sh
adb reverse tcp:80 tcp:8080
```
This means to map machine localhost:80 to android localhost:8080
