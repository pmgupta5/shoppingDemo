package com.lti.productservice.service;

import com.lti.productservice.exception.ProductException;
import com.lti.productservice.model.Product;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);
    public List<Product> masterProductList = new ArrayList<>();
    @PostConstruct
    public void initializeProduct() {
        masterProductList.add(new Product(1L, "Dell Laptop", 40000.00,10L));
        masterProductList.add(new Product(2L, "HP Laptop", 45000.00,11L));
        masterProductList.add(new Product(3L, "Acer Laptop", 30000.00,6L));
        masterProductList.add(new Product(4L, "Lenovo Laptop", 32000.00,8L));
    }

    public Product updateQty(long productId, Long qty) throws ProductException {
        if (isRequestQtyValid(productId, qty)) {
            modifyExistingProductList(productId, qty);
        } else {
            throwProductException("Invalid Product or quantity provided");
        }
        return masterProductList.stream()
                .filter(product -> product.getProductId() == productId)
                .findFirst().orElse(null);

    }
    public Product updateQtyNew(long productId, Long qty) throws ProductException {
        if (isRequestQtyValid(productId, qty)) {
            modifyExistingProductList(productId, qty);
        } else {
            throwProductException("Invalid Product or quantity provided");
        }
        return masterProductList.stream()
                .filter(product -> product.getProductId() == productId).findFirst().orElse(null);
    }
    public boolean isRequestQtyValid(long productId, Long qty){
        Product validProduct = masterProductList.stream()
                .filter(product -> product.getProductId() == productId &&
                        product.getQuantity()>=qty).
                findFirst().orElse(null);
        return validProduct != null;
    }

    public void  modifyExistingProductList(Long productId, Long qty){
        masterProductList.forEach(product -> {
            if( product.getProductId() == productId && isRequestQtyValid(productId,qty)){
                product.setQuantity(product.getQuantity()-qty);
            }
        });
    }

    public void  throwProductException (String errorMessage) throws ProductException {
        throw new ProductException(errorMessage);
    }


    @Override
    public List<Product> getAllProducts() {
        return masterProductList;
    }

    @Override
    public Product getProductById(Long productId) throws ProductException {
        return masterProductList.stream().filter(p->p.getProductId()==productId)
                .findFirst()
                .orElseThrow(()->new ProductException("Invalid ProductId:"+productId));
    }

    @Override
    public String addProductToCart(List<Product> productList) {
        //Check valid product
        if(!isProductValid(productList)){
            throw new ProductException("Invalid Product Given.");
        }
        //Check Qty available or not
        if(!isRequestProductQtyAvailable(productList)){
            throw new ProductException("Product Quantity not available.");
        }
        //update product Qty in inventory
        productList.forEach(p->{
            if(!reduceQtyFromMaster(p)){
                throw new ProductException("Unable to update product qty in inventory.");
            }
        });
        return "SUCCESS";
    }

    @Override
    public String deleteProductFromCart(List<Product> productList) {
        //Check valid product
        if(!isProductValid(productList)){
            throw new ProductException("Invalid Product Given.");
        }
        //update product Qty in inventory
        productList.forEach(p->{
            if(!addProductInInventory(p)){
                throw new ProductException("Unable to update product qty in inventory.");
            }
        });

        return "SUCCESS";
    }

    public boolean isProductValid(List<Product> productList){
        //check Product Exist or not
        productList.forEach(p->{
           if(!isProductExist(p)){
               throw new ProductException("Product not available ProductId: "+p.getProductId());
           }
        });
        return true;
    }

    public boolean isProductExist(Product product){
        return masterProductList.stream().anyMatch(p ->
                        Objects.equals(product.getProductId(), p.getProductId()) &&
                        Objects.equals(product.getGetProductName(), p.getGetProductName()));
    }

    public boolean addProductInInventory(Product product){
        masterProductList.forEach(p->{
            if (p.getProductId()==product.getProductId()){
                p.setQuantity(p.getQuantity()+product.getQuantity());
            }
        });
        return true;
    }

    public boolean removeProductInInventory(List<Product> productList){

        masterProductList.stream().forEach(masterProduct-> productList.stream()
                .filter(p->p.getProductId()==masterProduct.getProductId())
                .forEach(p-> {
                    if(p.getQuantity()<=masterProduct.getQuantity()){
                        masterProduct.setProductId(masterProduct.getQuantity() - p.getQuantity());
                    }
                    else {
                        throw new ProductException("Quantity not available for productId: "+ p.getProductId());
                    }

                }));
        return true;
    }


    public boolean reduceQtyFromMaster(Product product){
        masterProductList.forEach(p->{
            if (p.getProductId()==product.getProductId() &&
                    p.getQuantity()>=product.getQuantity()){
                p.setQuantity(p.getQuantity()-product.getQuantity());
            }
        });
        return true;
    }


    //check requested quantity valid or not.
    public boolean isRequestProductQtyAvailable(List<Product> productList){
        masterProductList.stream().forEach(masterProduct-> productList.stream()
                .filter(p->p.getProductId()==masterProduct.getProductId())
                .forEach(p-> {
                    if(masterProduct.getQuantity()<p.getQuantity())
                        throw new ProductException("Quantity not " +
                                "available for productId: "+ p.getProductId()+
                                " Requested Qty: "+p.getQuantity() +
                                " Available Qty: "+masterProduct.getQuantity());
                }));
        return true;
    }
}
