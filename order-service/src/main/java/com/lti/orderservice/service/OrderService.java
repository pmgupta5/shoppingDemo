package com.lti.orderservice.service;

import com.lti.orderservice.exception.OrderException;
import com.lti.orderservice.model.Order;
import com.lti.orderservice.model.Product;

import java.util.List;

public interface OrderService {

    public List<Order> getAllOrders();
    public Order getOrderById(Long orderId) throws OrderException;
    public Order createNewOrder(String customerName, List<Product> productList);
    public Order addProductIntoCart(Long orderId, List<Product> productList);
    public Order deleteProductFromCart(Long orderId, List<Product> productList);
}
