package com.lti.productservice.stepDefinitions;

import com.lti.productservice.model.Product;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class ProductStepDefs {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductStepDefs.class);

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
    private ResponseEntity<String> exchange;
    String welcomeProductUrl=null;

    @Given("Welcome product url {string}")
    public void welcomeProductUrl(String arg0) {
        welcomeProductUrl=arg0;
    }
    @When("user make call to above welcome API")
    public void userCallWelcomeApi() {
        exchange = restTemplate.exchange(RequestEntity.get(welcomeProductUrl)
                .build(), String.class);
    }
    @Then("user will receive the message {string}")
    public void userShouldReceiveMessageWelcomeToProductService(String message) throws IOException {
        assertThat(exchange.getBody()).isEqualTo(message);

    }


  //--------------------To get All products--------------------------------------------------------

    String getAllProductUrl=null;
    ResponseEntity<Product[]> listAllProducts;
    @Given("To get the all products URL is {string}")
    public void toGetAllProductsUrl(String arg0) {
        getAllProductUrl=arg0;
    }

    @When("user make call to the above URL")
    public void userCallMakeCallToAboveURL() {
        listAllProducts = restTemplate.exchange(RequestEntity.get(getAllProductUrl)
                .build(), Product[].class);
    }

    @Then("user will get the all Product List")
    public void userWillGetTheAllTheProductListFromAPI(List<Product> expectedProducts) {
       List<Product> allProductList=Arrays.stream(Objects.requireNonNull(listAllProducts.getBody())).toList();
        allProductList.forEach(p->LOGGER.info(p.toString()));
        assertFalse(allProductList.isEmpty());

    }
//--------------------To get product by ID  success scenario--------------------------------------------------------

    int productId;
    @Given("Product ID {int} is given as input")
    public void productIDIsGivenAsInput(int id) {
        productId=id;
        assertEquals(productId,id);
    }
    String getProductByIdUrl;

    @And("Product Id {int} pass to get the product from API {string}")
    public void theGivenProductIdPassToGetProductByIdAPI(int id, String url) {
        getProductByIdUrl=url+id;
        assertEquals(getProductByIdUrl,url+id);
    }

    ResponseEntity<Product> responseEntityProduct;
    @When("above API will get call with the success response and status code {int} OK")
    public void getProductByIdAPIGetCallSuccessfullyWithStatusCodeOK(int statusCode) {
        responseEntityProduct= restTemplate.getForEntity(getProductByIdUrl
                , Product.class);
        assertEquals(responseEntityProduct.getStatusCode(), HttpStatusCode.valueOf(statusCode));
    }
    @Then("user will get the Product details for the requested product Id")
    public void userWillGetProductDetailsForRequestedProductId(Product expectedProduct) {
        LOGGER.info(expectedProduct.toString());
        LOGGER.info(Objects.requireNonNull(responseEntityProduct.getBody()).getProductId().toString());
        assertEquals( productId, Objects.requireNonNull(responseEntityProduct.getBody()).getProductId());
    }

    //--------------------To get product by ID  failure scenario--------------------------------------------------------

    @Given("wrong Product ID {int} is given as input which is not available in the inventory")
    public void productIDIsGivenAsInputWhichIsNotAvailableInInventory(int id) {
        productId=id;
        assertEquals(productId,id);
    }
    @And("making API {string} call with the given product id {int} in the path variable")
    public void makingAPICallWithTheGivenProductIdInThePathVariable(String url, int id) {
        getProductByIdUrl=url+id;
        assertEquals(getProductByIdUrl,url+id);
    }


    ResponseEntity<Map> failureResponseGetProductById;
    @When("API will return the failure response without the product details")
    public void getProductByIdAPICallGetFailedAndWonTReturnTheProduct() {
        failureResponseGetProductById= restTemplate.getForEntity(getProductByIdUrl
                , Map.class);
       assertNotEquals( Objects.requireNonNull(failureResponseGetProductById.getBody()).getClass(),
               Product.class);

    }
    @Then("user will get the message {string} and status equal to {string}")
    public void userWillGetMessageAndStatusEqualTo(String errorMessage, String status) {
        LOGGER.info((String) Objects.requireNonNull(failureResponseGetProductById.getBody()).get("error message"));
        assertEquals(errorMessage, failureResponseGetProductById.getBody().get("error message"));
        assertEquals(status, failureResponseGetProductById.getBody().get("status"));
    }


    //--------------------to test endpoint http://localhost:8081/api/product/addProductToCart--------------------------------------------------------

    String addProductToCartUrl;
    List<Product> productList;
    @Given("endpoint {string} to update the product inventory by providing the product information are here")
    public void endpointToUpdateTheProductInventoryByProvidingTheProductInformationProductListAreHere(String url, List<Product> inputProductList) {
        LOGGER.info(url);
        inputProductList.forEach(p->LOGGER.info(p.toString()));
        addProductToCartUrl=url;
        productList=inputProductList;
    }

    ResponseEntity<String> addProductToCartApiResponse;
    @When("above API will get invoke successfully with the response status code {int} OK")
    public void aboveAPIWillGetInvokeSuccessfullyWithTheResponseStatusCodeOK(int expectedCode) {
        addProductToCartApiResponse =restTemplate.postForEntity(addProductToCartUrl,productList,String.class);
        assertEquals(addProductToCartApiResponse.getStatusCode(), HttpStatusCode.valueOf(expectedCode));
    }
    @Then("API will return the response {string};")
    public void apiWillReturnTheResponse(String expectedResponse) {
        assertEquals(addProductToCartApiResponse.getBody(), expectedResponse);
    }

    //--------------------to test endpoint http://localhost:8081/api/product/deleteProductFromCart--------------------------------------------------------


    String deleteProductFromCartUrl;
    @Given("endpoint {string} to delete the product from the inventory by providing the product information are here")
    public void endpointToDeleteTheProductFromTheInventoryByProvidingTheProductInformationAreHere(String url, List<Product> inputProductList) {
        LOGGER.info(url);
        inputProductList.forEach(p->LOGGER.info(p.toString()));
        deleteProductFromCartUrl=url;
        productList=inputProductList;
    }
    ResponseEntity<String> deleteProductFromCartResponse;
    @When("API will get invoke successfully with the response status code {int} OK")
    public void apiWillGetInvokeSuccessfullyWithTheResponseStatusCodeOK(int expectedStatusCode) {
         deleteProductFromCartResponse = restTemplate.postForEntity(deleteProductFromCartUrl,productList,String.class);
         LOGGER.info(" /deleteProductFromCart Status: {}  \n responseBody: {}",deleteProductFromCartResponse.getStatusCode(),deleteProductFromCartResponse.getBody());
        assertEquals(deleteProductFromCartResponse.getStatusCode(), HttpStatusCode.valueOf(expectedStatusCode));
    }
    @Then("will get response {string};")
    public void willGetResponse(String expectedResponse) {
        assertEquals(deleteProductFromCartResponse.getBody(), expectedResponse);
    }
}
