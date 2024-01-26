package com.example.sellerspace.model.response;

import com.example.sellerspace.entity.SellerEntity;
import lombok.Data;

import java.util.List;

@Data
public class SellerPageableResponse {
    private PageMeta meta;
    private List<Seller> data;
}

