package com.example.sellerspace.repository;

import com.example.sellerspace.entity.SellerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SellerRepository extends JpaRepository<SellerEntity, UUID>,
                                            JpaSpecificationExecutor<SellerEntity> {

}
