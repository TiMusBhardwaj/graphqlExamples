package com.example.sellerspace.grapghql;

import com.example.sellerspace.entity.SellerEntity;
import com.example.sellerspace.model.request.SellerSortBy;
import com.example.sellerspace.model.response.ProducerSellerState;
import com.example.sellerspace.model.response.Seller;
import com.example.sellerspace.model.response.SellerPageableResponse;
import com.example.sellerspace.service.SellerService;
import graphql.schema.DataFetchingEnvironment;
import org.dataloader.*;
import org.springframework.data.util.Pair;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.execution.BatchLoaderRegistry;
import org.springframework.stereotype.Controller;
import com.example.sellerspace.request.PageInput;
import com.example.sellerspace.request.SellerFilter;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class SellerGraphQLController {

    private final SellerService sellerService;



    public SellerGraphQLController(SellerService sellerService, BatchLoaderRegistry registry) {
        this.sellerService = sellerService;
        registry.forName("producerNameDataLoader").registerMappedBatchLoader(producerNameDataLoaderBiFunction(sellerService));
        registry.forName("producerSellerStatesLoader").registerMappedBatchLoader(producerSellerStatesLoaderBiFunction(sellerService));

    }

    private static BiFunction producerSellerStatesLoaderBiFunction(SellerService sellerService) {
        BiFunction<Set<Pair<String, String>>, BatchLoaderEnvironment, Mono<Map<Pair<String, String>, List<ProducerSellerState>>>> producerSellerStatesLoader = (Set<Pair<String, String>> ids, BatchLoaderEnvironment env) -> {
            return Mono.just(ids.stream().map(pair -> Map.entry(pair, getSellerByExternalIdAndMarketplaceId(sellerService, pair))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        };
        return producerSellerStatesLoader;
    }

    private static BiFunction producerNameDataLoaderBiFunction(SellerService sellerService) {
        BiFunction<Set<UUID>, BatchLoaderEnvironment, Mono<Map<UUID, String>>> producerNameDataLoader = (Set<UUID> ids, BatchLoaderEnvironment env) -> {
            return Mono.just(StreamSupport.stream(sellerService.getProducersByIds(ids).spliterator(), false)
                    .map(v -> Map.entry(v.getId(), v.getName())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        };
        return producerNameDataLoader;
    }

    private static List<ProducerSellerState> getSellerByExternalIdAndMarketplaceId(SellerService sellerService, Pair<String, String> x) {
        List<SellerEntity> sellers =  sellerService.getSellerByExternalIdAndMarketplaceId(x.getFirst(), x.getSecond());
        return sellers.stream()
                .map(seller ->
                        new ProducerSellerState(seller.getProducerId(), null, seller.getState(), seller.getSellerInfo().getId())).toList();


    }

    @QueryMapping
    public SellerPageableResponse sellers(@Argument SellerFilter filter, @Argument PageInput page, @Argument SellerSortBy sortBy) {
        return sellerService.getSellers(filter, page, sortBy);
    }

    @SchemaMapping(typeName = "Seller", field = "producerSellerStates")
    public CompletableFuture<List<ProducerSellerState>> producerSellerStates(Seller sellerResponse, DataFetchingEnvironment env , @Argument SellerFilter filter) {
        DataLoader<Pair<String, String>, List<ProducerSellerState>> dataLoader = env.getDataLoader("producerSellerStatesLoader");
        return dataLoader.load(Pair.of(sellerResponse.getExternalId(), sellerResponse.getMarketplaceId()));

    }

    @SchemaMapping(typeName = "ProducerSellerState", field = "producerName")
    public CompletableFuture<String> producerName(ProducerSellerState producerSellerState, DataFetchingEnvironment env ) {
        DataLoader<UUID, String> dataLoader = env.getDataLoader("producerNameDataLoader");


        return dataLoader.load(producerSellerState.getProducerId());

    }


}

