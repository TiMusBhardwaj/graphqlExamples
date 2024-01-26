package com.example.sellerspace.repository;

import com.example.sellerspace.entity.ProducerEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ProducerRepository extends CrudRepository<ProducerEntity, UUID> {

}
