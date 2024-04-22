package com.lti.orderservice.service;

import com.lti.orderservice.exception.OrderException;
import com.lti.orderservice.model.Order;
import com.lti.orderservice.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService{

    public static final String FAILED_TO_UPDATE_PRODUCT_INVENTORY = "Failed to update product Inventory";
    public static final String CASH = "CASH";
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);
    public static final String NEW = "NEW";
    public static final String CANCELLED = "CANCELLED";

    @Autowired
    RestTemplate restTemplate;
    public Long orderIdCounter=1000L;
    public List<Order> masterOrderList=new ArrayList<>();
    @Override
    public List<Order> getAllOrders() {
        return masterOrderList;
    }

    @Override
    public Order getOrderById(Long orderId) throws OrderException {
        return masterOrderList.stream().filter(o->
                                Objects.equals(o.getOrderId(), orderId))
                .findFirst().orElseThrow(()->new OrderException("Invalid Order Id:"+orderId));
    }

    @Override
    public Order createNewOrder(String customerName, List<Product> productList) {
        //Validate Product List
        validateProductListAndUpdateInventory(productList);
        Order newOrder= new Order();
        newOrder.setOrderId(orderIdCounter++);
        newOrder.setOrderStatus(NEW);
        newOrder.setPaymentType(CASH);
        newOrder.setCustomerName(customerName);
        newOrder.setProductList(productList);
        newOrder.setTotalBillAmount(calTotalAmountForProductList(productList));
        masterOrderList.add(newOrder);

        return newOrder;
    }
    public boolean validateProductListAndUpdateInventory(List<Product> productList){
        productList.forEach(p->{
            Product product = restTemplate
                    .getForObject("http://PRODUCT-SERVICE/api/product/get/{id}",
                            Product.class,
                            Map.of("id", p.getProductId()));
            if(product==null
                    || !Objects.equals(product.getProductId(), p.getProductId())
                    || product.getQuantity()<p.getQuantity()){
                    throw new OrderException("Invalid product or " +
                            "quantity for Product ID: "+p.getProductId());

            }
        });

        if(!callProductApiToAddProductintoCart(productList)){
            throw new OrderException(FAILED_TO_UPDATE_PRODUCT_INVENTORY);
        }

        return true;

    }
    public Double calTotalAmountForProductList(List<Product> productList){
       return productList
               .stream()
               .mapToDouble(p -> p.getQuantity() *(p.getPrice()))
               .sum();
    }
    @Override
    public Order addProductIntoCart(Long orderId, List<Product> productList) {
        //Validate Order ID exist or not
        if(!isOrderIdValid(orderId)){
            throw new OrderException("Invalid Order");
        }
        //Validate order status before adding product into cart
        if(!isOrderStatusValidToAddProduct(orderId)){
            throw new OrderException("Order status not in NEW");
        }
        //Call Product API to validate and update the product inventory
        if(!callProductApiToAddProductintoCart(productList)){
            throw new OrderException("Failed to update product inventory");
        }
        //Get existing product List from order
        Order existingOrder=getOrderById(orderId);
        //Add new Product list into order and update master OrderList
        return addNewProductIntoOrder(productList, existingOrder);
    }

    @Override
    public Order deleteProductFromCart(Long orderId, List<Product> productList) {
        //Validate Order ID exist or not
        if(!isOrderIdValid(orderId)){
            throw new OrderException("Invalid Order");
        }
        //Validate order status before adding product into cart
        if(!isOrderStatusValidToAddProduct(orderId)){
            throw new OrderException("Order status not in NEW");
        }
        //Get existing product List from order
        Order existingOrder=getOrderById(orderId);
        //Validate product qty before Delete
        if(!validateBeforeDeleteProductFromOrder(productList,existingOrder)){
            throw new OrderException("Invalid product or quantity");
        }
        //Call Product API to validate and update the product inventory
        if(!callProductApiToDeleteProductFromCart(productList)){
            throw new OrderException("Failed to update product inventory");
        }
        //delete product from order
        return deleteProductFromOrder(productList,existingOrder);
    }

    public boolean callProductApiToAddProductintoCart(List<Product> productList){
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://PRODUCT-SERVICE/api/product/addProductToCart", productList, String.class);
        LOGGER.info("HTTP Status: {}", responseEntity.getStatusCode());
        LOGGER.info("Response Body: {}", responseEntity.getBody());
        if(null != responseEntity.getBody() && !"SUCCESS".contains(responseEntity.getBody())){
            throw new OrderException(FAILED_TO_UPDATE_PRODUCT_INVENTORY);
        }
        return true;
    }

    public boolean callProductApiToDeleteProductFromCart(List<Product> productList){
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://PRODUCT-SERVICE/api/product/deleteProductFromCart", productList, String.class);
        LOGGER.info("HTTP Status: {}", responseEntity.getStatusCode());
        LOGGER.info("Response Body: {}", responseEntity.getBody());
        if(Objects.nonNull(responseEntity.getBody()) &&
                !"SUCCESS".contains(responseEntity.getBody())){
            throw new OrderException(FAILED_TO_UPDATE_PRODUCT_INVENTORY);
        }
        return true;
    }

    public boolean isOrderIdValid(Long orderId){
        return masterOrderList.stream().anyMatch(o ->
                Objects.equals(orderId, o.getOrderId()));
    }
    public boolean isOrderStatusValidToAddProduct(Long orderId){
        return masterOrderList.stream().anyMatch(o ->
                Objects.equals(orderId, o.getOrderId())
                && Objects.equals(NEW, o.getOrderStatus())
        );
    }
    public Order addNewProductIntoOrder(List<Product> newProductList, Order existingOrder){
        //Take new product list and compare with existing product list
        List<Product> existingProductList=existingOrder.getProductList();
        //if product already there in the existing list of product then add Qty into it
       newProductList.forEach(newProduct->{
           existingProductList.forEach(p->{
               if (Objects.equals(p.getProductId(), newProduct.getProductId())){
                   p.setQuantity(p.getQuantity()+newProduct.getQuantity());
               }
           });
       });

       //if product not present in the existing list of product then add product into list with requested Qty
        newProductList.forEach(newProduct->{
            if(productExist(newProduct, existingProductList)) {
                existingProductList.add(newProduct);
            }
        });

        //adding updated Product List into master order
        existingOrder.setProductList(existingProductList);
        existingOrder.setTotalBillAmount(calTotalAmountForProductList(existingProductList));
        masterOrderList.forEach(p->{
            if(Objects.equals(p.getOrderId(), existingOrder.getOrderId())){
                p.setProductList(existingProductList);
                p.setTotalBillAmount(existingOrder.getTotalBillAmount());
            }
        });

        return existingOrder;
    }

    public boolean validateBeforeDeleteProductFromOrder(List<Product> deleteProductList, Order existingOrder){
        //Take requested product list and compare with existing product list
        //To check Product and requested quantity valid to remove from existing product list
        List<Product> existingProductList=existingOrder.getProductList();
        //if to be deleted product not exist in current product list the reject request
        deleteProductList.forEach(toBeDeleteProduct->{
            if (productExist(toBeDeleteProduct, existingProductList)){
                throw new OrderException("Given Product not exist in the order Product ID: "+
                        toBeDeleteProduct.getProductId());
            }
        });

        //if product exist but not valid quantity then reject request
        existingProductList.forEach(existingProduct-> deleteProductList.stream()
                .filter(toBeDeleteProduct-> Objects.equals(toBeDeleteProduct.getProductId(), existingProduct.getProductId()))
                .forEach(p-> {
                    if(existingProduct.getQuantity()<p.getQuantity())
                        throw new OrderException("Invalid Qty for given Product ID: " + existingProduct.getProductId());
                }));
        return true;
    }

    public boolean productExist(Product product, List<Product> existingProductList ){
        return existingProductList.stream().noneMatch(p ->
                Objects.equals(product.getProductId(), p.getProductId()));
    }

    public Order deleteProductFromOrder(List<Product> deleteProductList, Order existingOrder){
        List<Product> existingProductList= existingOrder.getProductList();
        deleteProductList.forEach(toBeDeleteProduct->{
            existingProductList.forEach(p->{
                if (Objects.equals(p.getProductId(), toBeDeleteProduct.getProductId()) &&
                        p.getQuantity()>=toBeDeleteProduct.getQuantity()){
                    p.setQuantity(p.getQuantity()-toBeDeleteProduct.getQuantity());
                }
            });
        });
        //If qty 0 then remove product from order
        List<Product> updatedExistingProductList= existingProductList.stream().filter(p->p.getQuantity()>0).toList();
        //Set updated product list
        existingOrder.setProductList(updatedExistingProductList);
        existingOrder.setTotalBillAmount(calTotalAmountForProductList(updatedExistingProductList));

        //CANCELLED the order if productList is empty
        boolean cancelled= updatedExistingProductList.isEmpty();

        //Update master Order List
        masterOrderList.forEach(p->{
            if(Objects.equals(p.getOrderId(), existingOrder.getOrderId())){
                p.setProductList(updatedExistingProductList);
                p.setTotalBillAmount(existingOrder.getTotalBillAmount());
                if(cancelled){
                    p.setOrderStatus(CANCELLED);
                }
            }
        });
        return existingOrder;
    }

}
