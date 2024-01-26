package com.example.sellerspace.repository;

import com.example.sellerspace.entity.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {

}
