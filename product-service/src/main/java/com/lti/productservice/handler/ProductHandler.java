package com.lti.productservice.handler;

import com.lti.productservice.exception.ProductException;
import com.lti.productservice.model.Product;
import com.lti.productservice.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class ProductHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductHandler.class);

    @Autowired
    ProductService productService;

    public Mono<ServerResponse> getProducts(ServerRequest request) {
        Flux<Product> products = Flux.fromStream(productService.productList.parallelStream());
        return ServerResponse.ok().body(products, Product.class);
    }

    public Mono<ServerResponse> getProductById(ServerRequest request) {
        Long id = (long) Integer.parseInt(request.pathVariable("productId"));
        Mono<Product> productMono = Flux.fromStream(productService.productList.stream())
                .filter(product -> product.getProductId() == id)
                .next()
                .switchIfEmpty(Mono.error(new ProductException("Product not found with productId : " + id)));
        return ServerResponse.ok().body(productMono, Product.class);
    }

    public Mono<ServerResponse> updateProductQty(ServerRequest request) {
        final long productId = Long.parseLong(request.pathVariable("productId"));
        Optional<String> reqQuantity = request.queryParam("quantity");
        Long quantity= reqQuantity.isPresent()?Long.parseLong(reqQuantity.get()): null;
        try {
            return ServerResponse.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(productService.updateQty(productId, quantity), Product.class);
        } catch (ProductException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public Mono<ServerResponse> updateProductQtys(ServerRequest request) {
        LOGGER.info("Inside ProductHandler->updateProductQtys request: {}", request.toString());
        final long productId = Long.parseLong(request.pathVariable("productId"));
        Mono<Product> monoBody = request.bodyToMono(Product.class);
        Mono<Product> saveResponse = monoBody.map(p -> {
            try {
                return productService.updateQtyNew(p.getProductId(),p.getQuantity());
            } catch (ProductException e) {
                throw new RuntimeException(e);
            }
        });
        return ServerResponse.ok().body(saveResponse,Product.class);
    }
}
