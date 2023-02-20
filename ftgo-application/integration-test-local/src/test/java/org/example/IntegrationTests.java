package org.example;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;
import org.junit.Test;
import org.junit.BeforeClass;

import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IntegrationTests {

    final static String customerUrl = "http://localhost:8081";
    final static String orderUrl = "http://localhost:8082";
    final static String kitchenUrl = "http://localhost:8083";
    final static String restaurantUrl = "http://localhost:8084";
    final static String orderHistoryUrl = "http://localhost:8086";
    final static String deliveryUrl = "http://localhost:8089";

    final static String menuItemName = "Chicken";
    final static String menuItemId = "1";
    final static double menuItemPrice = 1.1;
    final static int revisedQuality = 10;

    static String customerId = null;
    static String restaurantId = null;

    static final double DELTA = 1e-15;

    //CookieStore httpCookieStore = new BasicCookieStore();
    //HttpClient httpClient = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore).build();

    private static HttpResponse postHttpHelper(JSONObject data, String url) throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        StringEntity params = new StringEntity(data.toString());
        request.addHeader("content-type", "application/json");
        request.setEntity(params);
        HttpResponse response = httpClient.execute(request);
        return response;
    }

    private static HttpResponse getHttpHelper(String url) throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = httpClient.execute(request);
        return response;
    }

    private static JSONObject parseResponse(HttpResponse response) throws Exception {
        String respString = EntityUtils.toString(response.getEntity());
        JSONObject respJson = new JSONObject(respString);
        return respJson;
    }

    private int createAnOrder() throws Exception {
        JSONObject address = new JSONObject();
        address.put("city", "Urbana");
        address.put("state", "IL");
        address.put("street1", "dom");
        address.put("street2", "");
        address.put("zip", "61000");

        JSONObject lineItem = new JSONObject();
        lineItem.put("menuItemId", menuItemId);
        lineItem.put("quantity", 1);

        JSONArray lineItems = new JSONArray();
        lineItems.put(lineItem);

        JSONObject order = new JSONObject();
        order.put("consumerId", customerId);
        order.put("deliveryAddress", address);
        order.put("deliveryTime", Instant.now().toString());
        order.put("lineItems", lineItems);
        order.put("restaurantId", restaurantId);

        Thread.sleep(1000);

        HttpResponse response = postHttpHelper(order, orderUrl + "/orders");
        Integer responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(200), responseCode);

        JSONObject respJson = parseResponse(response);
        int orderId = Integer.valueOf(respJson.get("orderId").toString());
        return orderId;
    }

    @BeforeClass
    public static void init() throws Exception {
        // create a customer
        JSONObject customer = new JSONObject();
        customer.put("firstName", "John");
        customer.put("lastName", "Wick");

        HttpResponse response = postHttpHelper(customer, customerUrl + "/consumers");
        Integer responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(200), responseCode);

        JSONObject respJson = parseResponse(response);
        customerId = respJson.get("consumerId").toString();

        // create a restaurant
        JSONObject address = new JSONObject();
        address.put("city", "Urbana");
        address.put("state", "IL");
        address.put("street1", "local market");
        address.put("street2", "");
        address.put("zip", "61000");

        JSONObject menuItem = new JSONObject();
        menuItem.put("id", menuItemId);
        menuItem.put("name", menuItemName);
        menuItem.put("price", String.valueOf(menuItemPrice));

        JSONArray menuItems = new JSONArray();
        menuItems.put(menuItem);

        JSONObject menu = new JSONObject();
        menu.put("menuItems", menuItems);

        JSONObject restaurant = new JSONObject();
        restaurant.put("address", address);
        restaurant.put("menu", menu);
        restaurant.put("name", "ChipSpace");

        response = postHttpHelper(restaurant, restaurantUrl + "/restaurants");
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(200), responseCode);

        respJson = parseResponse(response);
        restaurantId = respJson.get("id").toString();
    }

    @Test
    public void shouldCreateOrder() throws Exception {
        int orderId = createAnOrder();
        assertTrue(orderId > 0);

        Thread.sleep(1000);

        // check status
        HttpResponse response = getHttpHelper(orderUrl + "/orders/" + orderId);
        Integer responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(200), responseCode);

        JSONObject respJson = parseResponse(response);
        assertEquals("APPROVED", respJson.get("state").toString());
        double price = Double.valueOf(respJson.get("orderTotal").toString());
        assertTrue(Math.abs(price - menuItemPrice) <= DELTA);
    }

    @Test
    public void shouldReviseOrder() throws Exception {
        int orderId = createAnOrder();
        assertTrue(orderId > 0);

        Thread.sleep(1000);

        // revise the order
        JSONObject revisedOrderLineItem = new JSONObject();
        revisedOrderLineItem.put("menuItemId", menuItemId);
        revisedOrderLineItem.put("quantity", revisedQuality);

        JSONArray revisedOrderLineItems = new JSONArray();
        revisedOrderLineItems.put(revisedOrderLineItem);

        JSONObject revisedOrder = new JSONObject();
        revisedOrder.put("revisedOrderLineItems", revisedOrderLineItems);

        Thread.sleep(1000);

        HttpResponse response = postHttpHelper(revisedOrder, orderUrl + "/orders/" + orderId + "/revise");
        Integer responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(200), responseCode);

        Thread.sleep(2000);

        // check status
        response = getHttpHelper(orderUrl + "/orders/" + orderId);
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(200), responseCode);

        JSONObject respJson = parseResponse(response);
        assertEquals("APPROVED", respJson.get("state").toString());
        double price = Double.valueOf(respJson.get("orderTotal").toString());
        assertTrue(Math.abs(price - menuItemPrice * revisedQuality) <= DELTA);
    }

    @Test
    public void shouldCancelOrder() throws Exception {
        int orderId = createAnOrder();
        assertTrue(orderId > 0);

        Thread.sleep(1000);

        // cancel order
        JSONObject cancelledOrder = new JSONObject();

        HttpResponse response = postHttpHelper(cancelledOrder, orderUrl + "/orders/" + orderId + "/cancel");
        Integer responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(200), responseCode);

        Thread.sleep(2000);

        // check status
        response = getHttpHelper(orderUrl + "/orders/" + orderId);
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(200), responseCode);

        JSONObject respJson = parseResponse(response);
        assertEquals("CANCELLED", respJson.get("state").toString());
    }

    @Test
    public void shouldShowOrderHistory() throws Exception {
        int orderId = createAnOrder();
        assertTrue(orderId > 0);

        Thread.sleep(1000);

        HttpResponse response = getHttpHelper(orderHistoryUrl + "/orders?consumerId=" + customerId);
        Integer responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(200), responseCode);

        JSONObject respJson = parseResponse(response);

        boolean hasOrder = false;
        JSONArray orders = respJson.getJSONArray("orders");
        for (int i = 0; i < orders.length(); i++) {
            JSONObject order = orders.getJSONObject(i);
            int actualOrderId = Integer.valueOf(order.getString("orderId"));
            if (actualOrderId == orderId) {
                hasOrder = true;
                break;
            }
        }

        assertTrue(hasOrder);
    }

    @Test
    public void shouldAcceptTicket() throws Exception {
        int orderId = createAnOrder();
        assertTrue(orderId > 0);

        Thread.sleep(1000);

        JSONObject readyBy = new JSONObject();
        readyBy.put("readyBy", Instant.now().plusSeconds(3600));

        HttpResponse response = postHttpHelper(readyBy, kitchenUrl + "/tickets/" + orderId + "/accept");
        Integer responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(200), responseCode);
    }

    @Test
    public void shouldDeliverOrder() throws Exception {
        int orderId = createAnOrder();
        assertTrue(orderId > 0);

        Thread.sleep(1000);

        // notify driver is available
        JSONObject available = new JSONObject();
        available.put("available", true);

        HttpResponse response = postHttpHelper(available, deliveryUrl + "/couriers/" + 1 + "/availability");
        Integer responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(200), responseCode);

        Thread.sleep(1000);

        // accept ticket
        JSONObject readyBy = new JSONObject();
        readyBy.put("readyBy", Instant.now().plusSeconds(3600));

        response = postHttpHelper(readyBy, kitchenUrl + "/tickets/" + orderId + "/accept");
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(200), responseCode);

        Thread.sleep(1000);

        // check delivery action
        response = getHttpHelper(deliveryUrl + "/deliveries/" + orderId);
        System.out.print(deliveryUrl + "/deliveries/" + orderId);
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(200), responseCode);

        JSONObject respJson = parseResponse(response);
        JSONArray actions = respJson.getJSONArray("courierActions");
        //assertTrue(actions.length() > 0);
    }
}

