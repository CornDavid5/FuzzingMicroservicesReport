# Overview
Repo: https://github.com/CornDavid5/ftgo-application

Commit: d9a597b5d1846f2d0625f431ec2cc9c6c7e7e226

This project consists following services:
- `ftgo-consumer-service` - the Consumer Service
- `ftgo-restaurant-service` - the Restaurant Service
- `ftgo-order-service` - the Order Service
- `ftgo-kitchen-service` - the Kitchen Service
- `ftgo-accounting-service` - the Accounting Service
- `ftgo-order-history-service` - a Order History Service, which is a CQRS view
- `ftgo-api-gateway` - the API gateway

# How to Deploy
Prerequisite:
- Java 8+
- docker-compose

This project is managed by Gradle and the authors provide the functionality to deploy all services:
- build Spring Cloud Contract, run `./gradlew buildContracts`
- build other services, run `./gradlew assemble`
- start services in containers, run `./gradlew :composeUp`
- shut down all containers, run `./gradlew :composeDown`

This project doesn't have a frontend UI, but you can access services directly through REST API call, and you can use Swagger UI to invoke them:
- consumer service - http://localhost:8081/swagger-ui/index.html
- order service - http://localhost:8082/swagger-ui/index.html
- kitchen service - http://localhost:8083/swagger-ui/index.html
- restaurant service - http://localhost:8084/swagger-ui/index.html
- account service - http://localhost:8084/swagger-ui/index.html
- order history service - http://localhost:8086/swagger-ui/index.html

# How to Run Test
Prerequisite:
- Java 8+
- docker-compose

Steps:
- clone my instrumented repo, run `git clone git@github.com:CornDavid5/ftgo-application.git`
- generate coverage report, run `./gradlew jacocoTestReport`
- you can find coverage report for core components in their respective folder and under `build/jacoco` folder

Or, you can find the generated coverage in [here](./coverage/)
