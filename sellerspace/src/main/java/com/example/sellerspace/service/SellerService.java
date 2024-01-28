package com.example.sellerspace.service;

import com.example.sellerspace.entity.ProducerEntity;
import com.example.sellerspace.entity.SellerEntity;
import com.example.sellerspace.entity.SellerInfoEntity;
import com.example.sellerspace.model.request.SellerSortBy;
import com.example.sellerspace.model.response.PageMeta;
import com.example.sellerspace.model.response.Seller;
import com.example.sellerspace.model.response.SellerPageableResponse;
import com.example.sellerspace.repository.CustomSellerInfoRepository;
import com.example.sellerspace.repository.ProducerRepository;
import com.example.sellerspace.repository.SellerRepository;
import com.example.sellerspace.request.PageInput;
import com.example.sellerspace.request.SellerFilter;
import com.example.sellerspace.util.SellerSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SellerService {

    private final CustomSellerInfoRepository customSellerInfoRepository;

    private final SellerRepository sellerRepository;

    private final ProducerRepository producerRepository;

    public SellerService( SellerRepository sellerRepository, ProducerRepository producerRepository, CustomSellerInfoRepository customSellerInfoRepository) {

        this.sellerRepository = sellerRepository;
        this.producerRepository = producerRepository;
        this.customSellerInfoRepository = customSellerInfoRepository;

    }

    /**
     * Retrieves a paginated response of sellers matching the specified filter, page input, and sorting criteria.
     *
     * @param filter     The criteria used to filter sellers.
     * @param pageInput  Pagination information including page number and size.
     * @param sortBy     The criteria used to sort the sellers.
     * @return A SellerPageableResponse containing the paginated list of sellers and metadata.
     */
    public SellerPageableResponse getSellers(SellerFilter filter, PageInput pageInput, SellerSortBy sortBy) {
        //Create Filter For Query
        Specification<SellerInfoEntity> spec = SellerSpecifications.buildSpecification(filter);

        long totals = customSellerInfoRepository.findTotalByFilter(spec, pageInput, sortBy);
        if (totals == 0){
            return getSellerPageableResponse(pageInput, Collections.emptyList(), totals);
        }
        List<Seller> sellers = customSellerInfoRepository.findSellersByFilterWithPagination(spec, pageInput, sortBy).stream().map(x -> Seller.of(x.getId(), x.getName(), x.getExternalId(), x.getMarketplaceId()))
                .collect(Collectors.toList());
        return getSellerPageableResponse(pageInput, sellers, totals);
    }

    public List<SellerEntity> getSellerByExternalIdAndMarketplaceId(String externalId, String marketplaceId) {

        return sellerRepository.findBySellerInfo_ExternalIdAndSellerInfo_MarketplaceId(externalId, marketplaceId);
    }

    public Iterable<ProducerEntity> getProducersByIds(Set<UUID> Ids) {
        return producerRepository.findAllById(Ids);
    }

    private static SellerPageableResponse getSellerPageableResponse(PageInput pageInput, List<Seller> sellers, long total) {
        // Constructing the PageMeta
        PageMeta pageMeta = new PageMeta();
        pageMeta.setPage(pageInput.getPage());
        pageMeta.setSize(sellers.size());
        pageMeta.setTotalElements(total);
        pageMeta.setTotalPages((int) Math.ceil((double) total / pageInput.getSize()));

        // Constructing the response
        SellerPageableResponse response = new SellerPageableResponse();
        response.setMeta(pageMeta);
        response.setData(sellers);
        return response;
    }

    public List<SellerEntity> getSellersByInfoIds(Set<UUID> ids) {

        return sellerRepository.findBySellerInfo_IdIn(ids);
    }
}
