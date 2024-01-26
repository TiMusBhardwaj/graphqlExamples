package com.example.sellerspace.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "marketplaces", schema = "public")
@Data
public class MarketplaceEntity {

    @Id
    @Column(name = "id", length = 255)
    private String id;

    @Column(name = "description", length = 255)
    private String description;


}
