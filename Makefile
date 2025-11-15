.PHONY: dev
dev:
	cd dev-tools && \
	skaffold dev --port-forward

.PHONY: clear-minikube-psql-cache
clear-minikube-psql-cache:
	PV=$$(kubectl get pvc data-postgresdb-0 -o jsonpath='{.spec.volumeName}'); \
	HOST=$$(kubectl get pv $$PV -o jsonpath='{.spec.hostPath.path}'); \
	minikube ssh -- "sudo rm -rf $$HOST"

.PHONY: clean
clean:
	cd user-service && ./gradlew clean

.PHONY: test
test:
	cd user-service && ./gradlew test integrationTest

