package com.example.sellerspace.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "seller_infos", schema = "public")
@Data
public class SellerInfoEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "marketplace_id", referencedColumnName = "id")
    private MarketplaceEntity marketplace;

    @Column(name = "name", length = 2048, nullable = false)
    private String name;

    @Column(name = "url", length = 2048)
    private String url;

    @Column(name = "country", length = 255)
    private String country;

    @Column(name = "external_id", length = 255)
    private String externalId;

    @OneToMany(mappedBy = "sellerInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SellerEntity> sellerEntities = new ArrayList<>();

}
