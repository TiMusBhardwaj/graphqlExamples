package com.example.sellerspace;

import com.example.sellerspace.model.response.SellerPageableResponse;

import graphql.GraphQL;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GraphQLIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testQueryBySellerNameAndValidMarKetPlaceId() {
        String query = "{\"query\":\"query MyQuery { sellers(page: {size: 10, page: 1}, filter: {searchByName: \\\"Name_98\\\", marketplaceIds: \\\"walmart_marketplace\\\"}, sortBy: MARKETPLACE_ID_DESC) { data { producerSellerStates { producerId producerName sellerId sellerState } externalId marketplaceId sellerName } meta { page size totalElements totalPages } }}\"}";

        webTestClient.post().uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(query)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.sellers.data[0].sellerName").isEqualTo("Seller_Name_98")
                .jsonPath("$.data.sellers.data[0].marketplaceId").isEqualTo("walmart_marketplace")
                .jsonPath("$.data.sellers.meta.totalPages").isEqualTo(1)
                .jsonPath("$.data.sellers.meta.size").isEqualTo(1)
                .jsonPath("$.data.sellers.meta.page").isEqualTo(1);
        // Add more assertions as needed
    }


    @Test
    public void testQueryBySellerNameAndInvalidMarKetPlaceId() {
        String query = "{\"query\":\"query MyQuery { sellers(page: {size: 10, page: 1}, filter: {searchByName: \\\"Name_98\\\", marketplaceIds: \\\"walmart_marketplace_invalid\\\"}, sortBy: MARKETPLACE_ID_DESC) { data { producerSellerStates { producerId producerName sellerId sellerState } externalId marketplaceId sellerName } meta { page size totalElements totalPages } }}\"}";

        webTestClient.post().uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(query)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.sellers.meta.totalPages").isEqualTo(0)
                .jsonPath("$.data.sellers.meta.size").isEqualTo(0)
                .jsonPath("$.data.sellers.meta.page").isEqualTo(1);

    }
    @Test
    public void testQueryBySellerNameOrderByMarketPlaceIdDesc() {
        String query = "{\"query\":\"query MyQuery { sellers(page: {size: 11, page: 1}, filter: {searchByName: \\\"Name_9\\\"}, sortBy: MARKETPLACE_ID_DESC) { data { externalId marketplaceId } } }\"}";

        webTestClient.post().uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(query)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.sellers.data[10].externalId").isEqualTo("f1f29e14-d1ba-47e3-bc7e-91c4b8d51036")
                .jsonPath("$.data.sellers.data[10].marketplaceId").isEqualTo("alibaba")
                .jsonPath("$.data.sellers.data[10].sellerName").doesNotExist();
    }

    @Test
    public void testSelectSellerNameQueryBySellerNameOrderByMarketPlaceIdDesc() {
        String query = "{\"query\":\"query MyQuery { sellers(page: {size: 11, page: 1}, filter: {searchByName: \\\"Name_9\\\"}, sortBy: MARKETPLACE_ID_DESC) { data { sellerName externalId marketplaceId } } }\"}";

        webTestClient.post().uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(query)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.sellers.data[10].sellerName").isEqualTo("Seller_Name_90");
    }

    @Test
    public void testSelectSellerNameQueryBySellerNameOrderByNameAsc() {
        String query = "{\"query\":\"query MyQuery { sellers(page: {size: 11, page: 1}, filter: {searchByName: \\\"Name_1\\\"}, sortBy: NAME_ASC) { data { sellerName externalId marketplaceId } } }\"}";

        webTestClient.post().uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(query)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.sellers.data[10].sellerName").isEqualTo("Seller_Name_19");
    }

    @Test
    public void testSelectSellerNameQueryBySellerNameOrderBySellerInfoExternalIdAsc() {
        String query = "{\"query\":\"query MyQuery { sellers(page: {size: 11, page: 1}, filter: {searchByName: \\\"Name_2\\\"}, sortBy: SELLER_INFO_EXTERNAL_ID_ASC) { data { sellerName externalId marketplaceId } } }\"}";

        webTestClient.post().uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(query)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.sellers.data[10].sellerName").isEqualTo("Seller_Name_28");
    }

    @Test
    public void testBySellerNameAndProducerIdAndMarketPlaceIdOrderByNameAsc() {
        String query = "{\"query\":\"query MyQuery { sellers(page: {size: 10, page: 1}, filter: {searchByName: \\\"8\\\", marketplaceIds: [\\\"ebay\\\", \\\"etsy\\\"], producerIds: \\\"78d3bfae-a058-4729-81ff-bf57e3c16ad0\\\"}, sortBy: NAME_ASC) { data { marketplaceId producerSellerStates { producerId producerName sellerId sellerState } externalId } meta { page size totalElements totalPages } } }\"}";

        webTestClient.post().uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(query)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.sellers.meta.page").isEqualTo(1)
                .jsonPath("$.data.sellers.meta.size").isEqualTo(2)
                .jsonPath("$.data.sellers.meta.totalElements").isEqualTo(2)
                .jsonPath("$.data.sellers.data[1].marketplaceId").isEqualTo("etsy")
                .jsonPath("$.data.sellers.data[1].externalId").isEqualTo("c197edfd-429b-4e1f-9865-1e62059678c7")
                .jsonPath("$.data.sellers.data[0].producerSellerStates[0].producerId").isEqualTo("a150d2a4-c6c6-4aef-a97b-749416c28918")

        ;

    }


}


