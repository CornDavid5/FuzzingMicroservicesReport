# Social Network
This end-to-end microservice simulate a real social media network.

Important:
- The following report only considers the [docker default deployment](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/docker-compose.yml) scenario.
- This project also supports `TLS`, `docker swarm`, and `redis sharding` mode.
- This project also supports deployment using `openshift`, and the service interaction graph in the README is based on this `openshift` deployment.

# Task 2 & Task 3
There are totally 27 microservices in this project. 2 for front-end, 13 for business logic, 11 for caching and storage, and 1 for tracing.

### Notes for Task 2 and Task 3
- The interaction between services is done by Apache Thrift RPC call. The data passed in each RPC call is implementation detail, I only include the thrift struct in each section not the actual data.

### Service Summary
Following sections are all collapsible, expand them to see more.

<details>
  <summary>jaeger-agent</summary>

  ### Functionality
  distributed tracing system

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/config/jaeger-config.yml)

  ### Interactions
  All frontend and business logic microservice will connect to jaeger to provide tracing information.

</details>

<details>
  <summary>media-frontend</summary>

  ### Functionality
  a OpenResty/nginx server to serve incoming user request for querying media data.

  ### Related Files
  [nginx config](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/media-frontend/conf)
  [request handler](https://github.com/delimitrou/DeathStarBench/tree/master/socialNetwork/media-frontend/lua-scripts)
  [docker](https://github.com/delimitrou/DeathStarBench/tree/master/socialNetwork/docker/media-frontend)

  ### Interactions

</details>

<details>
  <summary>media-memcached</summary>

  ### Functionality
  This service provides caching for `media-frontend`

  ### Related Files

  ### Interactions

</details>

<details>
  <summary>media-mongodb</summary>

  ### Functionality
  This service provides storage for all image used by `media-frontend`

  ### Related Files

  ### Interactions

</details>

<details>
  <summary>nginx-thrift</summary>

  ### Functionality
  a OpenResty/nginx server to serve incoming user request for interacting with user, user timeline, social graph, and post related data.

  ### Related Files
  [nginx config](https://github.com/delimitrou/DeathStarBench/tree/master/socialNetwork/nginx-web-server/conf)
  [request handler](https://github.com/delimitrou/DeathStarBench/tree/master/socialNetwork/nginx-web-server/lua-scripts)
  [webpage resources](https://github.com/delimitrou/DeathStarBench/tree/master/socialNetwork/nginx-web-server/pages)
  [tracing config](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/nginx-web-server/jaeger-config.json)
  [Thrift Autogen](https://github.com/delimitrou/DeathStarBench/tree/master/socialNetwork/gen-lua)
  [docker](https://github.com/delimitrou/DeathStarBench/tree/master/socialNetwork/docker/openresty-thrift)

  ### Interactions

</details>

<details>
  <summary>unique-id-service</summary>

  ### Functionality
  This service generates 64-bit unique id with following composition:

  11 bit machine ID + 40-bit timestamp + 12-bit counter
  - 11-bit machine id code by hasing the MAC address
  - 40-bit UNIX timestamp in millisecond precision with custom epoch
  - 12 bit counter which increases monotonically on single process

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/config/service-config.json#L10)
  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/socialNetwork/src/UniqueIdService)
  [Thrift Autogen](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/gen-cpp)

  ### Interactions

</details>

<details>
  <summary>url-shorten-service</summary>

  ### Functionality
  This service generates shorten url in following format: `http://short-url/ + 10 random characters`

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/config/service-config.json#L192)
  [Thrift Autogen]()

  ### Interactions

</details>

<details>
  <summary>url-shorten-memcached</summary>

  ### Functionality
  This service provides caching for `url-shorten-service`

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/config/service-config.json#L25)

  ### Interactions

</details>

<details>
  <summary>url-shorten-mongodb</summary>

  ### Functionality
  This service provides storage for `url-shorten-service`

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/config/service-config.json#L185)

  ### Interactions

</details>

<details>
  <summary>media-service</summary>

  ### Functionality
  This service lets user to create post that contain image

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/config/service-config.json#L18)
  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/socialNetwork/src/MediaService)
  [Thrift Autogen](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/gen-cpp)

  ### Interactions

</details>



<details>
  <summary>text-service</summary>

  ### Functionality
  This service lets user create text post

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/config/service-config.json#L102)
  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/socialNetwork/src/TextService)
  [Thrift Autogen](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/gen-cpp)

  ### Interactions

</details>

<details>
  <summary>user-mention-service</summary>

  ### Functionality
  This service lets user create post that tag/mention/@ other user.

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/config/service-config.json#L157)
  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/socialNetwork/src/UserMentionService)
  [Thrift Autogen](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/gen-cpp)

  ### Interactions

</details>

<details>
  <summary>home-timeline-service</summary>

  ### Functionality
  This service generate a global timeline

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/config/service-config.json#L178)
  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/socialNetwork/src/HomeTimelineService)
  [Thrift Autogen](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/gen-cpp)

  ### Interactions

</details>

<details>
  <summary>home-timeline-redis</summary>

  ### Functionality
  This service provides caching for `home-timeline-service`

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/config/service-config.json#L117)
  [Thrift Autogen](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/gen-cpp)

  ### Interactions

</details>

<details>
  <summary>compose-post-service</summary>

  ### Functionality
  This service creates the post and update post database and timeline databases.

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/config/service-config.json#L127)
  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/socialNetwork/src/ComposePostService)
  [Thrift Autogen](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/gen-cpp)

  ### Interactions

</details>

<details>
  <summary>user-service</summary>

  ### Functionality
  This service provides functionality to register user, compose creator, and verify user login.

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/config/service-config.json#L134)
  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/socialNetwork/src/UserService)
  [Thrift Autogen](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/gen-cpp)

  ### Interactions

</details>

<details>
  <summary>user-memcached</summary>

  ### Functionality
  This service caches all `username:user_id` and `username:login` data

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/config/service-config.json#L87)

  ### Interactions

</details>

<details>
  <summary>user-mongodb</summary>

  ### Functionality
  This service stores all user data

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/config/service-config.json#L80)

  ### Interactions

</details>

<details>
  <summary>social-graph-service</summary>

  ### Functionality

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/config/service-config.json#L33)
  [source code](https://github.com/delimitrou/DeathStarBench/tree/master/socialNetwork/src/SocialGraphService)
  [Thrift Autogen](https://github.com/delimitrou/DeathStarBench/tree/master/socialNetwork/gen-cpp)

  ### Interactions

</details>

<details>
  <summary>social-graph-mongodb</summary>

  ### Functionality

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/config/service-config.json#L2)

  ### Interactions

</details>

<details>
  <summary>social-graph-redis</summary>

  ### Functionality

  ### Related Files
  [config](https://github.com/delimitrou/DeathStarBench/blob/master/socialNetwork/config/service-config.json#L49)

  ### Interactions

</details>

<details>
  <summary></summary>

  ### Functionality

  ### Related Files
  [config]()
  [source code]()
  [Thrift Autogen]()

  ### Interactions

</details>

<details>
  <summary></summary>

  ### Functionality

  ### Related Files
  [config]()
  [source code]()
  [Thrift Autogen]()

  ### Interactions

</details>

<details>
  <summary></summary>

  ### Functionality

  ### Related Files
  [config]()
  [source code]()
  [Thrift Autogen]()

  ### Interactions

</details>

<details>
  <summary></summary>

  ### Functionality

  ### Related Files
  [config]()
  [source code]()
  [Thrift Autogen]()

  ### Interactions

</details>

<details>
  <summary></summary>

  ### Functionality

  ### Related Files
  [config]()
  [source code]()
  [Thrift Autogen]()

  ### Interactions

</details>

<details>
  <summary></summary>

  ### Functionality

  ### Related Files
  [config]()
  [source code]()
  [Thrift Autogen]()

  ### Interactions

</details>


# Note
- Ubuntu default app repository only have docker compose v1. If you have install docker-compose using `apt install`, you will encounter following error: `Version in "./docker-compose.yml" is unsupported.` Fix: [link](https://stackoverflow.com/questions/42139982/version-in-docker-compose-yml-is-unsupported-you-might-be-seeing-this-error)