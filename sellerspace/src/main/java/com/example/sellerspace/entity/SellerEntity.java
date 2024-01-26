package com.example.sellerspace.entity;

import com.example.sellerspace.model.SellerState;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "sellers", schema = "public")
@Data
public class SellerEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "producer_id", nullable = false)
    private ProducerEntity producer;

    @ManyToOne
    @JoinColumn(name = "seller_info_id")
    private SellerInfoEntity sellerInfo;

    @Column(name = "state", length = 255, nullable = false)
    private SellerState state;


}
