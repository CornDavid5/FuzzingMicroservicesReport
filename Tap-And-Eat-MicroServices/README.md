# Overview
This project consists of following services:
- ConfigService
- DiscoverySErvice
- AccountService
- PriceService
- CustomerService
- StoreService
- ItemService
- FoodTrayService

Services interact with each other using `springframework.cloud.netflix.feign` library, which implements REST API for us.

# How to Deploy
The provided docker compose file contains base images that had already been removed from docker hub. Cannot deploy this project.

# How to Run Test
There is no real test in this project, all service just includes empty test, which will load the service but do nothing, for example:

``` java
@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemServiceApplicationTests  {

	@Test
	public void contextLoads() {
	}

}
```

# Note
- the docker image and compose file used in this project is too old, some base images and property had been deprecated and removed from the docker hub. For example, `java:8` should be changed to `openjdk:8-jre`; `links` should be changed to `depends_on`
- docker setting in pom file is not complete, for example, `docker.image.prefix` is not defined in `<imageName>${docker.image.prefix}/${project.artifactId}</imageName>`