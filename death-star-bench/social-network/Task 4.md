# How to Run
Following steps is for `docker compose` deployment:
- run `docker build -t deathstarbench/social-network-microservices:local .`
- run `docker compose up`

# Test Coverage
This project provides Python scripts to test all of business logic related services, but not include frontend, caching services, and data storage services.

- instrument the project
- you may need to remove all exited container generated from `How to Run` section to prevent `duplicated key` error.
- run `docker build -t deathstarbench/social-network-microservices:local .`
- run `docker compose up`
- go to test folder, enable virtual environment, run `./run-test.bash` twice
- terminate docker containers, then restart docker containers
- you have to do the following command for each service, change the service name for different services
- ssh into the docker container, `docker exec -it <container-id> bash`
- `cd /social-network-microservices/build/src/<service-name>/CMakeFiles/<service-name>.dir`
- `gcov -o . -b /social-network-microservices/src/<service-name>/<service-name>.cpp | grep -A 5 <service-name>`
- `mkdir report && lcov --capture --directory . --output-file report/report.info`
- `genhtml report/report.info --output-directory report`
- `cp -r report/ /social-network-microservices/coverage/<service-name>/`