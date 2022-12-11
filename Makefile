.PHONY: dev
dev:
	cd local-development && \
	skaffold dev --port-forward
