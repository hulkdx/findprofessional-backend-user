.PHONY: dev
dev:
	cd dev-tools && \
	skaffold dev --port-forward

.PHONY: clear-minikube-psql-cache
clear-minikube-psql-cache:
	minikube ssh -- "sudo rm -rf /data/psql_cache"

.PHONY: clean
clean:
	cd user-service && ./gradlew clean

.PHONY: test
test:
	cd user-service && ./gradlew test integrationTest

