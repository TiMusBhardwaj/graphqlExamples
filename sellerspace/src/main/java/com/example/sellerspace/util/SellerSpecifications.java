package com.example.sellerspace.util;

import com.example.sellerspace.entity.SellerInfoEntity;
import org.springframework.data.jpa.domain.Specification;
import com.example.sellerspace.request.SellerFilter;
import java.util.List;
import java.util.UUID;

public class SellerSpecifications {

    public static Specification<SellerInfoEntity> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return null;
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<SellerInfoEntity> hasProducerIds(List<UUID> producerIds) {
        return (root, query, criteriaBuilder) -> {
            if (producerIds == null || producerIds.isEmpty()) {
                return null;
            }
            return root.get("sellerEntities").get("producerId").in(producerIds);
        };
    }

    public static Specification<SellerInfoEntity> hasMarketplaceIds(List<String> marketplaceIds) {
        return (root, query, criteriaBuilder) -> {
            if (marketplaceIds == null || marketplaceIds.isEmpty()) {
                return null;
            }
            return root.get("marketplace").get("id").in(marketplaceIds);
        };
    }
    public static Specification<SellerInfoEntity> alwaysTrue() {
        return (root, query, cb) -> cb.isTrue(cb.literal(true));
    }


    public static Specification<SellerInfoEntity> buildSpecification(SellerFilter filter) {
        return Specification.where(hasName(filter.getSearchByName()))
                .and(hasProducerIds(filter.getProducerIds()))
                .and(hasMarketplaceIds(filter.getMarketplaceIds()))
                .and(alwaysTrue());
    }
}
