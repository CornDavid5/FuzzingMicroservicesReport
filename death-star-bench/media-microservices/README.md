# Media Microservices
This end-to-end microservice simulate then IMDB.

Important:
- The following report only considers the [docker default deployment](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/docker-compose.yml) scenario.
- This project also supports `redis sharding` mode.
- This project also supports deployment using `openshift`.

# Task 2 & Task 3
There are totally 33 microservices in this project. 1 for front-end, 1 for tracing, 1 for network management, 12 for business logic, and 18 for caching and storage.

### Notes for Task 2 and Task 3
- The interaction between services is done by Apache Thrift RPC call. The data passed in each RPC call is implementation detail, the data presented in each RPC call is not actual data. Also, each call will also pass a carrier object for tracing purpose, I didn't include them in the following section.
- Thrift defines RPC calls and related data types can be found in [here](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/media_service.thrift)
- The autogenerated code for business logic service by Thrift is in [here](https://github.com/delimitrou/DeathStarBench/tree/master/mediaMicroservices/gen-cpp)
- The interaction between service and jaeger is ignored in the following section.

### Service Summary
Following sections are all collapsible, expand them to see more.

<details>
  <summary>nginx-web-server</summary>

  ### Functionality
  This service serves as an API gateway, it serves all the incoming requests.

  It provides following endpoints:
  - /wrk2-api/user/register
  - /wrk2-api/movie/register
  - /wrk2-api/review/compose
  - /wrk2-api/movie-info/write
  - /wrk2-api/cast-info/write
  - /wrk2-api/plot/write

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/tree/master/mediaMicroservices/nginx-web-server/conf)

  [request handler](https://github.com/delimitrou/DeathStarBench/tree/master/mediaMicroservices/nginx-web-server/lua-scripts)

  ### Interactions
  | Service | URL | RPC Call | Data Sent |
  | --- | --- | --- | --- |
  | cast-info-service | /wrk2-api/cast-info/write | WriteCastInfo | (req_id, cast_id, cast_name, cast_gender, cast_intro) |
  | movie-info-service | /wrk2-api/movie-info/write | WriteMovieInfo | (req_id, movie_id, movie_title, casts, movie_plot_id, movie_thumbnail_ids, movie_photo_ids, movie_video_ids, movie_avg_rating, movie_num_rating) |
  | movie-id-service | /wrk2-api/movie/register |RegisterMovieId | (req_id, title, movie_id) |
  | plot-service | /wrk2-api/plot/write | WritePlot | (req_id, plot_id, plot) |
  | user-service | /wrk2-api/user/register | RegisterUser | (req_id, first_name, last_name, username, password) |
  | user-service | /wrk2-api/review/compose | UploadUserWithUsername | (req_id, username) |
  | text-service | /wrk2-api/review/compose | UploadText | (req_id, text) |
  | movie-id-service | /wrk2-api/review/compose | UploadMovieId | (req_id, title, rating) |
  | unique-id-service | /wrk2-api/review/compose | UploadUniqueId | (req_id) |

</details>

<details>
  <summary>jaeger</summary>

  ### Functionality
  distributed tracing system

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/jaeger-config.yml)

  ### Interactions
  All frontend and business logic microservices will connect to jaeger to provide tracing information.

</details>

<details>
  <summary>dns-media</summary>

  ### Functionality
  This service provides dns lookup service and enables resolving container hostname from host machine

  ### Related Files
  N/A

  ### Interactions
  All inter service need to go through this service to resolve ip address

</details>

<details>
  <summary>unique-id-service</summary>

  ### Functionality
  This service generates 64-bit unique id with following composition:

  12 bit machine ID + 40-bit timestamp + 12-bit counter
  - 12-bit machine id code by hasing the MAC address
  - 40-bit UNIX timestamp in millisecond precision with custom epoch
  - 12 bit counter which increases monotonically on single process

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L3)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/mediaMicroservices/src/UniqueIdService)

  ### Interactions
  | Service | RPC Call | Data Sent |
  | --- | --- | --- |
  | compose-review-service | UploadUniqueId | (req_id, review_id) |

</details>

<details>
  <summary>movie-id-service</summary>

  ### Functionality

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L7)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/mediaMicroservices/src/MovieIdService)

  ### Interactions
  | Service | RPC Call | Data Sent |
  | --- | --- | --- |
  | compose-review-service | UploadMovieId | (req_id, movie_id) |
  | rating-service | UploadRating | (req_id, movie_id, rating) |

  | Service | Database | Collection | Data Sent |
  | --- | --- | --- | --- |
  | movie-id-mongodb | movie-id | movie-id | (title) |
  | movie-id-mongodb | movie-id | movie-id | (title, movie_id) |

  | Service | Method | Data Sent |
  | --- | --- | --- |
  | movie-id-memcached | get | title |
  | movie-id-memcached | set | title = (movie_id) |

</details>

<details>
  <summary>movie-id-mongodb</summary>

  ### Functionality
  This service stores all movie id and title data

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L11)

  ### Interactions
  N/A

</details>

<details>
  <summary>movie-id-memcached</summary>

  ### Functionality
  This service provides caching for `movie-id-service`

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L15)

  ### Interactions
  N/A

</details>

<details>
  <summary>text-service</summary>

  ### Functionality

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L27)

  [source code](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/src/TextService)

  ### Interactions
  | Service | RPC Call | Data Sent |
  | --- | --- | --- |
  | compose-review-service | UploadText | (req_id, movie_id) |

</details>

<details>
  <summary>rating-service</summary>

  ### Functionality
  This service provides functionality to manage movie rating

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L31)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/mediaMicroservices/src/RatingService)

  ### Interactions
  | Service | RPC Call | Data Sent |
  | --- | --- | --- |
  | compose-review-service | UploadRating | (req_id, rating) |

  | Service | Method | Data Sent |
  | --- | --- | --- |
  | rating-redis | inc | movie_id + ":uncommit_sum" |

</details>

<details>
  <summary>rating-redis</summary>

  ### Functionality
  This service provides caching for `rating-service`

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L35)

  ### Interactions
  N/A

</details>

<details>
  <summary>user-service</summary>

  ### Functionality
  This service provides functionality to manage user

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L39)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/mediaMicroservices/src/UserService)

  ### Interactions
  | Service | RPC Call | Data Sent |
  | --- | --- | --- |
  | compose-review-service | UploadUserId | (req_id, user_id) |

  | Service | Database | Collection | Data Sent |
  | --- | --- | --- | --- |
  | user-mongodb | user | user | (username) |
  | user-mongodb | user | user | (user_id, first_name, last_name, username, salt, salted_password) |

  | Service | Method | Data Sent |
  | --- | --- | --- |
  | user-memcached | get | username + ":user_id" |
  | user-memcached | set | username + ":user_id" = user_id |
  | user-memcached | get | username + ":password" |
  | user-memcached | set | username + ":password" = password |
  | user-memcached | get | username + ":salt" |
  | user-memcached | set | username + ":salt" = salt |

</details>

<details>
  <summary>user-mongodb</summary>

  ### Functionality
  This service store all user data

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L19)

  ### Interactions
  N/A

</details>

<details>
  <summary>user-memcached</summary>

  ### Functionality
  This service provides caching for `user-service`

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L23)

  ### Interactions
  N/A

</details>

<details>
  <summary>compose-review-service</summary>

  ### Functionality

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L43)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/mediaMicroservices/src/ComposeReviewService)

  ### Interactions
  | Service | RPC Call | Data Sent |
  | --- | --- | --- |
  | review-storage-service | StoreReview | (req_id, new_review) |
  | user-review-service | UploadUserReview | (req_id, new_review.user_id, new_review.review_id, new_review.timestamp) |
  | movie-review-service | UploadMovieReview | (req_id, new_review.movie_id, new_review.review_id, new_review.timestamp) |

  | Service | Method | Data Sent |
  | --- | --- | --- |
  | compose-review-memcached | get | unique_id + ":review_id" |
  | compose-review-memcached | add | unique_id + ":review_id" = (unique_id) |
  | compose-review-memcached | get | movie_id + ":movie_id"|
  | compose-review-memcached | add | movie_id + ":movie_id" = (movie_id) |
  | compose-review-memcached | get | user_id + ":user_id" |
  | compose-review-memcached | add | user_id + ":user_id" = (user_id) |
  | compose-review-memcached | get | text + ":text" |
  | compose-review-memcached | add | text + ":text" = (text) |
  | compose-review-memcached | get | rating + ":rating" |
  | compose-review-memcached | add | rating + ":rating" = (rating) |
  | compose-review-memcached | get | req_id + ":counter" |
  | compose-review-memcached | add | req_id + ":counter" = (0) |
  | compose-review-memcached | inc | req_id + ":counter" |

</details>

<details>
  <summary>compose-review-memcached</summary>

  ### Functionality
  This service provides caching for `compose-review-service`

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L47)

  ### Interactions
  N/A

</details>

<details>
  <summary>review-storage-service</summary>

  ### Functionality

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L51)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/mediaMicroservices/src/ReviewStorageService)

  ### Interactions
  | Service | Database | Collection | Data Sent |
  | --- | --- | --- | --- |
  | review-storage-mongodb | review | review | (req_id, review_id, timestamp, user_id, movie_id, text, rating) |

  | Service | Method | Data Sent |
  | --- | --- | --- |
  | review-storage-memcached | get | review_id |
  | review-storage-memcached | set | review_id = review_id |

</details>

<details>
  <summary>review-storage-mongodb</summary>

  ### Functionality
  This service stores all review data

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L55)

  ### Interactions
  N/A

</details>

<details>
  <summary>review-storage-memcached</summary>

  ### Functionality
  This service provides caching for `review-storage-service`

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L59)

  ### Interactions
  N/A

</details>

<details>
  <summary>user-review-service</summary>

  ### Functionality

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L63)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/mediaMicroservices/src/UserReviewService)

  ### Interactions
  | Service | RPC Call | Data Sent |
  | --- | --- | --- |
  | review-storage-service | ReadReviews | (req_id, review_ids) |

  | Service | Database | Collection | Data Sent |
  | --- | --- | --- | --- |
  | user-review-mongodb | user-review | user-review | (user_id) |
  | user-review-mongodb | user-review | user-review | (user_id, reviews) |

  | Service | Method | Data Sent |
  | --- | --- | --- |
  | user-review-redis | get | user_id |
  | user-review-redis | add | user_id = (review_id, timestamp) |
  | user-review-redis | del | user_id |

</details>

<details>
  <summary>user-review-mongodb</summary>

  ### Functionality
  This service stores all user review data

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L67)

  ### Interactions
  N/A

</details>

<details>
  <summary>user-review-redis</summary>

  ### Functionality
  This service provides caching for `user-review-service`

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L71)

  ### Interactions
  N/A

</details>

<details>
  <summary>movie-review-service</summary>

  ### Functionality

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L75)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/mediaMicroservices/src/MovieReviewService)

  ### Interactions
  | Service | RPC Call | Data Sent |
  | --- | --- | --- |
  | review-storage-service | ReadReviews | (req_id, review_ids) |

  | Service | Database | Collection | Data Sent |
  | --- | --- | --- | --- |
  | movie-review-mongodb | movie-review | movie-review | (movie_id) |
  | movie-review-mongodb | movie-review | movie-review | (movie_id, reviews) |

  | Service | Method | Data Sent |
  | --- | --- | --- |
  | movie-review-redis | get | movie_id |
  | movie-review-redis | add | movie_id = (review_id, timestamp) |
  | movie-review-redis | del | movie_id |

</details>

<details>
  <summary>movie-review-mongodb</summary>

  ### Functionality
  This service stores all movie review data

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L79)

  ### Interactions
  N/A

</details>

<details>
  <summary>movie-review-redis</summary>

  ### Functionality
  This service provides caching for `movie-review-service`

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L83)

  ### Interactions
  N/A

</details>

<details>
  <summary>cast-info-service</summary>

  ### Functionality

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L87)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/mediaMicroservices/src/CastInfoService)

  ### Interactions
  | Service | Database | Collection | Data Sent |
  | --- | --- | --- | --- |
  | cast-info-mongodb | cast-info | cast-info | (cast_info_id, name, gender, intro) |

  | Service | Method | Data Sent |
  | --- | --- | --- |
  | cast-info-memcached | get | cast_info_id |
  | cast-info-memcached | set | cast_info_id = cast_info_id |

</details>

<details>
  <summary>cast-info-mongodb</summary>

  ### Functionality
  This service stores all cast info data

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L91)

  ### Interactions
  N/A

</details>

<details>
  <summary>cast-info-memcached</summary>

  ### Functionality
  This service provides caching for `cast-info-service`

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L95)

  ### Interactions
  N/A

</details>

<details>
  <summary>plot-service</summary>

  ### Functionality

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L99)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/mediaMicroservices/src/PlotService)

  ### Interactions
  | Service | Database | Collection | Data Sent |
  | --- | --- | --- | --- |
  | plot-mongodb | plot | plot | (plot_id) |
  | plot-mongodb | plot | plot | (plot_id, plot) |

  | Service | Method | Data Sent |
  | --- | --- | --- |
  | plot-memcached | get | plot_id |
  | plot-memcached | set | plot_id = (ploy_id) |

</details>

<details>
  <summary>plot-mongodb</summary>

  ### Functionality
  This service stores all plot data

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L103)

  ### Interactions
  N/A

</details>

<details>
  <summary>plot-memcached</summary>

  ### Functionality
  This service provides caching for `plot-service`

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L107)

  ### Interactions
  N/A

</details>

<details>
  <summary>movie-info-service</summary>

  ### Functionality

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L111)

  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/mediaMicroservices/src/MovieInfoService)

  ### Interactions
  | Service | Database | Collection | Data Sent |
  | --- | --- | --- | --- |
  | movie-info-mongodb | movie-info | movie-info | (movie_id) |
  | movie-info-mongodb | movie-info | movie-info | (movie_id, title, plot_id, avg_rating, num_rating, casts, thumbnail_ids, photo_ids, video_ids) |

  | Service | Method | Data Sent |
  | --- | --- | --- |
  | movie-info-memcached | get | movie_id |
  | movie-info-memcached | set | movie_id = movie_info |

</details>

<details>
  <summary>movie-info-mongodb</summary>

  ### Functionality
  This service stores all movie info data

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L115)

  ### Interactions
  N/A

</details>

<details>
  <summary>movie-info-memcached</summary>

  ### Functionality
  This service provides caching for `movie-info-service`

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/mediaMicroservices/config/service-config.json#L119)

  ### Interactions
  N/A

</details>


# Task 3
This report provide a Python script to generate a html file for interactive visualization.

Note:
- The following graph doesn't include the interaction between jaeger and other services and interaction between dns-media and other services.

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
