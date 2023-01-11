# How to Run
Prerequisite:
- docker
- kubernetes

Following steps is for `kubernetes quickstart` deployment:
- `make deploy`

You can find some screenshot for the running app in [here](./resources/app/)

# Code Coverage
Most of the services is written in Java, and it uses `mockito` as mocking framework and `junit` as testing framework. However, feels like the development team have not maintained the tests for a while, many tests will not pass without modifications. I modified the source code and the following modules can pass the tests:
- ts-admin-basic-info-service
- ts-admin-user-service
- ts-cancel-service
- ts-contacts-service
- ts-route-plan-service

The coverage file is in [here](./coverage/)

If you want to generate the coverage, using following steps:
- clone the instrumented repo, `git clone https://github.com/CornDavid5/train-ticket`
- generate coverage for one module, `mvn test -pl ts-route-plan-service -am`


## Modification Examples
In the original tests, there are parameters that are tightly tied to developers' own development environment.
``` java
@Test
public void testGetAllContacts() {
    Mockito.when(restTemplate.exchange(
            "http://ts-contacts-service:12347/api/v1/contactservice/contacts",
            HttpMethod.GET,
            requestEntity,
            Response.class)).thenReturn(re);
    response = adminBasicInfoService.getAllContacts(headers);
    Assert.assertEquals(new Response<>(null, null, null), response);
}
```

Should be changed to:

``` java
@Test
public void testGetAllContacts() {
    Mockito.when(restTemplate.exchange(
            Mockito.contains("/api/v1/contactservice/contacts"),
            Mockito.eq(HttpMethod.GET),
            Mockito.eq(requestEntity),
            Mockito.eq(Response.class))).thenReturn(re);
    response = adminBasicInfoService.getAllContacts(headers);
    Assert.assertEquals(new Response<>(null, null, null), response);
}
```

Or, the implementation of function is changed, but the test still expects the old implementation.

``` java
@Test
public void testCreateContacts1() {
    Contacts contacts = new Contacts();
    Mockito.when(contactsRepository.findById(Mockito.any(UUID.class).toString())).thenReturn(Optional.of(contacts));
    Response result = contactsServiceImpl.createContacts(contacts, headers);
    Assert.assertEquals(new Response<>(0, "Already Exists", contacts), result);
}
```

Should be changed to:

``` java
@Test
public void testCreateContacts1() {
    UUID id = UUID.randomUUID();
    Contacts contacts = new Contacts(id.toString(), id.toString(), "name", 1, "12", "10001");
    Mockito.when(contactsRepository.findByAccountIdAndDocumentTypeAndDocumentType(id.toString(), "12", 1)).thenReturn(contacts);
    Response result = contactsServiceImpl.createContacts(contacts, headers);
    Assert.assertEquals(new Response<>(0, "Already Exists", contacts), result);
}
```

Or, API URL changed, but test still uses old URL.

``` java
@Test
public void testDeleteStation() throws Exception {
    Station s = new Station();
    Mockito.when(adminBasicInfoService.deleteStation(Mockito.anyString(), Mockito.any(HttpHeaders.class))).thenReturn(response);
    String requestJson = JSONObject.toJSONString(s);
    String result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/adminbasicservice/adminbasic/stations").contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn().getResponse().getContentAsString();
    Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
}
```

Should be changed to:

``` java
@Test
public void testDeleteStation() throws Exception {
    Station s = new Station();
    Mockito.when(adminBasicInfoService.deleteStation(Mockito.anyString(), Mockito.any(HttpHeaders.class))).thenReturn(response);
    String requestJson = JSONObject.toJSONString(s);
    String result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/adminbasicservice/adminbasic/stations/id").contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn().getResponse().getContentAsString();
    Assert.assertEquals(response, JSONObject.parseObject(result, Response.class));
}
```
