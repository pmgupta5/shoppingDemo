package com.lti.productservice.service;

import com.lti.productservice.exception.ProductException;
import com.lti.productservice.model.Product;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
@Service
public class ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    public List<Product> productList= new ArrayList<Product>();
    @PostConstruct
    public void initializeProduct() {
        productList.add(new Product(1L, "Dell Laptop", 40000.00,10L));
        productList.add(new Product(2L, "HP Laptop", 45000.00,11L));
        productList.add(new Product(3L, "Acer Laptop", 30000.00,6L));
        productList.add(new Product(4L, "Lenovo Laptop", 32000.00,8L));
    }

    public Mono<Product> updateQty(long productId, Long qty) throws ProductException {
        if (isRequestQtyValid(productId, qty)) {
            modifyExistingProductList(productId, qty);
        } else {
            throwProductException("Invalid Product or quantity provided");
        }
        Mono<Product> productMono = Flux.fromStream(productList.stream())
                .filter(product -> product.getProductId() == productId)
                .next()
                .switchIfEmpty(Mono.error(new ProductException("Product not found with productId : " + productId)));
        return productMono;
    }
    public Product updateQtyNew(long productId, Long qty) throws ProductException {
        if (isRequestQtyValid(productId, qty)) {
            modifyExistingProductList(productId, qty);
        } else {
            throwProductException("Invalid Product or quantity provided");
        }
        return productList.stream()
                .filter(product -> product.getProductId() == productId).findFirst().orElse(null);
    }
    public boolean isRequestQtyValid(long productId, Long qty){
        Product validProduct = productList.stream()
                .filter(product -> product.getProductId() == productId &&
                        product.getQuantity()>=qty).
                findFirst().orElse(null);
       return validProduct != null;
    }

    public void  modifyExistingProductList(Long productId, Long qty){
        productList.forEach(product -> {
                    if( product.getProductId() == productId && isRequestQtyValid(productId,qty)){
                        product.setQuantity(product.getQuantity()-qty);
                    }
                });
    }

    public void  throwProductException (String errorMessage) throws ProductException {
       throw new ProductException(errorMessage);
    }



}
