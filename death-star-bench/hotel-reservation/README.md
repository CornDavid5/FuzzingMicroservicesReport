# Hotel Reservation
This end-to-end microservice simulate a hotel reservation system.

Important:
- The following report only considers the [docker default deployment](https://github.com/delimitrou/DeathStarBench/blob/master/hotelReservation/docker-compose.yml) scenario.
- This project also supports `TLS`, and `docker swarm`.
- This project also supports deployment using `openshift` or `kubernetes`.

# Task 2 & Task 3
There are totally 19 microservices in this project. 1 for network management, 1 for tracing, 1 for front-end, 7 for business logic, and 9 for caching and storage.

### Notes for Task 2 and Task 3
- The interaction between services is done by gRPC call. The data passed in each RPC call is implementation detail, the data presented in each RPC call is not actual data.
- The interaction between service and jaeger is ignored in the following section.

### Service Summary
Following sections are all collapsible, expand them to see more.

<details>
  <summary>consul</summary>

  ### Functionality
  This service provides registry functionality, it can register a service with its IP address and port, and provide service look up for gRPC.

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/hotelReservation/config.json#L2)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/registry)

  ### Interactions
  Business logic services will connect to consul to register their services.

</details>

<details>
  <summary>hotel_reserv_jaeger</summary>

  ### Functionality
  distributed tracing system

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/hotelReservation/config.json#L3)

  ### Interactions
  Business logic services and frontend will connect to jaeger to provide tracing information.

</details>

<details>
  <summary>hotel_reserv_frontend</summary>

  ### Functionality
  The frontend platform with which user will be interacted

  It provides following endpoints:
  - /
  - /hotels
  - /recommendations
  - /user
  - /reservation

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/hotelReservation/config.json#L4)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/services/frontend)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/cmd/frontend)

  ### Interactions
  | Service | RPC Call | Data Sent |
  | --- | --- | --- |
  | hotel_reserv_profile | GetProfiles | (HotelIds, Locale) |
  | hotel_reserv_search | Nearby | (Lat, Lon, InDate, OutDate) |
  | hotel_reserv_recommendation | GetRecommendations | (Require, Lat, Lon,) |
  | hotel_reserv_user | CheckUser | (Username, Password) |
  | hotel_reserv_reservation | CheckAvailability | (CustomerName, HotelId, InDate, OutDate, RoomNumber) |
  | hotel_reserv_reservation | MakeReservation | (CustomerName, HotelId, InDate, OutDate, RoomNumber) |

</details>

<details>
  <summary>hotel_reserv_profile</summary>

  ### Functionality
  This service provides functionality to manage hotel profile

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/hotelReservation/config.json#L7)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/services/profile)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/cmd/profile)

  [proto](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/services/profile/proto)

  ### Interactions
  | Service | Database | Collection | Data Sent |
  | --- | --- | --- | --- |
  | hotel_reserv_profile_mongo | profile-db | hotels | HotelId |

  | Service | Method | Data Sent |
  | --- | --- | --- |
  | hotel_reserv_profile_mmc | get | HotelId |
  | hotel_reserv_profile_mmc | set | HotelId = Hotel |

</details>

<details>
  <summary>hotel_reserv_profile_mmc</summary>

  ### Functionality
  This service provides caching for `hotel_reserv_profile` service

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/hotelReservation/config.json#L9)

  ### Interactions
  N/A

</details>

<details>
  <summary>hotel_reserv_profile_mongo</summary>

  ### Functionality
  This service stores all hotel profile data

  ``` go
    type Hotel struct {
        Id          string   `bson:"id"`
        Name        string   `bson:"name"`
        PhoneNumber string   `bson:"phoneNumber"`
        Description string   `bson:"description"`
        Address     *Address `bson:"address"`
    }

    type Address struct {
        StreetNumber string  `bson:"streetNumber"`
        StreetName   string  `bson:"streetName"`
        City         string  `bson:"city"`
        State        string  `bson:"state"`
        Country      string  `bson:"country"`
        PostalCode   string  `bson:"postalCode"`
        Lat          float32 `bson:"lat"`
        Lon          float32 `bson:"lon"`
    }
  ```

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/hotelReservation/config.json#L8)

  ### Interactions
  N/A

</details>

<details>
  <summary>hotel_reserv_search</summary>

  ### Functionality
  This service provides search functionality

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/hotelReservation/config.json#L18)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/services/search)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/cmd/search)

  [proto](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/services/search/proto)

  ### Interactions
  | Service | RPC Call | Data Sent |
  | --- | --- | --- |
  | hotel_reserv_rate | GetRates | (HotelIds, InDate, OutDate) |
  | hotel_reserv_geo | Nearby | (Lat, Lon) |

</details>


<details>
  <summary>hotel_reserv_geo</summary>

  ### Functionality
  This service provides functionality to manage hotel geographic location

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/hotelReservation/config.json#L5)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/services/geo)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/cmd/geo)

  [proto](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/services/geo/proto)

  ### Interactions
  | Service | Database | Collection | Data Sent |
  | --- | --- | --- | --- |
  | hotel_reserv_geo_mongo | geo-db | geo | point |

</details>

<details>
  <summary>hotel_reserv_geo_mongo</summary>

  ### Functionality
  This service stores all hotel geographic location data

  ``` go
    type point struct {
        Pid  string  `bson:"hotelId"`
        Plat float64 `bson:"lat"`
        Plon float64 `bson:"lon"`
    }
  ```

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/hotelReservation/config.json#L6)

  ### Interactions
  N/A

</details>

<details>
  <summary>hotel_reserv_rate</summary>

  ### Functionality
  This service provides rate functionality

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/hotelReservation/config.json#L10)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/services/rate)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/cmd/rate)

  [proto](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/services/rate/proto)

  ### Interactions
  | Service | Database | Collection | Data Sent |
  | --- | --- | --- | --- |
  | hotel_reserv_rate_mongo | rate-db | inventory | HotelId |

  | Service | Method | Data Sent |
  | --- | --- | --- |
  | hotel_reserv_rate_mmc | get | HotelId |
  | hotel_reserv_rate_mmc | set | HotelId = RatePlans |

</details>

<details>
  <summary>hotel_reserv_rate_mmc</summary>

  ### Functionality
  This service provides caching for `hotel_reserv_rate` service

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/hotelReservation/config.json#L12)

  ### Interactions
  N/A

</details>

<details>
  <summary>hotel_reserv_rate_mongo</summary>

  ### Functionality
  This service stores all rate data

  ``` go
    type RoomType struct {
        BookableRate       float64 `bson:"bookableRate"`
        Code               string  `bson:"code"`
        RoomDescription    string  `bson:"roomDescription"`
        TotalRate          float64 `bson:"totalRate"`
        TotalRateInclusive float64 `bson:"totalRateInclusive"`
    }

    type RatePlan struct {
        HotelId  string    `bson:"hotelId"`
        Code     string    `bson:"code"`
        InDate   string    `bson:"inDate"`
        OutDate  string    `bson:"outDate"`
        RoomType *RoomType `bson:"roomType"`
    }
  ```

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/hotelReservation/config.json#L11)

  ### Interactions
  N/A

</details>

<details>
  <summary>hotel_reserv_recommendation</summary>

  ### Functionality
  This service generates hotel recommendation

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/hotelReservation/config.json#L13)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/services/recommendation)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/cmd/recommendation)

  [proto](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/services/recommendation/proto)

  ### Interactions
  | Service | Database | Collection | Data Sent |
  | --- | --- | --- | --- |
  | hotel_reserv_recommendation_mongo | recommendation-db | recommendation | HotelProfiles |

</details>

<details>
  <summary>hotel_reserv_recommendation_mongo</summary>

  ### Functionality
  This service stores all hotel recommendation data

  ``` go
    type Hotel struct {
        HId    string  `bson:"hotelId"`
        HLat   float64 `bson:"lat"`
        HLon   float64 `bson:"lon"`
        HRate  float64 `bson:"rate"`
        HPrice float64 `bson:"price"`
    }
  ```

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/hotelReservation/config.json#L14)

  ### Interactions
  N/A

</details>

<details>
  <summary>hotel_reserv_user</summary>

  ### Functionality
  This service provides functionality to manage user

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/hotelReservation/config.json#L19)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/services/user)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/cmd/user)

  [proto](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/services/user/proto)

  ### Interactions
  | Service | Database | Collection | Data Sent |
  | --- | --- | --- | --- |
  | hotel_reserv_user_mongo | user-db | user | Users |

</details>

<details>
  <summary>hotel_reserv_user_mongo</summary>

  ### Functionality
  This service stores all user data

  ``` go
    type User struct {
        Username string `bson:"username"`
        Password string `bson:"password"`
    }
  ```

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/hotelReservation/config.json#L20)

  ### Interactions
  N/A

</details>

<details>
  <summary>hotel_reserv_reservation</summary>

  ### Functionality

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/hotelReservation/config.json#L15)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/services/reservation)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/cmd/reservation)

  [proto](https://github.com/delimitrou/DeathStarBench/tree/master/hotelReservation/services/reservation/proto)

  ### Interactions
  | Service | Database | Collection | Data Sent |
  | --- | --- | --- | --- |
  | hotel_reserv_reservation_mongo | reservation-db | reservation | (HotelId, InDate, OutDate) |
  | hotel_reserv_reservation_mongo | reservation-db | reservation | (HotelId, CustomerName, InDate, OutDate, Number) |
  | hotel_reserv_reservation_mongo | reservation-db | number | HotelId |

  | Service | Method | Data Sent |
  | --- | --- | --- |
  | hotel_reserv_reservation_mmc | get | HotelId + "_" + InDate + "_" + OutDate |
  | hotel_reserv_reservation_mmc | set | HotelId + "_" + InDate + "_" + OutDate = NumOfRemainingRooms |
  | hotel_reserv_reservation_mmc | get | HotelId + "_cap" |
  | hotel_reserv_reservation_mmc | set | HotelId + "_cap" = NumOfRemainingRooms |

</details>

<details>
  <summary>hotel_reserv_reservation_mmc</summary>

  ### Functionality
  This service provides caching for `hotel_reserv_reservation` service

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/hotelReservation/config.json#L17)

  ### Interactions
  N/A

</details>

<details>
  <summary>hotel_reserv_reservation_mongo</summary>

  ### Functionality
  This service stores all reservation data

  ``` go
    type Reservation struct {
        HotelId      string `bson:"hotelId"`
        CustomerName string `bson:"customerName"`
        InDate       string `bson:"inDate"`
        OutDate      string `bson:"outDate"`
        Number       int    `bson:"number"`
    }

    type Number struct {
        HotelId string `bson:"hotelId"`
        Number  int    `bson:"numberOfRoom"`
    }
  ```

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/hotelReservation/config.json#L16)

  ### Interactions
  N/A

</details>

# Task 3
This report provide a Python script to generate a html file for interactive visualization.

Note:
- The following graph doesn't include the interaction between jaeger and other services and the interaction between consul and other services.

Prerequisite:
- Python 3.10+

Generate graph steps:
- (optional) create a virtual environment
- install dependencies, run `python -m pip install -r requirements.txt`
- generate html, run `python generate.py`
  - note: due to a bug in the visualization library, depends on wether you have set the `BROWSER` environement variable, this step will start the browser and open a empty page or it will print out some error messages in the terminal, please ignore them
- manully open the html file in your browser

Interact with the graph:
- you can drag the view
- you can zoom in and zoom out
- you can drag the node
- you can select and highlight a node
- you can hover over an edge to see more details about the interaction

