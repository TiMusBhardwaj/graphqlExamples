package com.example.sellerspace.model.response;

import com.example.sellerspace.model.SellerState;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ProducerSellerState {
    private UUID producerId;
    private String producerName;
    private SellerState sellerState;
    private UUID sellerId;


}
