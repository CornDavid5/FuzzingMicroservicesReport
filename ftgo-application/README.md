# Overview
Repo: https://github.com/CornDavid5/ftgo-application

This project consists following business logic related services:
- Consumer Service
    - [Consumer](https://github.com/CornDavid5/ftgo-application/blob/master/ftgo-consumer-service/src/main/java/net/chrisrichardson/ftgo/consumerservice/domain/Consumer.java) aggregate
- Restaurant Service
    - [Restaurant](https://github.com/CornDavid5/ftgo-application/blob/master/ftgo-restaurant-service/src/main/java/net/chrisrichardson/ftgo/restaurantservice/domain/Restaurant.java) aggregate
- Order Service
    - [Order](https://github.com/CornDavid5/ftgo-application/blob/master/ftgo-order-service/src/main/java/net/chrisrichardson/ftgo/orderservice/domain/Order.java) aggregate
    - [Restaurant](https://github.com/CornDavid5/ftgo-application/blob/master/ftgo-order-service/src/main/java/net/chrisrichardson/ftgo/orderservice/domain/Restaurant.java) aggregate
- Kitchen Service
    - [Restaurant](https://github.com/CornDavid5/ftgo-application/blob/master/ftgo-kitchen-service/src/main/java/net/chrisrichardson/ftgo/kitchenservice/domain/Restaurant.java) aggregate
    - [Ticket](https://github.com/CornDavid5/ftgo-application/blob/master/ftgo-kitchen-service/src/main/java/net/chrisrichardson/ftgo/kitchenservice/domain/Ticket.java) aggregate
- Accounting Service
    - [Account](https://github.com/CornDavid5/ftgo-application/blob/master/ftgo-accounting-service/src/main/java/net/chrisrichardson/ftgo/accountingservice/domain/Account.java) aggregate

`Order Service` implements saga pattern to manage transactions
- [CreateOrderSaga](https://github.com/CornDavid5/ftgo-application/blob/master/ftgo-order-service/src/main/java/net/chrisrichardson/ftgo/orderservice/sagas/createorder/CreateOrderSaga.java)
- [CancelOrderSaga](https://github.com/CornDavid5/ftgo-application/blob/master/ftgo-order-service/src/main/java/net/chrisrichardson/ftgo/orderservice/sagas/cancelorder/CancelOrderSaga.java)
- [ReviseOrderSaga](https://github.com/CornDavid5/ftgo-application/blob/master/ftgo-order-service/src/main/java/net/chrisrichardson/ftgo/orderservice/sagas/reviseorder/ReviseOrderSaga.java)

Four services participate in this sage:
- Accounting Service, [AccountingServiceCommandHandler](https://github.com/CornDavid5/ftgo-application/blob/master/ftgo-accounting-service/src/main/java/net/chrisrichardson/ftgo/accountingservice/messaging/AccountingServiceCommandHandler.java)
- Consumer Service, [ConsumerServiceCommandHandlers](https://github.com/CornDavid5/ftgo-application/blob/master/ftgo-consumer-service/src/main/java/net/chrisrichardson/ftgo/consumerservice/domain/ConsumerServiceCommandHandlers.java)
- Kitchen Service, [KitchenServiceCommandHandler](https://github.com/CornDavid5/ftgo-application/blob/master/ftgo-kitchen-service/src/main/java/net/chrisrichardson/ftgo/kitchenservice/messagehandlers/KitchenServiceCommandHandler.java)
- Order Service, [OrderCommandHandlers](https://github.com/CornDavid5/ftgo-application/blob/master/ftgo-order-service/src/main/java/net/chrisrichardson/ftgo/orderservice/service/OrderCommandHandlers.java)

`Account` aggregate in `Account Service` is implemented using event sourcing pattern.

`Order History Service` is implemented using CQRS pattern.
- `API Gateway Service` uses API composition pattern to implement the REST endpoint for retrieving the order history.

# How to Deploy
Prerequisite:
- Java 8
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
- account service - http://localhost:8085/swagger-ui/index.html
- order history service - http://localhost:8086/swagger-ui/index.html
- delivery service - http://localhost:8089/swagger-ui/index.html

# How to Run Unit Test and Generate Coverage
Prerequisite:
- Java 8
- docker-compose

Steps:
- Clone my instrumented repo, run `git clone git@github.com:CornDavid5/ftgo-application.git`
- Generate coverage report, run `./gradlew jacocoTestReport`
- You can find coverage report for core components in their respective folder and under `build/jacoco` folder

Or, you can find the generated coverage in [here](./coverage/)

# How to Run Integration Test
Steps:
- Clone my instrumented repo, run `git clone git@github.com:CornDavid5/ftgo-application.git`
- Start the project using steps mentioned in the `How to Deploy` section
- Go back to this folder
- Go to the integration test folder, run `cd integration-test-local`
- Run integration tests, run `mvn clean test`