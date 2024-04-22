package com.lti.productservice.model;

import com.google.gson.Gson;
import io.cucumber.cucumberexpressions.Transformer;

import java.util.ArrayList;
import java.util.List;

public class ProductListTransformer implements Transformer<List<Product>> {
    @Override
    public List<Product> transform(String s) throws Throwable {
        return new Gson().fromJson(s, ArrayList.class);
    }

}