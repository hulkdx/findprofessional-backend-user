To run locally: 
```sh
./gradlew bootRun
```

To run with docker:
```sh
./gradlew bootBuildImage
docker run --rm -p 8080:8080 IMAGE_NAME
```
