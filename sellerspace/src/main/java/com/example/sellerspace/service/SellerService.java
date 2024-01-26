package com.example.sellerspace.service;

import com.example.sellerspace.entity.SellerEntity;
import com.example.sellerspace.entity.SellerInfoEntity;
import com.example.sellerspace.model.request.SellerSortBy;
import com.example.sellerspace.model.response.PageMeta;
import com.example.sellerspace.model.response.ProducerSellerState;
import com.example.sellerspace.model.response.Seller;
import com.example.sellerspace.model.response.SellerPageableResponse;
import com.example.sellerspace.repository.ProducerRepository;
import com.example.sellerspace.repository.SellerInfoRepository;
import com.example.sellerspace.request.SellerFilter;
import org.springframework.data.domain.Page;
import com.example.sellerspace.request.PageInput;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SellerService {

    private final SellerInfoRepository sellerInfoRepository;
    public SellerService(SellerInfoRepository sellerRepository) {
        this.sellerInfoRepository = sellerRepository;

    }

    public SellerPageableResponse getSellers(SellerFilter filter, PageInput pageInput, SellerSortBy sortBy) {
        // Create Specification based on filter
        Specification<SellerInfoEntity> spec = SellerSpecifications.buildSpecification(filter);

        // Create Pageable instance for pagination and sorting
        Pageable pageable = createPageable(pageInput, sortBy);

        // Fetch page of sellers based on Specification and Pageable
        Page<SellerInfoEntity> sellerPage = sellerInfoRepository.findAll(spec, pageable);

        // Convert entities to GraphQL response types
        List<Seller> sellers = sellerPage.getContent().stream()
                .map(this::convertToSellerGraphQLType)
                .collect(Collectors.toList());

        // Constructing the PageMeta
        PageMeta pageMeta = new PageMeta();
        pageMeta.setPage(pageable.getPageNumber());
        pageMeta.setSize(pageable.getPageSize());
        pageMeta.setTotalElements(sellerPage.getTotalElements());
        pageMeta.setTotalPages(sellerPage.getTotalPages());

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
        seller.setProducerSellerStates(sellerInfoEntity.getSellerEntities().stream()
                .map(info -> new ProducerSellerState(info.getProducer().getId(), info.getProducer().getName(), info.getState(), info.getId()))
                .collect(Collectors.toList()));

        return seller;
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
            return root.get("sellerEntities").get("producer").get("id").in(producerIds);
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