package com.example.sellerspace.util;

import com.example.sellerspace.entity.SellerInfoEntity;
import org.springframework.data.jpa.domain.Specification;
import com.example.sellerspace.request.SellerFilter;
import java.util.List;
import java.util.UUID;

/**
 * Utility class for creating JPA Specifications for SellerInfoEntity.
 *
 * This class provides static methods to create specifications based on different criteria like name,
 * producer IDs, and marketplace IDs. These specifications are used to build dynamic queries for fetching sellers.
 */
public class SellerSpecifications {

    /**
     * Creates a specification to filter SellerInfoEntity by name.
     *
     * @param name The name to filter by.
     * @return A Specification for the given name, or null if the name is null or empty.
     */
    public static Specification<SellerInfoEntity> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return null;
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    /**
     * Creates a specification to filter SellerInfoEntity by producer IDs.
     *
     * @param producerIds The list of producer IDs to filter by.
     * @return A Specification for the given producer IDs, or null if the list is null or empty.
     */
    public static Specification<SellerInfoEntity> hasProducerIds(List<UUID> producerIds) {
        return (root, query, criteriaBuilder) -> {
            if (producerIds == null || producerIds.isEmpty()) {
                return null;
            }
            return root.get("sellerEntities").get("producerId").in(producerIds);
        };
    }

    /**
     * Creates a specification to filter SellerInfoEntity by marketplace IDs.
     *
     * @param marketplaceIds The list of marketplace IDs to filter by.
     * @return A Specification for the given marketplace IDs, or null if the list is null or empty.
     */
    public static Specification<SellerInfoEntity> hasMarketplaceIds(List<String> marketplaceIds) {
        return (root, query, criteriaBuilder) -> {
            if (marketplaceIds == null || marketplaceIds.isEmpty()) {
                return null;
            }
            return root.get("marketplace").get("id").in(marketplaceIds);
        };
    }

    /**
     * Creates a specification that always evaluates to true.
     *
     * @return A Specification that always returns true.
     */
    public static Specification<SellerInfoEntity> alwaysTrue() {
        return (root, query, cb) -> cb.isTrue(cb.literal(true));
    }

    /**
     * Builds a combined Specification for SellerInfoEntity based on the provided SellerFilter.
     *
     * @param filter The SellerFilter containing various filtering criteria.
     * @return A combined Specification based on the provided filter criteria.
     */
    public static Specification<SellerInfoEntity> buildSpecification(SellerFilter filter) {
        return Specification.where(hasName(filter.getSearchByName()))
                .and(hasProducerIds(filter.getProducerIds()))
                .and(hasMarketplaceIds(filter.getMarketplaceIds()))
                .and(alwaysTrue());
    }
}
