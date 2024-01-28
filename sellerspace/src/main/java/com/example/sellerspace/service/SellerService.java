package com.example.sellerspace.service;

import com.example.sellerspace.entity.ProducerEntity;
import com.example.sellerspace.entity.SellerEntity;
import com.example.sellerspace.entity.SellerInfoEntity;
import com.example.sellerspace.model.SellerInfoDTO;
import com.example.sellerspace.model.request.SellerSortBy;
import com.example.sellerspace.model.response.PageMeta;
import com.example.sellerspace.model.response.Seller;
import com.example.sellerspace.model.response.SellerPageableResponse;
import com.example.sellerspace.repository.CustomSellerInfoRepository;
import com.example.sellerspace.repository.ProducerRepository;
import com.example.sellerspace.repository.SellerInfoRepository;
import com.example.sellerspace.repository.SellerRepository;
import com.example.sellerspace.request.SellerFilter;
import com.example.sellerspace.util.SellerSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import com.example.sellerspace.request.PageInput;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SellerService {

    @Autowired
    private CustomSellerInfoRepository customSellerInfoRepository;
    private final SellerInfoRepository sellerInfoRepository;
    private final SellerRepository sellerRepository;

    private final ProducerRepository producerRepository;

    public SellerService(SellerInfoRepository sellerInfoRepository, SellerRepository sellerRepository, ProducerRepository producerRepository, CustomSellerInfoRepository customSellerInfoRepository) {
        this.sellerInfoRepository = sellerInfoRepository;
        this.sellerRepository = sellerRepository;
        this.producerRepository = producerRepository;
        this.customSellerInfoRepository = customSellerInfoRepository;

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
                .map(x -> Seller.of(x.getName(), x.getExternalId(), x.getMarketplace().getId()))
                .collect(Collectors.toList());


        return getSellerPageableResponse(sellerPage, sellers);
    }


    public SellerPageableResponse getSellers2(SellerFilter filter, PageInput pageInput, SellerSortBy sortBy) {
        // Create Pageable instance for pagination and sorting
        Pageable pageable = createPageable(pageInput, sortBy);
        Specification<SellerInfoEntity> spec = SellerSpecifications.buildSpecification(filter);


        Page<SellerInfoDTO> dto = customSellerInfoRepository.findSellersByFilterWithPagination(spec, pageable, sortBy);
        List<Seller> sellers = dto.getContent().stream().map(x -> Seller.of(x.getName(), x.getExternalId(), x.getMarketplaceId()))
                .collect(Collectors.toList());


        return getSellerPageableResponse(dto, sellers);
    }

    private Pageable createPageable(PageInput pageInput, SellerSortBy sortBy) {
        // Assuming PageInput.page is zero-based
        int page = pageInput.getPage() - 1;
        int size = pageInput.getSize();


        return PageRequest.of(page, size, sortBy.getSort());
    }


    public List<SellerEntity> getSellerByExternalIdAndMarketplaceId(String externalId, String marketplaceId) {

        return sellerRepository.findBySellerInfo_ExternalIdAndSellerInfo_MarketplaceId(externalId, marketplaceId);
    }

    public Iterable<ProducerEntity> getProducersByIds(Set<UUID> Ids) {
        return producerRepository.findAllById(Ids);
    }

    private static SellerPageableResponse getSellerPageableResponse(Page<?> sellerPage, List<Seller> sellers) {
        // Constructing the PageMeta
        PageMeta pageMeta = new PageMeta();
        pageMeta.setPage(sellerPage.getPageable().getPageNumber() + 1);
        pageMeta.setSize(sellers.size());
        pageMeta.setTotalElements(sellerPage.getTotalElements());
        pageMeta.setTotalPages(sellerPage.getTotalPages());

        // Constructing the response
        SellerPageableResponse response = new SellerPageableResponse();
        response.setMeta(pageMeta);
        response.setData(sellers);
        return response;
    }
}
