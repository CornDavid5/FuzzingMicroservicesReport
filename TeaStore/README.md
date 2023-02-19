# Overview
Instrumented Repo: https://github.com/CornDavid5/TeaStore
Commit: 4bea6b2e3e0cc1ea592ced988084cbccf0de4e30

This project consists of five main services:
- WebUI Service
- Authentication Service
- Recommender Service
- Persistence Provider Service
- Image Provider Service

The interaction between services is done by REST API call facilitating by a service registry.

# How to Deploy
Prerequisite:
- Java 11

Deploy:
- clone my repo, run `git clone git@github.com:CornDavid5/TeaStore.git`
- running the project without monitoring, run `docker compose -f ./examples/docker/docker-compose_default.yaml up -d`
- to access the UI, visit `http://localhost:8080/tools.descartes.teastore.webui`
![](ui.png)

# How to Run Unit Test
Only `Authentication Service`, `Recommender Service`, `Persistence Provider Service`, and `Image Provider Service` provide tests.

Steps:
- clone my repo, run `git clone git@github.com:CornDavid5/TeaStore.git && cd TeaStore`
- run `mvn test -pl services/tools.descartes.teastore.image -am`
- run `mvn test -pl services/tools.descartes.teastore.persistence -am`
- run `mvn test -pl services/tools.descartes.teastore.recommender -am`
- run `mvn test -pl services/tools.descartes.teastore.auth -am`

Note: If you run `mvn test`, you will encounter `Artifact has not been packaged yet. When used on reactor artifact, copy should be executed after packaging: see MDEP-187` error. There is still an open ticket in the [maven-dependency-plugin](https://issues.apache.org/jira/browse/MDEP-187), the potential cause is stated in [here](https://stackoverflow.com/questions/26101135/artifact-has-not-been-packaged-yet-maven-dependency-plugin). However, the problem module is an utility module with no real test, so it should be fine.

## Unit Test Coverage
Coverage report for those four testable modules could be found in [here](./unit-coverage/). The tests don't cover many of the user cases, the resulting coverage is not so high.

# How to Run Integration Test
Prerequisite:
- Java 11
- Maven 3

Steps:
- Start the project using steps mentioned in the `How to Deploy` section
- Go back to this folder
- Go to the integration test folder, run `cd integration-test-local`
- Run integration tests, run `mvn clean test`