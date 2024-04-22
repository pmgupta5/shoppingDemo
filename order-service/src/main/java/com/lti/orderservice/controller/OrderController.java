package com.lti.orderservice.controller;

import com.lti.orderservice.exception.OrderException;
import com.lti.orderservice.model.Order;
import com.lti.orderservice.model.Product;
import com.lti.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import java.util.List;

@Tag(name = "List of Order APIs")
@RestController
@RequestMapping("/api/order")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    OrderService orderService;

    @Operation(summary = "API to get all the Order list from the Order Service")
    @GetMapping("/getAll")
    public ResponseEntity<List<Order>> getAllOrders() {
        LOGGER.info("Inside OrderController-->getAllOrders()");
        return new ResponseEntity<>(orderService.getAllOrders(),
                HttpStatus.OK);
    }

    @Operation(summary = "API to get the Order details by providing the Order Id in the Path variable")
    @GetMapping("/get/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) throws OrderException {
        LOGGER.info("Inside OrderController-->getOrderById()");
        return new ResponseEntity<>(orderService.getOrderById(orderId),
                HttpStatus.OK);
    }

    @Operation(summary = "Order API to invoke the welcome API of the Product service")
    @GetMapping("/callProduct")
    public ResponseEntity<String> callProduct(){
        return restTemplate.getForEntity("http://PRODUCT-SERVICE/api/product/", String.class);
    }


    @Operation(summary = "API to create the new order by providing product details and customer information")
    @PostMapping("/createNewOrder/{customerName}")
    public ResponseEntity<Order> createNewOrder(@PathVariable String customerName,
            @RequestBody List<Product> productList){
        return new ResponseEntity<>(orderService.createNewOrder(customerName,productList),
                HttpStatus.OK);
    }

    @Operation(summary = "API to add the new product or increase quantity of existing product in the given order details")
    @PostMapping("/addProductIntoCart/{orderId}")
    public ResponseEntity<Order> addProductIntoCart(@PathVariable Long orderId,
                                                @RequestBody List<Product> productList){
        return new ResponseEntity<>(orderService.addProductIntoCart(orderId,productList),
                HttpStatus.OK);
    }

    @Operation(summary = "API to delete the product quantity from the given order details")
    @PostMapping("/deleteProductFromCart/{orderId}")
    public ResponseEntity<Order> deleteProductIntoCart(@PathVariable Long orderId,
                                                    @RequestBody List<Product> productList){
        return new ResponseEntity<>(orderService.deleteProductFromCart(orderId,productList),
                HttpStatus.OK);
    }

}