package com.example.sellerspace.service;

import com.example.sellerspace.entity.ProducerEntity;
import com.example.sellerspace.entity.SellerEntity;
import com.example.sellerspace.entity.SellerInfoEntity;
import com.example.sellerspace.model.request.SellerSortBy;
import com.example.sellerspace.model.response.PageMeta;
import com.example.sellerspace.model.response.Seller;
import com.example.sellerspace.model.response.SellerPageableResponse;
import com.example.sellerspace.repository.ProducerRepository;
import com.example.sellerspace.repository.SellerInfoRepository;
import com.example.sellerspace.repository.SellerRepository;
import com.example.sellerspace.request.SellerFilter;
import org.springframework.data.domain.Page;
import com.example.sellerspace.request.PageInput;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SellerService {

    private final SellerInfoRepository sellerInfoRepository;
    private final SellerRepository sellerRepository;

    private final ProducerRepository producerRepository;

    public SellerService(SellerInfoRepository sellerInfoRepository, SellerRepository sellerRepository, ProducerRepository producerRepository) {
        this.sellerInfoRepository = sellerInfoRepository;
        this.sellerRepository = sellerRepository;
        this.producerRepository = producerRepository;

    }

    public SellerPageableResponse getSellers(SellerFilter filter, PageInput pageInput, SellerSortBy sortBy) {
        // Create Specification based on filter
        Specification<SellerInfoEntity> spec = SellerSpecifications.buildSpecification(filter);

        // Create Pageable instance for pagination and sorting
        Pageable pageable = createPageable(pageInput, sortBy);

        // Fetch page of sellers based on Specification and Pageable
        Page<SellerInfoEntity> sellerPage = sellerInfoRepository.findAll(spec, pageable);
        long totalElements = sellerInfoRepository.count(spec);
        // Convert entities to GraphQL response types
        List<Seller> sellers = sellerPage.getContent().stream()
                .map(this::convertToSellerGraphQLType)
                .collect(Collectors.toList());

        // Constructing the PageMeta
        PageMeta pageMeta = new PageMeta();
        pageMeta.setPage(pageable.getPageNumber());
        pageMeta.setSize(pageable.getPageSize());
        pageMeta.setTotalElements(totalElements);
        pageMeta.setTotalPages((int) (totalElements/pageable.getPageSize()));

        // Constructing the response
        SellerPageableResponse response = new SellerPageableResponse();
        response.setMeta(pageMeta);
        response.setData(sellers);

        return response;
    }

    private Pageable createPageable(PageInput pageInput, SellerSortBy sortBy) {
        // Assuming PageInput.page is zero-based
        int page = pageInput.getPage();
        int size = pageInput.getSize();


        return PageRequest.of(page, size, sortBy.getSort());
    }

    private Seller convertToSellerGraphQLType(SellerInfoEntity sellerInfoEntity) {

        Seller seller = new Seller();
        seller.setSellerName(sellerInfoEntity.getName());
        seller.setExternalId(sellerInfoEntity.getExternalId());
        seller.setMarketplaceId(sellerInfoEntity.getMarketplace().getId());
        return seller;
    }



    public List<SellerEntity> getSellerByExternalIdAndMarketplaceId(String externalId, String marketplaceId) {

        return sellerRepository.findBySellerInfo_ExternalIdAndSellerInfo_MarketplaceId(externalId, marketplaceId);
    }

    public Iterable<ProducerEntity> getProducersByIds(Set<UUID> Ids) {
        return producerRepository.findAllById(Ids);
    }
}

class SellerSpecifications {

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

    public static Specification<SellerInfoEntity> buildSpecification(SellerFilter filter) {
        return Specification.where(hasName(filter.getSearchByName()))
                .and(hasProducerIds(filter.getProducerIds()))
                .and(hasMarketplaceIds(filter.getMarketplaceIds()));
    }
}