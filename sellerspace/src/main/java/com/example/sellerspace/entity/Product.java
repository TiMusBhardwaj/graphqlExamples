package com.example.sellerspace.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;


@Entity
@Data
public class Product {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String description;
    private double price;
}
