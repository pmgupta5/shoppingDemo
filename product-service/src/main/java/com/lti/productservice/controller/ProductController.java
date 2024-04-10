package com.lti.productservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @GetMapping("/")
    public Mono<String> welcomeProduct() {
        LOGGER.info("Inside ProductController-->welcomeProduct()");
        return  Mono.just("Welcome to product service.");
    }
}
