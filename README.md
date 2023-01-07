# Fuzzing Microservices Report
Please check out corresponding folder for the task report.

## Note on Code Coverage
Following are some notes on the code coverage for each end-to-end microservice benchmarks. No number on the actual coverage yet, just some observations.

For `Train Ticket`:
- Most of the services is written in Java, and it uses `mockito` as mocking framework and `junit` as testing framework.
- Read through a few services, the tests did call all the functions in the source code, but there is a lot of if conditions in the source code, so without actually running any code coverage tools, it is hard to tell whether the coverage is high or not.

For `Media Microservices` and `Social Network`:
- Both benchmarks have similar structures and are written in C++, and they provide some Python scripts for testing end-to-end functionalities, you can find them in [here](https://github.com/delimitrou/DeathStarBench/tree/master/socialNetwork/test) and [here](https://github.com/delimitrou/DeathStarBench/tree/master/mediaMicroservices/test)
- Quick skim through the source code, seems like the Python scripts cover most of the `read` and `write` functionalities of each service. I am not sure how to check the coverage with C++ and Python combo.

For `Hotel Reservation`:
- This project is written in Go, and there is no test presented in the source code.