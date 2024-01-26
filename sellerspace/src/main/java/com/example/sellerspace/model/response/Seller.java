package com.example.sellerspace.model.response;

import lombok.Data;

import java.util.List;

@Data
public class Seller {
    private String sellerName;
    private String externalId;
    private List<ProducerSellerState> producerSellerStates;
    private String marketplaceId;


}
