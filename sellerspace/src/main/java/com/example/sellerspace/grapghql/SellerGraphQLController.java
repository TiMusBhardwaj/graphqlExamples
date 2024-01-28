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
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Controller
public class SellerGraphQLController {

    private final SellerService sellerService;

    /**
     * Constructor for SellerGraphQLController.
     * Initializes the SellerService and registers batch loaders in the BatchLoaderRegistry.
     *
     * @param sellerService The service for handling seller-related operations.
     * @param registry      The registry for registering GraphQL batch loaders.
     */
    public SellerGraphQLController(SellerService sellerService, BatchLoaderRegistry registry) {
        this.sellerService = sellerService;
        registry.forName("producerNameDataLoader").registerMappedBatchLoader(producerNameDataLoaderBiFunction(sellerService));
        registry.forName("producerSellerStatesLoader").registerMappedBatchLoader(producerSellerStatesLoaderBiFunction(sellerService));
        registry.forName("producerSellerStatesInfoIdLoader").registerMappedBatchLoader(producerSellerStatesInfoIdLoaderBiFunction(sellerService));

    }

    /**
     * GraphQL query mapping for fetching a paginated list of sellers based on filters, page input, and sorting criteria.
     *
     * @param filter Filter criteria for sellers.
     * @param page   Pagination information.
     * @param sortBy Sorting criteria.
     * @return SellerPageableResponse containing the list of sellers and pagination metadata.
     */
    @QueryMapping
    public SellerPageableResponse sellers(@Argument SellerFilter filter, @Argument PageInput page, @Argument SellerSortBy sortBy) {
        return sellerService.getSellers(filter, page, sortBy);
    }

    /**
     * Schema mapping for resolving the 'producerSellerStates' field for a 'Seller'.
     * Uses DataLoader to batch load seller states efficiently.
     *
     * @param sellerResponse The seller for whom to resolve the states.
     * @param env            The GraphQL data-fetching environment.
     * @return A CompletableFuture of a list of ProducerSellerState.
     */
    @SchemaMapping(typeName = "Seller", field = "producerSellerStates")
    public CompletableFuture<List<ProducerSellerState>> producerSellerStates(Seller sellerResponse, DataFetchingEnvironment env) {
        DataLoader<UUID, List<ProducerSellerState>> dataLoader = env.getDataLoader("producerSellerStatesInfoIdLoader");
        return dataLoader.load(sellerResponse.getId());

    }

    /**
     * Schema mapping for resolving the 'producerName' field for a 'ProducerSellerState'.
     * Uses DataLoader to batch load producer names efficiently.
     *
     * @param producerSellerState The producer seller state for which to resolve the name.
     * @param env                 The GraphQL data-fetching environment.
     * @return A CompletableFuture of the producer name.
     */
    @SchemaMapping(typeName = "ProducerSellerState", field = "producerName")
    public CompletableFuture<String> producerName(ProducerSellerState producerSellerState, DataFetchingEnvironment env) {
        DataLoader<UUID, String> dataLoader = env.getDataLoader("producerNameDataLoader");


        return dataLoader.load(producerSellerState.getProducerId());

    }




    /**
     * Creates a BiFunction for a DataLoader to batch load producer seller states based on a pair of external ID and marketplace ID.
     *
     * @param sellerService The service used to fetch seller data.
     * @return A BiFunction for batch loading producer seller states.
     */
    private static BiFunction producerSellerStatesLoaderBiFunction(SellerService sellerService) {
        BiFunction<Set<Pair<String, String>>, BatchLoaderEnvironment, Mono<Map<Pair<String, String>, List<ProducerSellerState>>>> producerSellerStatesLoader = (Set<Pair<String, String>> ids, BatchLoaderEnvironment env) -> {
            // Stream processing to batch load producer seller states based on provided pairs of external ID and marketplace ID
            return Mono.just(ids.stream()
                    .map(pair -> Map.entry(pair, getSellerByExternalIdAndMarketplaceId(sellerService, pair)))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        };
        return producerSellerStatesLoader;
    }

    /**
     * Creates a BiFunction for a DataLoader to batch load producer seller states by seller info IDs.
     *
     * @param sellerService The service used to fetch seller data.
     * @return A BiFunction for batch loading producer seller states by seller info IDs.
     */
    private static BiFunction producerSellerStatesInfoIdLoaderBiFunction(SellerService sellerService) {
        BiFunction<Set<UUID>, BatchLoaderEnvironment, Mono<Map<UUID, List<ProducerSellerState>>>> producerSellerStatesInfoIdLoader = (Set<UUID> ids, BatchLoaderEnvironment env) -> {
            // Stream processing to batch load producer seller states based on a set of seller info IDs
            Map<UUID, List<ProducerSellerState>> map = sellerService.getSellersByInfoIds(ids).stream()
                    .map(seller -> Map.entry(seller.getSellerInfo().getId(), new ProducerSellerState(seller.getProducerId(), null, seller.getState(), seller.getSellerInfo().getId())))
                    .collect(Collectors.groupingBy(Map.Entry::getKey,
                            Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

            return Mono.just(map);
        };
        return producerSellerStatesInfoIdLoader;
    }

    /**
     * Creates a BiFunction for a DataLoader to batch load producer names by their IDs.
     *
     * @param sellerService The service used to fetch producer data.
     * @return A BiFunction for batch loading producer names by their IDs.
     */
    private static BiFunction producerNameDataLoaderBiFunction(SellerService sellerService) {
        BiFunction<Set<UUID>, BatchLoaderEnvironment, Mono<Map<UUID, String>>> producerNameDataLoader = (Set<UUID> ids, BatchLoaderEnvironment env) -> {
            // Stream processing to batch load producer names based on a set of producer IDs
            return Mono.just(StreamSupport.stream(sellerService.getProducersByIds(ids).spliterator(), false)
                    .map(v -> Map.entry(v.getId(), v.getName())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        };
        return producerNameDataLoader;
    }

    /**
     * Helper method to get a list of ProducerSellerState by an external ID and marketplace ID.
     *
     * @param sellerService The service used to fetch seller data.
     * @param Pair             The pair of external ID and marketplace ID.
     * @return A list of ProducerSellerState.
     */
    private static List<ProducerSellerState> getSellerByExternalIdAndMarketplaceId(SellerService sellerService, Pair<String, String> x) {
        // Fetching sellers based on external ID and marketplace ID and converting them to ProducerSellerState
        List<SellerEntity> sellers = sellerService.getSellerByExternalIdAndMarketplaceId(x.getFirst(), x.getSecond());
        return sellers.stream()
                .map(seller ->
                        new ProducerSellerState(seller.getProducerId(), null, seller.getState(), seller.getSellerInfo().getId())).toList();
    }



}

