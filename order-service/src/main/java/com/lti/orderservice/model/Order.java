package com.lti.orderservice.model;

import java.util.List;


public class Order {
    private Long orderId;
    private String customerName;
    private String orderStatus;
    private String paymentType;
    private List<Product> productList;
    private Double totalBillAmount;


    public Order() {
    }

    public Order(Long orderId, String customerName, String orderStatus, String paymentType, List<Product> productList, Double totalBillAmount) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.orderStatus = orderStatus;
        this.paymentType = paymentType;
        this.productList = productList;
        this.totalBillAmount = totalBillAmount;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public Double getTotalBillAmount() {
        return totalBillAmount;
    }

    public void setTotalBillAmount(Double totalBillAmount) {
        this.totalBillAmount = totalBillAmount;
    }

    @Override
    public String toString() {
        return "Order{" +
                "OrderId=" + orderId +
                ", CustomerName='" + customerName + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", PaymentType='" + paymentType + '\'' +
                ", productList=" + productList +
                ", totalBillAmount=" + totalBillAmount +
                '}';
    }


}
