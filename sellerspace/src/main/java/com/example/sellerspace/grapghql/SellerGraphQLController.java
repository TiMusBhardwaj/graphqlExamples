package com.example.sellerspace.grapghql;

import com.example.sellerspace.model.request.SellerSortBy;
import com.example.sellerspace.model.response.SellerPageableResponse;
import com.example.sellerspace.service.SellerService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;
import com.example.sellerspace.request.PageInput;
import com.example.sellerspace.request.SellerFilter;
@Controller
public class SellerGraphQLController {

    private final SellerService sellerService;

    public SellerGraphQLController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @QueryMapping
    public SellerPageableResponse sellers(@Argument SellerFilter filter, @Argument PageInput page, @Argument SellerSortBy sortBy) {
       return sellerService.getSellers(filter, page, sortBy);
    }
}
