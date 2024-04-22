package com.lti.productservice.controller;

import com.lti.productservice.exception.ProductException;
import com.lti.productservice.model.Product;
import com.lti.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "List of Product APIs")
@RestController
@RequestMapping("/api/product")
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    ProductService productService;

    @Operation(summary = "API to get the welcome message from the Product Service")
    @GetMapping("/")
    public ResponseEntity<String> welcomeProduct() {
        LOGGER.info("Inside ProductController-->welcomeProduct()");
        return new ResponseEntity<>("Welcome to Product Service", HttpStatus.OK);
    }

    @Operation(summary = "API to get all the product list from the product inventory")
    @GetMapping("/getAll")
    public ResponseEntity<List<Product>> getAllProducts() {
        LOGGER.info("Inside ProductController-->getAllProducts()");
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }
    @Operation(summary = "API to get the product details from the product inventory by providing the Product Id")
    @GetMapping("/get/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) throws ProductException {
        LOGGER.info("Inside ProductController-->getAllProducts()");
        return new ResponseEntity<>(productService.getProductById(productId), HttpStatus.OK);
    }

    @Operation(summary = "API to reduce the product quantity from the inventory")
    @PostMapping("/addProductToCart")
    public ResponseEntity<String> addProductToCart(@RequestBody List<Product> productList){
        return new ResponseEntity<>(productService.addProductToCart(productList),
                HttpStatus.OK);
    }
    @Operation(summary = "API to add the product quantity into the inventory")
    @PostMapping("/deleteProductFromCart")
    public ResponseEntity<String> deleteProductFromCart(@RequestBody List<Product> productList){
        return new ResponseEntity<>(productService.deleteProductFromCart(productList),
                HttpStatus.OK);
    }
}
