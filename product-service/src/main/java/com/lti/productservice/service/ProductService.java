package com.lti.productservice.service;

import com.lti.productservice.exception.ProductException;
import com.lti.productservice.model.Product;

import java.util.List;

public interface ProductService {
    public List<Product> getAllProducts();
    public Product getProductById(Long productId) throws ProductException;
    public String addProductToCart(List<Product> productList);
    public String deleteProductFromCart(List<Product> productList);

}