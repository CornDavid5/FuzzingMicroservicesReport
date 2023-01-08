# How to Run
Following steps is for `docker compose` deployment:
- modify the `config.json` file, change the last three lines:
    ```
    "UserMongoAddress": "mongodb-user:27017",
    "KnativeDomainName":  "default.10.108.189.25.sslip.io:80"
    }
    ```
    to
    ```
    "UserMongoAddress": "mongodb-user:27017"
    }
    ```
    - we have to modify the configuration file to make it work, because the original configuration file is for `kubernetes` deployment, without modification, you will have DNS issue.
- run `docker compose build`
- run `docker compose up`
    - this project also supports additional configurations, check out [here](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation#docker-compose)
    - you can pass them as environment variables, for example, `LOG_LEVEL=trace docker compose up`
- open a browser and test it
    - default port is `5000`

Supported endpoints and parameters:
- /
- /hotels
    - required parameters: inDate, outDate, lat, lon
    - optional parameters: locale
    - example: http://localhost:5000/hotels?inDate=2015-04-12&outDate=2015-04-13&lat=38.106&lon=-122.043
- /recommendations
    - required parameters: lat, lon, require
    - optional parameters: locale
    - note: the value of require parameter has to be one of the following: dis, rate, price
- /user
    - required parameters: username, password
- /reservation
    - required parameters: inDate, outDate, hotelId, customerName, username, password, number
    - note: number is the number of room you want to reserve

Note:
- each service will pre-populate the database with some data, you can find them in the `db.go` file under each service's folder in [here](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/cmd)

# Test Coverage
This project is written in `Go`, and there is no test presented in the source code. This is done deliberately by the author of this project, they choose to remove any test in the source code, as you can see in this [configuration](https://github.com/delimitrou/DeathStarBench/blob/master/hotelReservation/Gopkg.toml#L57)
