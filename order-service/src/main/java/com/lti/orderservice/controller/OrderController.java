package com.lti.orderservice.controller;

import com.lti.orderservice.exception.OrderException;
import com.lti.orderservice.model.Order;
import com.lti.orderservice.model.Product;
import com.lti.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    OrderService orderService;

    @GetMapping("/getAll")
    public ResponseEntity<List<Order>> getAllOrders() {
        LOGGER.info("Inside OrderController-->getAllOrders()");
        return new ResponseEntity<>(orderService.getAllOrders(),
                HttpStatus.OK);
    }

    @GetMapping("/get/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) throws OrderException {
        LOGGER.info("Inside OrderController-->getOrderById()");
        return new ResponseEntity<>(orderService.getOrderById(orderId),
                HttpStatus.OK);
    }

    @GetMapping("/callProduct")
    public ResponseEntity<String> callProduct(){
        return restTemplate.getForEntity("http://PRODUCT-SERVICE/api/product/", String.class);
    }

    @PostMapping("/createNewOrder/{customerName}")
    public ResponseEntity<Order> createNewOrder(@PathVariable String customerName,
            @RequestBody List<Product> productList){
        return new ResponseEntity<>(orderService.createNewOrder(customerName,productList),
                HttpStatus.OK);
    }

    @PostMapping("/addProductIntoCart/{orderId}")
    public ResponseEntity<Order> addProductIntoCart(@PathVariable Long orderId,
                                                @RequestBody List<Product> productList){
        return new ResponseEntity<>(orderService.addProductIntoCart(orderId,productList),
                HttpStatus.OK);
    }

    @PostMapping("/deleteProductFromCart/{orderId}")
    public ResponseEntity<Order> deleteProductIntoCart(@PathVariable Long orderId,
                                                    @RequestBody List<Product> productList){
        return new ResponseEntity<>(orderService.deleteProductFromCart(orderId,productList),
                HttpStatus.OK);
    }

}