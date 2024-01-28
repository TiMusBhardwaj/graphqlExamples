package com.example.sellerspace.model.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data

public class Seller {
    private String sellerName;
    private String externalId;
    private List<ProducerSellerState> producerSellerStates;
    private String marketplaceId;

    public static Seller of(String name, String externalId, String marketplaceId){
        return new Seller(name, externalId, marketplaceId);
    }

    private Seller(String name, String externalId, String marketplaceId){
        this.sellerName = name;
        this.externalId = externalId;
        this.marketplaceId = marketplaceId;
    }

}
