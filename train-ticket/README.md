# Train Ticket
Train Ticket is a system to simulate real train management system, it supports user to reserve a ticket, modify or cancel a ticket, or order food, etc. It also supports administrator to manage ticket order and manage train route, etc.

Source Repository: https://github.com/FudanSELab/train-ticket

Important:
- The following report only considers the [kubernetes `quickstart` deployment](https://github.com/FudanSELab/train-ticket/blob/master/deployment/kubernetes-manifests/quickstart-k8s/yamls/deploy.yaml.sample) scenario.
- This project also supports local [docker-compose deployment](https://github.com/FudanSELab/train-ticket/blob/master/deployment/docker-compose-manifests/quickstart-docker-compose.yml), however, this deployment uses different images than that of k8s deployment, such as `redis`.
- This project also supports optional services, such as monitoring service `prometheus` and tracing service `skywalking`.
- This project also supports `Database per service` pattern.

## Task 1 & Task 2
Even though in its README file, it states that there are `41` microservices used in this project, however, there are totally `49` microservices.
- Those aforementioned 41 microservices are related to the business logic, such user management, ticket querying, etc.
- There are 3 additional bushiness logic related microservices, `ts-order-other-service`, `ts-preserve-other-service`, and `ts-travel2-service`, provide similar functionalities as their counterparts, such as `ts-order-service`, but querying different set of trains.
  - In this project, train data is divided into two group, one with train number `start with "G" or "D"`, the other is the rest.
- There is 1 microservice for ui dashboard, `ts-ui-dashboard`.
- There are 4 microservices, `rabbitmq`, `mysql`, `ts-gateway-service`, and `nacos`, they provide necessary functionalities for communication, persistence storage, api-gateway, and service discovery.

### Notes for Task 2 and Task 3
- The information in [wiki page](https://github.com/FudanSELab/train-ticket/wiki/Service-Guide-and-API-Reference) is a little out of date, need to analyze the source code to get latest changes.
- I tried to deploy the k8s locally but failed, due to the high requirements of the system.
  ```
  If you use docker-compose, please make sure you have at least 24G memory 50G disk in Linux.
  If you use k8s (+istio), please make sure you have at least four same configuration Linux servers.
  ```
- This project supports monitoring and tracing, it may be easier to analyze dependency if I could run the those services. The failed k8s deployment screenshots are in this [folder](./resources/k8s/).
- The interaction between services is done by RESTful http request. For most of the service, which is written in Java, they uses spring web framework's [RestTemplate exchange](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html) for synchronous http request. There is 1 service, `ts-voucher-service`, which is written in Python and uses [urllib](https://docs.python.org/3/library/urllib.html) library for http request.
- All RESTful request is done by sending request directly to the service's uri, because kubernetes' CoreDNS will resolve the uri. Following is example code:
  ``` java
  HttpEntity requestEntity = new HttpEntity(headers);
  ResponseEntity<Boolean> re = restTemplate.exchange(
          verification_code_service_url + "/api/v1/verifycode/verify/" + verifyCode,
          HttpMethod.GET,
          requestEntity,
          Boolean.class);
  ```
- For task 2, it requires to include the information shared between service. For interaction between service and persistent storage service, it is done by third-party library. What kind of information is exchanged by third-party library is implementation details, so I don't include them in this report.

### Service Summary
Following sections are all collapsible, expand them to see more.

<details>
  <summary>ts-admin-basic-info-service</summary>

  ### Functionality
  /api/v1/adminbasicservice/**: provide CRUD APIs to manage basic information for admin, include contacts information, station information, train information, config information and price information.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-admin-basic-info-service)

  ### Interactions
  | Service | URI | Method | Data Sent |
  | --- | --- | --- | --- |
  | ts-contacts-service | /api/v1/contactservice/contacts | GET |  |
  | ts-contacts-service | /api/v1/contactservice/contacts/{contactsId} | DELETE |   |
  | ts-contacts-service | /api/v1/contactservice/contacts | PUT | modified contact data |
  | ts-contacts-service | /api/v1/contactservice/contacts/admin | POST | new contact data |
  | ts-station-service | /api/v1/stationservice/stations | GET |  |
  | ts-station-service | /api/v1/stationservice/stations | POST | new station data |
  | ts-station-service | /api/v1/stationservice/stations/{id} | DELETE |  |
  | ts-station-service | /api/v1/stationservice/stations | PUT | modified station data |
  | ts-train-service | /api/v1/trainservice/trains | GET |  |
  | ts-train-service | /api/v1/trainservice/trains | POST | new train-type data |
  | ts-train-service | /api/v1/trainservice/trains/{id} | DELETE |  |
  | ts-train-service | /api/v1/trainservice/trains | PUT | modified train-type data |
  | ts-config-service | /api/v1/configservice/configs | GET |  |
  | ts-config-service | /api/v1/configservice/configs | POST | new config data |
  | ts-config-service | /api/v1/configservice/configs /{id} | DELETE |  |
  | ts-config-service | /api/v1/configservice/configs | PUT | modified config data |
  | ts-price-service | /api/v1/priceservice/prices | GET |  |
  | ts-price-service | /api/v1/priceservice/prices | POST | new price data |
  | ts-price-service | /api/v1/priceservice/prices/{pricesId} | DELETE |  |
  | ts-price-service | /api/v1/priceservice/prices | PUT | modified price data |

</details>

<details>
  <summary>ts-admin-order-service</summary>

  ### Functionality
  /api/v1/adminorderservice/**: provide CRUD APIs to manage order for admin.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-admin-order-service)

  ### Interactions
  | Service | URI | Method | Data Sent |
  | --- | --- | --- | --- |
  | ts-order-service | /api/v1/orderservice/order | GET | |
  | ts-order-other-service | /api/v1/orderOtherService/orderOther | GET | |
  | ts-order-service | /api/v1/orderservice/order/{orderId} | DELETE |  |
  | ts-order-other-service | /api/v1/orderOtherService/orderOther/{orderId} | DELETE | |
  | ts-order-service | /api/v1/orderservice/order/admin | PUT | modified order data |
  | ts-order-other-service | /api/v1/orderOtherService/orderOther/admin | PUT | modified order data |
  | ts-order-service | /api/v1/orderservice/order/admin | POST | new order data |
  | ts-order-other-service | /api/v1/orderOtherService/orderOther/admin | POST | new order data |

</details>

<details>
  <summary>ts-admin-route-service</summary>

  ### Functionality
  /api/v1/adminrouteservice/**: provide APIs to manage route.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-admin-route-service)

  ### Interactions
  | Service | URI | Method | Data Sent |
  | --- | --- | --- | --- |
  | ts-route-service | /api/v1/routeservice/routes | GET |  |
  | ts-route-service | /api/v1/routeservice/routes | POST | new route data |
  | ts-route-service | /api/v1/routeservice/routes/{routeId} | DELETE |  |
  | ts-station-service | /api/v1/stationservice/stations/idlist | POST | a list of station name |

</details>

<details>
  <summary>ts-admin-travel-service</summary>

  ### Functionality
  /api/v1/admintravelservice/**: provide CRUD APIs to manage travel.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-admin-travel-service)

  ### Interactions
  | Service | URI | Method | Data Sent |
  | --- | --- | --- | --- |
  | ts-travel-service | /api/v1/travelservice/admin_trip | GET |  |
  | ts-travel2-service | /api/v1/travel2service/admin_trip | GET | |
  | ts-travel-service | /api/v1/travelservice/trips | POST | new travel data |
  | ts-travel2-service | /api/v1/travel2service/trips | POST | new travel data |
  | ts-travel-service | /api/v1/travelservice/trips | PUT | modified travel data |
  | ts-travel2-service | /api/v1/travel2service/trips | PUT | modified travel data |
  | ts-travel-service | /api/v1/travelservice/trips/{tripId} | DELETE |  |
  | ts-travel2-service | /api/v1/travel2service/trips/{tripId} | DELETE |  |
  | ts-station-service | /api/v1/stationservice/stations/idlist | POST | a list of station name |
  | ts-train-service | /api/v1/trainservice/trains/byName/{trainTypeName} | GET |  |
  | ts-route-service | /api/v1/routeservice/routes/{routeId} | GET |  |

</details>


<details>
  <summary>ts-admin-user-service</summary>

  ### Functionality
  /api/v1/adminuserservice/users/**: provide CRUD APIs to manage users.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-admin-user-service)

  ### Interactions
  | Service | URI | Method | Data Sent |
  | --- | --- | --- | --- |
  | ts-user-service | /api/v1/userservice/users | GET |  |
  | ts-user-service | /api/v1/userservice/users | PUT | modified user data |
  | ts-user-service | /api/v1/userservice/users | POST | new user data |
  | ts-user-service | /api/v1/userservice/users/{userId} | DELETE |  |

</details>


<details>
  <summary>ts-assurance-service</summary>

  ### Functionality
  /api/v1/assuranceservice/**: provide CRUD APIs to manage insurance.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-assurance-service)

  ### Interactions
  Directly interact with `mysql` service using Hibernate:

  Assurance
  ``` java
  private String id;
  private String orderId;
  private AssuranceType type;
  ```

  AssuranceType
  ``` java
  private int index;
  private String name;
  private double price;
  ```

</details>


<details>
  <summary>ts-auth-service</summary>

  ### Functionality
  /api/v1/auth/**: create default auth user;

  /api/v1/users/**: verify upload verification code and send back auth token; query all user; delete a user by id;

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-auth-service)

  ### Interactions
  | Service | URI | Method | Data Sent |
  | --- | --- | --- | --- |
  | ts-verification-code-service | /api/v1/verifycode/verify/{verifyCode} | GET |  |

  Also interact with `mysql` service using Hibernate:

  User
  ``` java
  private String userId;
  private String username;
  private String password;
  private Set<String> roles;
  ```

</details>


<details>
  <summary>ts-avatar-service</summary>

  ### Functionality
  /api/v1/avatar/**: detect uploaded image contains human face and return the detected face back to requestor;

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-avatar-service)

  ### Interactions
  N/A

</details>


<details>
  <summary>ts-basic-service</summary>

  ### Functionality
  /api/v1/basicservice/**: provide APIs to query basic travel information and basic station information.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-basic-service)

  ### Interactions
  | Service | URI | Method | Data Sent |
  | --- | --- | --- | --- |
  | ts-station-service | /api/v1/stationservice/stations/id/{stationName} | GET |  |
  | ts-station-service | /api/v1/stationservice/stations/idlist | GET | a list of station name |
  | ts-train-service | /api/v1/trainservice/trains/byName/{trainTypeName} | GET |  |
  | ts-train-service | /api/v1/trainservice/trains/byNames | GET | a list of train-type name |
  | ts-route-service | /api/v1/routeservice/routes/{routeId} | GET |  |
  | ts-route-service | /api/v1/routeservice/routes/byIds/ | GET | a list of route id |
  | ts-price-service | /api/v1/priceservice/prices/{routeId}/{trainType} | GET |  |
  | ts-price-service | /api/v1/priceservice/prices/byRouteIdsAndTrainTypes | GET | a list of route id and train type |

</details>


<details>
  <summary>ts-cancel-service</summary>

  ### Functionality
  /api/v1/cancelservice/**: provide APIs to calculate refund and cancel ticket.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-cancel-service)

  ### Interactions
  | Service | URI | Method | Data Sent |
  | --- | --- | --- | --- |
  | ts-notification-service | /api/v1/notifyservice/notification/order_cancel_success | POST | notify-info data |
  | ts-order-service | /api/v1/orderservice/order | PUT | order data |
  | ts-order-other-service | /api/v1/orderOtherService/orderOther | PUT | order data |
  | ts-inside-payment-service | /api/v1/inside_pay_service/inside_payment/drawback/{userId}/{money} | GET |  |
  | ts-user-service | /api/v1/userservice/users/id/{orderId} | GET |  |
  | ts-order-service | /api/v1/orderservice/order/{orderId} | GET |  |
  | ts-order-other-service | /api/v1/orderOtherService/orderOther/{orderId} | GET |  |

</details>


<details>
  <summary>ts-config-service</summary>

  ### Functionality
  /api/v1/configservice/**: provide CRUD APIs to manage configuration.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-config-service)

  ### Interactions
  Directly interact with `mysql` service using Hibernate:

  Config
  ``` java
  private String name;
  private String value;
  private String description;
  ```

</details>


<details>
  <summary>ts-consign-price-service</summary>

  ### Functionality
  /api/v1/consignpriceservice/**: provide APIs to manage baggage fee policy and calculate baggage fee.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-consign-price-service)

  ### Interactions
  Directly interact with `mysql` service using Hibernate:

  ConsignPrice
  ``` java
  private String id;
  private int index;
  private double initialWeight;
  private double initialPrice;
  private double withinPrice;
  private double beyondPrice;
  ```

</details>


<details>
  <summary>ts-consign-service</summary>

  ### Functionality
  /api/v1/consignservice/**: provide APIs to manage baggage order.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-consign-service)

  ### Interactions
  | Service | URI  |  Http Method | Data Sent |
  | --- | --- | --- | --- |
  | ts-consign-price-service | /api/v1/consignpriceservice/consignprice/{weight}/{isWithinRegion} | GET |  |

  Also interact with `mysql` service using Hibernate:

  ConsignRecord
  ``` java
  private String id;
  private String orderId;
  private String accountId;
  private String handleDate;
  private String targetDate;
  private String from;
  private String to;
  private String consignee;
  private String phone;
  private double weight;
  private double price;
  ```

</details>


<details>
  <summary>ts-contacts-service</summary>

  ### Functionality
  /api/v1/contactservice/**: provide CRUD APIs to manage contacts.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-contacts-service)

  ### Interactions
  Directly interact with `mysql` service using Hibernate:

  Contact
  ``` java
  private String id;
  private String accountId;
  private String name;
  private int documentType;
  private String documentNumber;
  private String phoneNumber;
  ```

</details>


<details>
  <summary>ts-delivery-service</summary>

  ### Functionality
  This service consumes delivery event and stores them into database

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-delivery-service)

  ### Interactions
  A consumer of `rabbitmq` service, subscribe to `food_delivery`.

  Also interact with `mysql` service using Hibernate:

  Delivery
  ``` java
  private String id;
  private UUID orderId;
  private String foodName;
  private String storeName;
  private String stationName;
  ```

</details>


<details>
  <summary>ts-execute-service</summary>

  ### Functionality
  /api/v1/executeservice/execute/**: provide APIs to check if a ticket is paid and to mark a ticket is used.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-execute-service)

  ### Interactions
  | Service | URI | Method | Data Sent |
  | --- | --- | --- | --- |
  | ts-order-service | /api/v1/orderservice/order/status/{orderId}/{status} | GET | |
  | ts-order-other-service | /api/v1/orderOtherService/orderOther/status/{orderId}/{status} | GET |  |
  | ts-order-service | /api/v1/orderservice/order/{orderId} | GET |  |
  | ts-order-other-service | /api/v1/orderOtherService/orderOther/{orderId} | GET | |

</details>


<details>
  <summary>ts-food-delivery-service</summary>

  ### Functionality
  /api/v1/fooddeliveryservice/orders/**: provide CRUD APIs to manage food delivery orders.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-food-delivery-service)

  ### Interactions
  | Service | URI | Method | Data Sent |
  | --- | --- | --- | --- |
  | ts-station-food-service | /api/v1/stationfoodservice/stationfoodstores/bystoreid/{stationFoodStoreId} | GET | |

  Also interact with `mysql` service using Hibernate:

  FoodDeliveryOrder
  ``` java
  private String id;
  private String stationFoodStoreId;
  private List<Food> foodList;
  private String tripId;
  private int seatNo;
  private String createdTime;
  private String deliveryTime;
  private double deliveryFee;
  ```

</details>


<details>
  <summary>ts-food-service</summary>

  ### Functionality
  /api/v1/foodservice/**: provide CRUD APIs to manage food orders and get all food offers in a specific trip.

  When a new order is created, a event will be produced.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-food-service)

  ### Interactions
  | Service | URI | Method | Data Sent |
  | --- | --- | --- | --- |
  | ts-train-food-service | /api/v1/trainfoodservice/trainfoods/{tripId} | GET |  |
  | ts-travel-service | /api/v1/travelservice/routes/{tripId} | GET |  |
  | ts-station-food-service | /api/v1/stationfoodservice/stationfoodstores | POST | a list of station name |

  A producer of `rabbitmq` service, publish to `food_delivery`.

  Also interact with `mysql` service using Hibernate:

  FoodOrder
  ``` java
  private String id;
  private String orderId;
  private int foodType;
  private String stationName;
  private String storeName;
  private String foodName;
  private double price;
  ```

</details>


<details>
  <summary>ts-gateway-service</summary>

  ### Functionality
  This service is the api gateway of the system, it routes the traffic to the specific service based on its uri and also provides load balancing and flow control.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-gateway-service)

  the route setting is in [here](https://github.com/FudanSELab/train-ticket/blob/master/ts-gateway-service/src/main/resources/application.yml)

  ### Interactions
  This service will interact with pretty much all the business logic related service

</details>


<details>
  <summary>ts-inside-payment-service</summary>

  ### Functionality
  /api/v1/inside_pay_service/inside_payment/**: provide APIs to manage payments.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-inside-payment-service)

  ### Interactions
  | Service | URI | Method | Data Sent |
  | --- | --- | --- | --- |
  | ts-order-service | /api/v1/orderservice/order/{orderId} | GET |  |
  | ts-order-other-service | /api/v1/orderOtherService/orderOther/{orderId} | GET |  |
  | ts-order-service | /api/v1/orderService/order/status/{orderId}/{orderStatus} | GET |  |
  | ts-order-other-service | /api/v1/orderOtherService/orderOther/status/{orderId}/{orderStatus} | GET |  |
  | ts-payment-service | /api/v1/paymentservice/payment | POST | third-party payment data |

  Also interact with `mysql` service using Hibernate:

  Money
  ``` java
  private String id;
  private String userId;
  private String money;
  private MoneyType type;
  ```

  Payment
  ``` java
  private String id;
  private String orderId;
  private String userId;
  private String price;
  private PaymentType type;
  ```
</details>


<details>
  <summary>ts-news-service</summary>

  ### Functionality
  This service is only used for testing purpose.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-news-service)

  ### Interactions
  N/A

</details>


<details>
  <summary>ts-notification-service</summary>

  ### Functionality
  /api/v1/notifyservice/notification/**: provide APIs to send email when successful buying insurance, successful creating order, successful updating order and successful canceling order.

  This service will also consume email event, and send email to user.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-notification-service)

  ### Interactions
  A producer of `rabbitmq` service, publish to `email`. However, base on the source code, seems like that this is only used for test purpose. [usage](https://github.com/FudanSELab/train-ticket/blob/master/ts-notification-service/src/main/java/notification/controller/NotificationController.java#L35)

  A consumer of `rabbitmq` service, subscribe to `email`.

  Also interact with `mysql` service using Hibernate:

  NotifyInfo
  ``` java
  private String id;
  private Boolean sendStatus;
  private String email;
  private String orderNumber;
  private String username;
  private String startPlace;
  private String endPlace;
  private String startTime;
  private String date;
  private String seatClass;
  private String seatNumber;
  private String price;
  ```

</details>


<details>
  <summary>ts-order-other-service</summary>

  ### Functionality
  /api/v1/orderOtherService/orderOther/**: provide APIs to manage ticket order, whose train number does not start with G or D.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-order-other-service)

  ### Interactions
  | Service | URI | Method | Data Sent |
  | --- | --- | --- | --- |
  | ts-station-service | /api/v1/stationservice/stations/namelist | POST | a list of station id |

  Also interact with `mysql` service using Hibernate:

  Order
  ``` java
  private String id;
  private String boughtDate;
  private String travelDate;
  private String travelTime;
  private String accountId;
  private String contactsName;
  private int documentType;
  private String contactsDocumentNumber;
  private String trainNumber;
  private int coachNumber;
  private int seatClass;
  private String seatNumber;
  private String from;
  private String to;
  private int status;
  private String price;
  ```

</details>


<details>
  <summary>ts-order-service</summary>

  ### Functionality
  /api/v1/orderservice/order/**: provide APIs to manage ticket order, whose train number starts with G or D.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-order-service)

  ### Interactions
  | Service | URI | Method | Data Sent |
  | --- | --- | --- | --- |
  | ts-station-service | /api/v1/stationservice/stations/namelist | POST | a list of station id |

  Also interact with `mysql` service using Hibernate:

  Order
  ``` java
  private String id;
  private String boughtDate;
  private String travelDate;
  private String travelTime;
  private String accountId;
  private String contactsName;
  private int documentType;
  private String contactsDocumentNumber;
  private String trainNumber;
  private int coachNumber;
  private int seatClass;
  private String seatNumber;
  private String from;
  private String to;
  private int status;
  private String price;
  ```

</details>


<details>
  <summary>ts-payment-service</summary>

  ### Functionality
  /api/v1/paymentservice/payment/**: provide APIs to create payment and query payment.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-payment-service)

  ### Interactions
  Directly interact with `mysql` service using Hibernate:

  Money
  ``` java
  private String id;
  private String userId;
  private String money;
  ```

  Payment
  ``` java
  private String id;
  private String orderId;
  private String userId;
  private String price;
  ```

</details>


<details>
  <summary>ts-preserve-other-service</summary>

  ### Functionality
  /api/v1/preserveotherservice/preserveOther: provide APIs to reserve a ticket, whose train number does not start with G or D.

  When a reservation is confirmed, a event will be produced. However, in the source code, the event producing code is commented. [usage](https://github.com/FudanSELab/train-ticket/blob/master/ts-preserve-other-service/src/main/java/preserveOther/service/PreserveOtherServiceImpl.java#L260)

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/blob/master/ts-preserve-other-service)

  ### Interactions
  | Service                 | URI                                                          | Method | Data Sent                  |
  | ----------------------- | ------------------------------------------------------------ | ------ | ---------------------------- |
  | ts-basic-service   | /api/v1/ticketinfoservice/  | POST | travel data |
  | ts-seat-service  | /api/v1/seatservice/seats    | POST   | seat data |
  | ts-user-service         | /api/v1/userservice/users/id/{accountId}                    | GET    |    |
  | ts-assurance-service    | /api/v1/assuranceservice/assurances/{assuranceType}/{orderId} | GET    |   |
  | ts-station-service      | /api/v1/stationservice/stations/id/{stationName}            | GET    |  |
  | ts-security-service     | /api/v1/securityservice/securityConfigs/{accountId}         | GET    |  |
  | ts-travel2-service      | /api/v1/travel2service/trip_detail                           | POST   | trip-all-detail-info data  |
  | ts-contacts-service     | /api/v1/contactservice/contacts/{contactsId}                  | GET    |      |
  | ts-order-other-service  | /api/v1/orderOtherService/orderOther                         | POST   | order data    |
  | ts-food-service         | /api/v1/foodservice/orders    | POST   | food-order data      |
  | ts-consign-service      | /api/v1/consignservice/consigns      | POST   | consign data      |

  A producer of `rabbitmq` service, publish to `email`.

</details>


<details>
  <summary>ts-preserve-service</summary>

  ### Functionality
  /api/v1/preserveservice/preserve: provide APIs to reserve a ticket, whose train number starts with G or D.

  When a reservation is confirmed, a event will be produced. However, in the source code, the event producing code is commented. [usage](https://github.com/FudanSELab/train-ticket/blob/master/ts-preserve-service/src/main/java/preserve/service/PreserveServiceImpl.java#L261)

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/blob/master/ts-preserve-service)

  ### Interactions
  | Service                 | URI                                                          | Method | Data Sent                  |
  | ----------------------- | ------------------------------------------------------------ | ------ | ---------------------------- |
  | ts-basic-service   | /api/v1/basicservice/basic/travel | POST   | travel data   |
  | ts-seat-service         | /api/v1/seatservice/seats      | POST   | seat data |
  | ts-user-service         | /api/v1/userservice/users/id/{accountId}                    | GET    |    |
  | ts-assurance-service    | /api/v1/assuranceservice/assurances/{assuranceType}/{orderId} | GET    |   |
  | ts-station-service      | /api/v1/stationservice/stations/id/{stationName}            | GET    |  |
  | ts-security-service     | /api/v1/securityservice/securityConfigs/{accountId}         | GET    |  |
  | ts-travel-service       | /api/v1/travelservice/trip_detail       | POST   |   trip-all-detail-info data   |
  | ts-contacts-service     | /api/v1/contactservice/contacts/{contactsId}                  | GET    |      |
  | ts-order-service        | /api/v1/orderService/order    | POST   | order data |
  | ts-food-service         | /api/v1/foodservice/orders                                   | POST   | food-order data    |
  | ts-consign-service      | /api/v1/consignservice/consigns                              | POST   | consign data  |

  A producer of `rabbitmq` service, publish to `email`.

</details>


<details>
  <summary>ts-price-service</summary>

  ### Functionality
  /api/v1/priceservice/prices/**: provide APIs to calculate ticket's price and manage price configuration.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-price-service)

  ### Interactions
  Directly interact with `mysql` service using Hibernate:

  PriceConfig
  ``` java
  private String id;
  private String trainType;
  private String routeId;
  private double basicPriceRate;
  private double firstClassPriceRate;
  ```

</details>


<details>
  <summary>ts-rebook-service</summary>

  ### Functionality
  /api/v1/rebookservice/rebook/**: provides APIs to manage ticket changing/rebooking, as well as, calculating price difference.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-rebook-service)

  ### Interactions
  | Service                   | URI                                                  | Method | Data Sent                                 |
  | ------------------------- | ---------------------------------------------------- | ------ | ------------------------------------------- |
  | ts-seat-service           | /api/v1/seatservice/seats                            | POST   | seat data          |
  | ts-travel-service         | /api/v1/travelservice/trip_detail                    | POST   | trip-all-detail-info data    |
  | ts-travel2-service        | /api/v1/travel2service/trip_detail                   | POST   | trip-all-detail-info data   |
  | ts-order-service          | /api/v1/orderService/order{orderId}                | GET   |    |
  | ts-order-other-service    | /api/v1/orderOtherService/orderOther/{orderId}                 | GET | |
  | ts-order-service          | /api/v1/orderService/order                           | POST   | new order data       |
  | ts-order-other-service    | /api/v1/orderOtherService/orderOther                 | POST   | new order data   |
  | ts-order-service          | /api/v1/orderService/order                           | PUT   | modified order data       |
  | ts-order-other-service    | /api/v1/orderOtherService/orderOther                 | PUT | modified order data   |
  | ts-order-service          | /api/v1/orderService/order{orderId}                | DELETE   |    |
  | ts-order-other-service    | /api/v1/orderOtherService/orderOther/{orderId}                 | DELETE | |
  | ts-train-service          | /api/v1/trainservice/trains/byName/ {trainTypeName}  | GET    | |
  | ts-route-service          | /api/v1/routeservice/routes/{routeId}               | GET    | |
  | ts-inside-payment-service | /api/v1/inside_pay_service/inside_payment/drawback/{userId}/{money} | GET  |  |
  | ts-inside-payment-service | /api/v1/inside_pay_service/inside_payment/difference | POST   | payment-difference-info data |

</details>


<details>
  <summary>ts-route-plan-service</summary>

  ### Functionality
  /api/v1/routeplanservice/routePlan/**: provide APIs to get cheapest route, quickest route and route with minimum stop.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-route-plan-service)

  ### Interactions
  | Service            | URI                                             | Method | Data Sent               |
  | ------------------ | ----------------------------------------------- | ------ | ------------------------- |
  | ts-route-service   | /api/v1/routeservice/routes/{routeId}          | GET    |  |
  | ts-travel-service  | /api/v1/travelservice/trips/routes              | POST    |  a list of route id      |
  | ts-travel2-service  | /api/v1/travel2service/trips/routes              | POST    | a list of route id      |
  | ts-travel-service  | /api/v1/travelservice/trips/left                | POST   | trip-info data      |
  | ts-travel2-service | /api/v1/travel2service/trips/left               | POST   | trip-info data      |
  | ts-travel-service  | /api/v1/travelservice/routes/{tripId}          | GET    |     |
  | ts-travel2-service | /api/v1/travel2service/routes/{tripId}             | GET   |  |

</details>


<details>
  <summary>ts-route-service</summary>

  ### Functionality
  /api/v1/routeservice/routes/**: provide CRUD APIs to manage route information.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-route-service)

  ### Interactions
  Directly interact with `mysql` service using Hibernate:

  Route
  ``` java
  private String id;
  private List<String> stations;
  private List<Integer> distances;
  private String startStation;
  private String endStation;
  ```

</details>


<details>
  <summary>ts-seat-service</summary>

  ### Functionality
  /api/v1/seatservice/seats/**: provide APIs to reserve seats and query remaining seats within a route.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-seat-service)

  ### Interactions
  | Service                | URI                                              | Method | Data Sent                     |
  | ---------------------- | ------------------------------------------------ | ------ | ------------------------------- |
  | ts-order-service       | /api/v1/orderservice/order/tickets               | POST   | seat data           |
  | ts-order-other-service | /api/v1/orderOtherService/orderOther/tickets     | POST   | seat data             |
  | ts-config-service      | /api/v1/configservice/configs/{configName}        | GET    |      |

</details>


<details>
  <summary>ts-security-service</summary>

  ### Functionality
  /api/v1/securityservice/securityConfigs/**: provide CRUD APIs to manage security configuration and verify account id.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-security-service)

  ### Interactions
  | Service                | URI                                                          | Method | Data Sent                   |
  | ---------------------- | ------------------------------------------------------------ | ------ | ----------------------------- |
  | ts-order-service       | /api/v1/orderservice/order/security/{checkDate}/{accountId} | GET    |  |
  | ts-order-other-service | /api/v1/orderOtherService/orderOther/security/{checkDate}/{accountId} | GET    | |

  Also interact with `mysql` service using Hibernate:

  SecurityConfig
  ``` java
  private String id;
  private String name;
  private String value;
  private String description;
  ```

</details>


<details>
  <summary>ts-station-food-service</summary>

  ### Functionality
  /api/v1/stationfoodservice/stationfoodstores/**: provide CRUD APIs to manage station food information.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-station-food-service)

  ### Interactions
  Directly interact with `mysql` service using Hibernate:

  StationFoodStore
  ``` java
  private String id;
  private String stationName;
  private String storeName;
  private String telephone;
  private String businessTime;
  private double deliveryFee;
  private List<Food> foodList;
  ```

</details>


<details>
  <summary>ts-station-service</summary>

  ### Functionality
  /api/v1/stationservice/stations/**: provide CRUD APIs to manage station information, to query station name by id, and to query station id by name.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-station-service)

  ### Interactions
  Directly interact with `mysql` service using Hibernate:

  Station
  ``` java
  private String id;
  private String name;
  private int stayTime;
  ```
</details>


<details>
  <summary>ts-ticket-office-service</summary>

  ### Functionality
  This standalone service provides CRUD APIs to manage ticket office information.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-ticket-office-service)

  ### Interactions
  Directly interact with `mysql` service with Javascript mysql driver

</details>


<details>
  <summary>ts-train-food-service</summary>

  ### Functionality
  /api/v1/trainfoodservice/trainfoods/**: provide CRUD APIs to manage train food information.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-train-food-service)

  ### Interactions
  Directly interact with `mysql` service using Hibernate:

  TrainFood
  ``` java
  private String id;
  private String tripId;
  private List<Food> foodList;
  ```

</details>


<details>
  <summary>ts-train-service</summary>

  ### Functionality
  /api/v1/trainservice/trains/**: provide CRUD APIs to manage train information.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-train-service)

  ### Interactions
  Directly interact with `mysql` service using Hibernate:

  TrainType
  ``` java
  private String id;
  private String name;
  private int economyClass;
  private int confortClass;
  private int averageSpeed;
  ```

</details>


<details>
  <summary>ts-travel-plan-service</summary>

  ### Functionality
  /api/v1/travelplanservice/travelPlan/**: provide APIs to query travel plan, includes cheapest travel plan, quickest travel plan, and travel plan with minimum stop.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-travel-plan-service)

  ### Interactions
  | Service | URI | Method | Data Sent |
  | --- | --- | --- | --- |
  | ts-seat-service | /api/v1/seatservice/seats/left_tickets | POST | seat data |
  | ts-route-plan-service | /api/v1/routeplanservice/routePlan/cheapestRoute | POST | route-plan-info data |
  | ts-route-plan-service | /api/v1/routeplanservice/routePlan/quickestRoute | POST | route-plan-info data |
  | ts-route-plan-service | /api/v1/routeplanservice/routePlan/minStopStations | POST | route-plan-info data |
  | ts-travel-service | /api/v1/travelservice/trips/left | POST | trip-info data |
  | ts-travel2-service | /api/v1/travel2service/trips/left | POST | trip-info data |
  | ts-train-service | /api/v1/trainservice/trains/byName/{trainTypeName} | GET | |

</details>


<details>
  <summary>ts-travel-service</summary>

  ### Functionality
  /api/v1/travelservice/**: provide APIs to manage high speed train's trip.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-travel-service)

  ### Interactions
  | Service | URI | Method | Data Sent |
  | --- | --- | --- | --- |
  | ts-basic-service | /api/v1/basicservice/basic/travel | POST |  travel data |
  | ts-basic-service | /api/v1/basicservice/basic/travels | POST | a list of travel data |
  | ts-train-service | /api/v1/trainservice/trains/byName/{trainTypeName} | GET |  |
  | ts-route-service | /api/v1/routeservice/routes/{routeId} | GET | |
  | ts-seat-service | /api/v1/seatservice/seats/left_tickets | POST | seat data |

  Also interact with `mysql` service using Hibernate:

  Trip
  ``` java
  private String id;
  private TripId tripId;
  private String trainTypeName;
  private String routeId;
  private String startStationName;
  private String stationsName;
  private String terminalStationName;
  private String startTime;
  private String endTime;
  ```

</details>


<details>
  <summary>ts-travel2-service</summary>

  ### Functionality
  /api/v1/travel2service/**: provide APIs to manage normal train's trip.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-travel2-service)

  ### Interactions
  | Service | URI | Method | Data Sent |
  | --- | --- | --- | --- |
  | ts-basic-service | /api/v1/basicservice/basic/travel | POST |  travel data |
  | ts-basic-service | /api/v1/basicservice/basic/travels | POST | a list of travel data |
  | ts-train-service | /api/v1/trainservice/trains/byName/{trainTypeName} | GET |  |
  | ts-route-service | /api/v1/routeservice/routes/{routeId} | GET | |
  | ts-seat-service | /api/v1/seatservice/seats/left_tickets | POST | seat data |

  Also interact with `mysql` service using Hibernate:

  Trip
  ``` java
  private String id;
  private TripId tripId;
  private String trainTypeName;
  private String routeId;
  private String startStationName;
  private String stationsName;
  private String terminalStationName;
  private String startTime;
  private String endTime;
  ```

</details>


<details>
  <summary>ts-ui-dashboard</summary>

  ### Functionality
  This service provides all the UI interface to interact with the system.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-ui-dashboard)

  ### Interactions


</details>


<details>
  <summary>ts-user-service</summary>

  ### Functionality
  /api/v1/userservice/users/**: provide APIs to manage user information.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-user-service)

  ### Interactions
  | Service | URI | Method | Data Sent |
  | --- | --- | --- | --- |
  | ts-auth-service | /api/v1/auth | POST | auth data |
  | ts-auth-service | /api/v1/users/{userId} | DELETE |  |

  Also interact with `mysql` service using Hibernate:

  User
  ``` java
  private String userId;
  private String userName;
  private String password;
  private int gender;
  private int documentType;
  private String documentNum;
  private String email;
  ```

</details>


<details>
  <summary>ts-verification-code-service</summary>

  ### Functionality
  /api/v1/verifycode/**: generate verification code image; verify verification code;

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-verification-code-service)

  ### Interactions
  N/A

</details>


<details>
  <summary>ts-voucher-service</summary>

  ### Functionality
  /getVoucher: generate the reimbursement voucher based on the order id.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-voucher-service)

  ### Interactions
  | Service | URI | Method | Data Sent |
  | --- | --- | --- | --- |
  | ts-order-other-service | /api/v1/orderOtherService/orderOther/{orderId} | GET |  |
  | ts-order-service | /api/v1/orderservice/order/{orderId} | GET |  |

  Also interact with `mysql` service with pymysql library

  Voucher
  ``` sql
  voucher_id INT NOT NULL AUTO_INCREMENT,
  order_id VARCHAR(1024) NOT NULL,
  travelDate VARCHAR(1024) NOT NULL,
  travelTime VARCHAR(1024) NOT NULL,
  contactName VARCHAR(1024) NOT NULL,
  trainNumber VARCHAR(1024) NOT NULL,
  seatClass INT NOT NULL,
  seatNumber VARCHAR(1024) NOT NULL,
  startStation VARCHAR(1024) NOT NULL,
  destStation VARCHAR(1024) NOT NULL,
  price FLOAT NOT NULL,
  ```
</details>


<details>
  <summary>ts-wait-order-service</summary>

  ### Functionality
  /api/v1/waitorderservice/**: provide APIs to manage waitlist.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/ts-wait-order-service)

  ### Interactions
  Directly interact with `mysql` service using Hibernate:

  WaitListOrder
  ``` java
  private String id;
  private String travelTime;
  private String accountId;
  private String contactsId;
  private String contactsName;
  private int contactsDocumentType;
  private String contactsDocumentNumber;
  private String trainNumber;
  private int seatType;
  private String from;
  private String to;
  private String price;
  private String waitUtilTime;
  private String createdTime;
  private int status;
  ```

</details>


<details>
  <summary>rabbitmq</summary>

  ### Functionality
  This message queue provides async communication between services.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/deployment/kubernetes-manifests/quickstart-k8s/charts/rabbitmq)

  ### Interactions
  There are a few service needed message queue for communication.

</details>


<details>
  <summary>mysql</summary>

  ### Functionality
  This service provides persistent storage for other services.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/deployment/kubernetes-manifests/quickstart-k8s/charts/mysql)

  ### Interactions
  Pretty much all bussiness logic related services will interact with this service to store data.

</details>


<details>
  <summary>nacos</summary>

  ### Functionality
  This service provide service discovery.

  ### Related Files
  All code is in this [folder](https://github.com/FudanSELab/train-ticket/tree/master/deployment/kubernetes-manifests/quickstart-k8s/charts/nacos)

  ### Interactions
  Pretty much all bussiness logic related services needed to register themselve with this service.

</details>


## Task 3
This report provide a Python script to generate a html file for interactive visualization.

Note:
- the result does not include `ts-gateway-servie` service, because almost all business logic related service will interact with it, including it will make the graph too crowded.
- the data passed between bussiness logic related service and `mysql` service is not displayed in the graph, because the actual data passed between them is an implementation detail of the ORM library.
- the data passed between bussiness logic related service and `rabbitmq` service is not displayed in the graph, because the actual data passed between them is an implementation detail of the rabbitmq library.

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
