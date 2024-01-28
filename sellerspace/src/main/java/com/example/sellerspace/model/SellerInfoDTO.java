package com.example.sellerspace.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class SellerInfoDTO {
    private UUID id;
    private String name;
    private String url;
    private String country;
    private String externalId;
    private String marketplaceId;
}