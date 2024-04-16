package com.lti.orderservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    /*@Autowired
    private WebClient webClient;*/
    @Autowired
    private WebClient.Builder webClientBuilder;

    @GetMapping("/")
    public Mono<String> welcomeProduct() {
        LOGGER.info("Inside OrderController-->welcomeProduct()");
        return  Mono.just("Welcome to order service.");
    }

    @GetMapping("/callProductService")
    public Mono<String> callProductService() {
        LOGGER.info("Inside Order-->callProductService()");
        /*return webClient.get()
                .uri("/api/product/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(String.class);*/
       return webClientBuilder.build()
                .get()
                .uri("http://product-service/api/product/")
                .retrieve()
                .bodyToMono(String.class);
    }

}