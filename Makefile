.PHONY: dev
dev:
	cd dev-tools && \
	skaffold dev --port-forward

.PHONY: clear-minikube-psql-cache
clear-minikube-psql-cache:
	eval $$(minikube docker-env); \
	docker volume rm --force psql_cache

.PHONY: clean
clean:
	cd user-service && ./gradlew clean

test:
	cd user-service && ./gradlew test integrationTest

