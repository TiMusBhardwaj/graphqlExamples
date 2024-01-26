package com.example.sellerspace.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "producers", schema = "public")
@Data
public class ProducerEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


}
