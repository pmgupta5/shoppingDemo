package com.lti.productservice.model;

public class Product {
    private Long productId;
    private String getProductName;
    private Double price;
    private Long quantity;

    public Product() {
    }
    public Product(Long productId, String getProductName, Double price, Long quantity) {
        this.productId = productId;
        this.getProductName = getProductName;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getGetProductName() {
        return getProductName;
    }

    public void setGetProductName(String getProductName) {
        this.getProductName = getProductName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
