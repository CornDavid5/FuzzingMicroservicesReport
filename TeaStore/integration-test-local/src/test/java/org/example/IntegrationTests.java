package org.example;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.*;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IntegrationTests {

    String baseUrl = "http://localhost:8080/tools.descartes.teastore.webui";
    CookieStore httpCookieStore = new BasicCookieStore();
    //HttpClient httpClient = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore).setRedirectStrategy(new LaxRedirectStrategy()).build();
    HttpClient httpClient = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore).build();

    public HttpResponse postHttpHelper(String url) throws Exception {
        HttpPost request = new HttpPost(url);
        HttpResponse response = httpClient.execute(request);
        return response;
    }

    public HttpResponse getHttpHelper(String url) throws Exception{
        HttpGet request = new HttpGet(url);
        HttpResponse response = httpClient.execute(request);
        return response;
    }

    public String parseResponse(HttpResponse response) throws Exception {
        return EntityUtils.toString(response.getEntity());
    }

    @Test
    public void shouldSignInAndDisplayProfile() throws Exception {
        String username = "user2";

        String url = baseUrl + "/loginAction" + "?" + "referer=http://localhost:8080/tools.descartes.teastore.webui/" + "&" + "username=" + username + "&" + "password=password" + "&" + "signin=Sign+in";
        HttpResponse response = postHttpHelper(url);
        Integer responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(302), responseCode);

        url = baseUrl + "/profile";
        response = getHttpHelper(url);
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(200), responseCode);
        String html = parseResponse(response);
        assertTrue(html.contains(username));
    }

    @Test
    public void shouldDisplayBlackTea() throws Exception {
        String teaName = "Earl Grey (loose)";
        String category = "2";

        String url = baseUrl + "/category" + "?" + "category=" + category + "&page=1";
        HttpResponse response = getHttpHelper(url);
        Integer responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(200), responseCode);
        String html = parseResponse(response);
        assertTrue(html.contains(teaName));
    }

    @Test
    public void shouldDisplayEarlGrey() throws Exception {
        String teaName = "Earl Grey (loose)";
        String price = "75.82";
        String id = "7";

        String url = baseUrl + "/product" + "?" + "id=" + id;
        HttpResponse response = getHttpHelper(url);
        Integer responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(200), responseCode);
        String html = parseResponse(response);
        assertTrue(html.contains(teaName));
        assertTrue(html.contains(price));
    }

    @Test
    public void shouldAddToCart() throws Exception {
        String teaName = "Earl Grey (loose)";
        String price = "75.82";
        String id = "7";

        String url = baseUrl + "/cartAction" + "?" + "productid=" + id + "&" + "addToCart=Add+to+Cart";
        HttpResponse response = postHttpHelper(url);
        Integer responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(302), responseCode);

        url = baseUrl + "/cart";
        response = getHttpHelper(url);
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(200), responseCode);
        String html = parseResponse(response);
        assertTrue(html.contains(teaName));
        assertTrue(html.contains(price));
    }

    @Test
    public void shouldUpdateCart() throws Exception {
        String teaName = "Earl Grey (loose)";
        double price = 75.82;
        int count = 2;
        String id = "7";

        String url = baseUrl + "/cartAction" + "?" + "productid=" + id + "&" + "addToCart=Add+to+Cart";
        HttpResponse response = postHttpHelper(url);
        Integer responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(302), responseCode);

        url = baseUrl + "/cartAction" + "?" + "productid=" + id + "&" + "orderitem_" + id + "=" + String.valueOf(count) + "&updateCartQuantities=Update+Cart";
        response = postHttpHelper(url);
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(302), responseCode);

        url = baseUrl + "/cart";
        response = getHttpHelper(url);
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(200), responseCode);
        String html = parseResponse(response);
        assertTrue(html.contains(teaName));
        assertTrue(html.contains(String.valueOf(count * price)));
    }

    @Test
    public void shouldEmptyCart() throws Exception {
        String id = "7";

        String url = baseUrl + "/cartAction" + "?" + "productid=" + id + "&" + "addToCart=Add+to+Cart";
        HttpResponse response = postHttpHelper(url);
        Integer responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(302), responseCode);

        url = baseUrl + "/cartAction" + "?" + "productid=" + id + "&" + "orderitem_" + id + "=1" + "&removeProduct_7=";
        response = postHttpHelper(url);
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(302), responseCode);

        url = baseUrl + "/cart";
        response = getHttpHelper(url);
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(200), responseCode);
        String html = parseResponse(response);
        assertTrue(html.contains("Your cart is empty."));
    }

    @Test
    public void shouldPlaceOrder() throws Exception {
        String id = "7";
        String firstname = "microservice";
        String lastname = "fuzzing";
        String address1 = "Odd";
        String address2 = "Even";
        String cardtype = "visa";
        String cardnumber = "314159265359";
        String expirydate = "12/2023";


        String url = baseUrl + "/loginAction" + "?" + "referer=http://localhost:8080/tools.descartes.teastore.webui/" + "&" + "username=user6" + "&" + "password=password" + "&" + "signin=Sign+in";
        HttpResponse response = postHttpHelper(url);
        Integer responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(302), responseCode);

        url = baseUrl + "/cartAction" + "?" + "productid=" + id + "&" + "addToCart=Add+to+Cart";
        response = postHttpHelper(url);
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(302), responseCode);

        url = baseUrl + "/cartAction" + "?" + "productid=" + id + "&" + "orderitem_" + id + "=1" + "&" + "proceedtoCheckout=Proceed+to+Checkout";
        response = postHttpHelper(url);
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(302), responseCode);

        url = baseUrl + "/cartAction" + "?" + "firstname=" + firstname + "&lastname=" + lastname + "&address1=" + address1 + "&address2=" + address2 + "&cardtype=" + cardtype + "&cardnumber=" + cardnumber + "&expirydate=" + expirydate + "&confirm=Confirm";
        response = postHttpHelper(url);
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(302), responseCode);

        url = baseUrl + "/profile";
        response = getHttpHelper(url);
        responseCode = Integer.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));
        assertEquals(Integer.valueOf(200), responseCode);
        String html = parseResponse(response);
        assertTrue(html.contains(address1));
        assertTrue(html.contains(address2));
    }
}

