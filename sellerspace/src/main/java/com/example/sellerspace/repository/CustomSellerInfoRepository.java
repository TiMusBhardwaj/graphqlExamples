package com.example.sellerspace.repository;

import org.springframework.stereotype.Repository;


import com.example.sellerspace.entity.SellerInfoEntity;
import com.example.sellerspace.model.SellerInfoDTO;
import com.example.sellerspace.model.request.SellerSortBy;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import com.example.sellerspace.request.PageInput;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
@Repository
public class CustomSellerInfoRepository {

    private final EntityManager entityManager;

    public CustomSellerInfoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Finds sellers based on the specified filter, pagination, and sorting criteria.
     * This method uses Criteria API to construct dynamic queries based on the provided specification.
     *
     * @param spec     The JPA specification for filtering the sellers.
     * @param pageable The pageable object containing pagination information.
     * @param sortBy   The sorting criteria for the result set.
     * @return A page of SellerInfoDTO objects that match the given filter, sorted and paginated as requested.
     */
    public long findTotalByFilter(Specification<SellerInfoEntity> spec, PageInput pageInput, SellerSortBy sortBy) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Query for total count (for pagination)
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<SellerInfoEntity> countRoot = countQuery.from(SellerInfoEntity.class);
        countQuery.select(cb.countDistinct(countRoot.get("id")));
        countQuery.where(spec.toPredicate(countRoot, countQuery, cb));
        return entityManager.createQuery(countQuery).getSingleResult();
    }
    public List<SellerInfoDTO> findSellersByFilterWithPagination(Specification<SellerInfoEntity> spec, PageInput pageInput, SellerSortBy sortBy) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        // Query for fetching data
        CriteriaQuery<SellerInfoDTO> query = cb.createQuery(SellerInfoDTO.class);
        Root<SellerInfoEntity> root = query.from(SellerInfoEntity.class);

        query.select(cb.construct(
                        SellerInfoDTO.class,
                        root.get("id"),
                        root.get("name"),
                        root.get("url"),
                        root.get("country"),
                        root.get("externalId"),
                        root.get("marketplace").get("id")))

                .where(spec.toPredicate(root, query, cb))
                .distinct(true)
        ;


            //query.orderBy(QueryUtils.toOrders(pageable.getSort(), root, cb));
        if (Objects.nonNull(sortBy))
            query.orderBy(sortBy.getCriteriaOrder(cb, root));


        TypedQuery<SellerInfoDTO> typedQuery = entityManager.createQuery(query);

        typedQuery.setFirstResult((pageInput.getPage()-1)*pageInput.getSize());
        typedQuery.setMaxResults(pageInput.getSize());



        return typedQuery.getResultList();
    }
}