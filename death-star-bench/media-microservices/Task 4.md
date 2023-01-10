# How to Run
Following steps is for `docker compose` deployment:
- run `docker build -t yg397/media-microservices:local .`
- run `docker compose up -d`
- populate database, `cd scripts && python3 write_movie_info.py && ./register_users.sh && ./register_movies.sh && cd ..`
- test it, `'curl -X POST http://localhost:8080/wrk2-api/cast-info/write -H "Content-Type: application/json" -d '{ "cast_info_id": 123456, "name": "alpaca", "gender": "male", "intro": "nice" }'`

# Test Coverage
This project provides Python scripts to test all of business logic related services, but not include frontend, caching services, and data storage services.

- instrument the project
- you may need to remove all exited container generated from `How to Run` section to prevent `duplicated key` error.
- run `docker build -t yg397/media-microservices:local .`
- run `docker compose up`
- go to test folder, enable virtual environment, run `./run-test.bash` twice
- terminate docker containers, then restart docker containers
- you have to do the following command for each service, change the service name for different services
- ssh into the docker container, `docker exec -it <container-id> bash`
- `cd /media-microservices/build/src/<service-name>/CMakeFiles/<service-name>.dir`
- `gcov -o . -b /media-microservices/src/<service-name>/<service-name>.cpp | grep -A 5 <service-name>`
- `mkdir report && lcov --capture --directory . --output-file report/report.info`
- `genhtml report/report.info --output-directory report`
- `cp -r report/ /media-microservices/coverage/<service-name>/`

I couldn't get the testMovieInfoService.py to work, because some encoding issue with Thrift generated code. However, the only service that used MovieInfoService is frontend, no other business logic related service will interact with MovieInfoService, therefore, its absence will not affect other service's code coverage.

