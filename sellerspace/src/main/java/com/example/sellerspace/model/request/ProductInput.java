package com.example.sellerspace.request;

import lombok.Data;

@Data
public class ProductInput {
    private String name;
    private String description;
    private double price;

}