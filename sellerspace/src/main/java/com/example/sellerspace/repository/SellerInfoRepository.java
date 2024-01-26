package com.example.sellerspace.repository;

import com.example.sellerspace.entity.SellerEntity;
import com.example.sellerspace.entity.SellerInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SellerInfoRepository extends JpaRepository<SellerInfoEntity, UUID>,
                                            JpaSpecificationExecutor<SellerInfoEntity> {

}
