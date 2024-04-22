package com.lti.orderservice.stepDefinations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.lti.orderservice.model.Order;
import com.lti.orderservice.model.Product;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.Assert.*;

import org.mockito.internal.matchers.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;


public class OrderStepDef {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderStepDef.class);

    @DataTableType
    public Order orderEntryTransformer(Map<String, String> row) throws IOException {
        Order order =new Order();
        order.setOrderId(Long.parseLong(row.get("orderId")));
        order.setCustomerName(row.get("customerName"));
        order.setOrderStatus(row.get("orderStatus"));
        order.setPaymentType(row.get("paymentType"));
        String productListString=row.get("productList");
        order.setProductList(new Gson().fromJson(productListString, new TypeToken<List<Product>>(){}.getType()));
        order.setTotalBillAmount(Double.valueOf(row.get("totalBillAmount")));
        return  order;
    }

    @DataTableType
    public Product productEntryTransformer(Map<String, String> row) {
        return new Product(
                Long.parseLong(row.get("productId")),
                row.get("productName"),
                Double.valueOf(row.get("price")),
                Long.parseLong(row.get("quantity"))
        );
    }


    @Autowired
    private TestRestTemplate restTemplate;
    String url;
    private ResponseEntity<String> response;

    @Given("API endpoint {string} to call the Product welcome API")
    public void apiEndpointToCallTheProductWelcomeAPI(String givenUrl) {
        url=givenUrl;
        assertNotNull(givenUrl);
    }

    @When("client make call to above API then return status {int} OK")
    public void clientMakeCallToAboveAPIThenReturnStatusOK(int expectedStatusCode) {
        response=restTemplate.exchange(RequestEntity.get(url)
                .build(), String.class);
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(expectedStatusCode));
    }

    @Then("client will receive the message {string}")
    public void clientWillReceiveTheMessageWelcomeToProductService(String expectedMessage) {
        assertEquals(response.getBody(),expectedMessage);
    }
//---------------------------test create new Order--------------------------------


    List<Product> productList;
    @Given("customer name {string}, url {string} and product list")
    public void customerNameUrlAndProductList(String name, String createOrderUrl, List<Product> inputProductList) {
        url=createOrderUrl.replace("{customerName}",name);
        LOGGER.info(url);
        inputProductList.forEach(p->LOGGER.info(p.toString()));
        productList=inputProductList;
        assertNotNull(inputProductList);
    }

    ResponseEntity<Order> responseOrder;
    @When("client make call for above API then return status {int} OK")
    public void clientMakeCallForAboveAPIThenReturnStatusOK(int expectedHttpStatusCode) {
      responseOrder=restTemplate.postForEntity(url,productList,Order.class);
      assertEquals(responseOrder.getStatusCode(),HttpStatusCode.valueOf(expectedHttpStatusCode));
    }

    @Then("client will receive the order details")
    public void clientWillReceiveTheOrderDetails(List<Order> orders) {
        orders.forEach(o->LOGGER.info(o.toString()));
       assertEquals (Objects.requireNonNull(responseOrder.getBody()).getTotalBillAmount(), orders.get(0).getTotalBillAmount());
    }

//----------------------------get All Orders -----------------------------------------------------------
    @Given("to get the all orders url {string}")
    public void toGetTheAllOrdersUrl(String getAllOrderUrl) {
        url=getAllOrderUrl;
    }
    ResponseEntity<Order[]> responseAllOrder;
    @When("client make call for the above API to get the all orders then return status {int} OK")
    public void clientMakeCallForTheAboveAPIToGetTheAllOrdersThenReturnStatusOK(int expectedStatusCode) {
        responseAllOrder= restTemplate.exchange(RequestEntity.get(url)
                .build(), Order[].class);
        assertEquals(responseAllOrder.getStatusCode(),HttpStatusCode.valueOf(expectedStatusCode));
    }
    @Then("client will receive all the order details")
    public void clientWillReceiveAllTheOrderDetails(List<Order> orders) {
        assertFalse(Arrays.stream(Objects.requireNonNull(responseAllOrder.getBody())).findAny().isEmpty());
    }


//----------------------------get oder by Order Id -----------------------------------------------------------

    @Given("to get the order details by the orderId {int} and url {string}")
    public void toGetTheOrderDetailsByTheOrderIdAndUrl(int givenOrderId, String getOrderByIdUrl) {
        url=getOrderByIdUrl.replace("{orderId}",String.valueOf(givenOrderId));
        assertTrue(url.contains(String.valueOf(givenOrderId)));
    }

    @When("client make call for the above API to get the order details return status {int} OK")
    public void clientMakeCallForTheAboveAPIToGetTheOrderDetailsReturnStatusOK(int expectedStatusCode) {
        responseOrder= restTemplate.exchange(RequestEntity.get(url)
                .build(), Order.class);
        assertEquals(responseOrder.getStatusCode(),HttpStatusCode.valueOf(expectedStatusCode));
    }
    @Then("client will receive the searched order details")
    public void clientWillReceiveTheSearchedOrderDetails(List<Order> orders) {
       assertNotNull(responseOrder.getBody());
       assertEquals(responseOrder.getBody().getOrderId(), orders.get(0).getOrderId());
    }
//-------------------------------------------http://localhost:8082/api/order/addProductIntoCart/{orderId}-------------------------------

    @Given("order Id {int} is given with the below required list of product and required quantity to call url {string}")
    public void orderIdIsGivenWithTheBelowRequiredListOfProductAndRequiredQuantityToCallUrl(int orderId, String addProductUrl, List<Product> needToAddProduct) {
        url=addProductUrl.replace("{orderId}",String.valueOf(orderId));
        productList=needToAddProduct;
        assertTrue(url.contains(String.valueOf(orderId)));
    }

    @When("client make call for the above API to add the product quantity into the order and return status {int} OK")
    public void clientMakeCallForTheAboveAPIToGetAddProductIntoTheOrderAndReturnStatusOK(int expectedStatusCode) {
        responseOrder=restTemplate.postForEntity(url,productList,Order.class);
        assertEquals(responseOrder.getStatusCode(),HttpStatusCode.valueOf(expectedStatusCode));
    }
    @Then("client will receive the updated order details after adding the product quantity into the given order")
    public void clientWillReceiveTheUpdatedOrderDetailsAfterAddingTheProducts(List<Order> orders) {
        assertEquals(Objects.requireNonNull(responseOrder.getBody()).getOrderId(), orders.get(0).getOrderId());
        assertEquals(Objects.requireNonNull(responseOrder.getBody()).getTotalBillAmount(), orders.get(0).getTotalBillAmount());
        assertEquals(Objects.requireNonNull(responseOrder.getBody()).getProductList().get(0).getProductId(), orders.get(0).getProductList().get(0).getProductId());
        assertEquals(Objects.requireNonNull(responseOrder.getBody()).getProductList().get(0).getQuantity(), orders.get(0).getProductList().get(0).getQuantity());
    }

//-------------------------------------------http://localhost:8082/api/order/deleteProductFromCart/{orderId}-------------------------------

    @Given("order Id {int} is given with the below required list of product and required quantity to be deleted from the order and url {string}")
    public void orderIdIsGivenWithTheBelowRequiredListOfProductAndRequiredQuantityToBeDeletedFromTheOrderAndUrl(int orderId, String deleteProductUrl, List<Product> needToDeleteThProduct) {
        url=deleteProductUrl.replace("{orderId}",String.valueOf(orderId));
        productList=needToDeleteThProduct;
        assertTrue(url.contains(String.valueOf(orderId)));
    }

    @When("client makes the call to above API to delete the product quantity from the given order and return status {int} OK")
    public void clientMakesTheCallToAboveAPIToDeleteTheProductQuantityFromTheGivenOrderAndReturnStatusOK(int expectedStatusCode) {
        responseOrder=restTemplate.postForEntity(url,productList,Order.class);
        assertEquals(responseOrder.getStatusCode(),HttpStatusCode.valueOf(expectedStatusCode));
    }

    @Then("client will receive the updated order details after deleted the product quantity")
    public void clientWillReceiveTheUpdatedOrderDetailsAfterDeletedTheProductQuantity(List<Order> orders) {
        assertEquals(Objects.requireNonNull(responseOrder.getBody()).getOrderId(), orders.get(0).getOrderId());
        assertEquals(Objects.requireNonNull(responseOrder.getBody()).getTotalBillAmount(), orders.get(0).getTotalBillAmount());
        assertEquals(Objects.requireNonNull(responseOrder.getBody()).getProductList().get(0).getProductId(), orders.get(0).getProductList().get(0).getProductId());
        assertEquals(Objects.requireNonNull(responseOrder.getBody()).getProductList().get(0).getQuantity(), orders.get(0).getProductList().get(0).getQuantity());
    }
}

