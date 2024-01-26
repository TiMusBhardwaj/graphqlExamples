package com.example.sellerspace.request;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SellerFilter {
    private String searchByName;
    private List<UUID> producerIds;
    private List<String> marketplaceIds;

}
