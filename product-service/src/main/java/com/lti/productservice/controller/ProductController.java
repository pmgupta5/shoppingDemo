package com.lti.productservice.controller;

import com.lti.productservice.exception.ProductException;
import com.lti.productservice.model.Product;
import com.lti.productservice.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    ProductService productService;

    @GetMapping("/")
    public ResponseEntity<String> welcomeProduct() {
        LOGGER.info("Inside ProductController-->welcomeProduct()");
        return new ResponseEntity<>("Welcome to Product Service", HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Product>> getAllProducts() {
        LOGGER.info("Inside ProductController-->getAllProducts()");
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }
    @GetMapping("/get/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) throws ProductException {
        LOGGER.info("Inside ProductController-->getAllProducts()");
        return new ResponseEntity<>(productService.getProductById(productId), HttpStatus.OK);
    }

    @PostMapping("/addProductToCart")
    public ResponseEntity<String> addProductToCart(@RequestBody List<Product> productList){
        return new ResponseEntity<>(productService.addProductToCart(productList),
                HttpStatus.OK);
    }
    @PostMapping("/deleteProductFromCart")
    public ResponseEntity<String> deleteProductFromCart(@RequestBody List<Product> productList){
        return new ResponseEntity<>(productService.deleteProductFromCart(productList),
                HttpStatus.OK);
    }
}
