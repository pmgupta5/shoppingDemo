package com.lti.gatewayservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallBackMethodController {

    @GetMapping("/productServiceFallBack")
    public String productServiceFallBackMethod() {
        return "Product Service is taking longer than Expected." +
                " Please try again later";
    }

    @GetMapping("/orderServiceFallBack")
    public String orderServiceFallBackMethod() {
        return "Order Service is taking longer than Expected." +
                " Please try again later";
    }
}
