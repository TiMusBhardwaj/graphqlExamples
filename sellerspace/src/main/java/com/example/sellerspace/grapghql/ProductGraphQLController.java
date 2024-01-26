package com.example.sellerspace.grapghql;

import com.example.sellerspace.entity.Product;
import com.example.sellerspace.repository.ProductRepository;
import com.example.sellerspace.request.ProductInput;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.Optional;

@Controller
public class ProductGraphQLController {

    private final ProductRepository productRepository;

    public ProductGraphQLController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @QueryMapping
    public Optional<Product> productById(@Argument Long id) {
        return productRepository.findById(id);
    }

    @QueryMapping
    public Iterable<Product> allProducts() {
        return productRepository.findAll();
    }

    @MutationMapping
    public Product addProduct(@Argument ProductInput input) {
        Product product = new Product();  // assuming you have a default constructor
        product.setName(input.getName());
        product.setDescription(input.getDescription());
        product.setPrice(input.getPrice());
        return productRepository.save(product);
    }
}
