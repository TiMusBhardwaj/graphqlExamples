package com.example.sellerspace.repository;

import com.example.sellerspace.entity.MarketplaceEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface MarketplaceRepository extends CrudRepository<MarketplaceEntity, UUID> {

}
