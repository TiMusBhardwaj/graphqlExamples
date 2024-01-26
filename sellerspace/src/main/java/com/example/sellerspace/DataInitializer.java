package com.example.sellerspace;

import com.example.sellerspace.entity.MarketplaceEntity;
import com.example.sellerspace.entity.ProducerEntity;
import com.example.sellerspace.entity.SellerEntity;
import com.example.sellerspace.entity.SellerInfoEntity;
import com.example.sellerspace.model.SellerState;
import com.example.sellerspace.repository.MarketplaceRepository;
import com.example.sellerspace.repository.ProducerRepository;
import com.example.sellerspace.repository.SellerInfoRepository;
import com.example.sellerspace.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private MarketplaceRepository marketplaceRepository;

    @Autowired
    private SellerInfoRepository sellerInfoRepository;

    @Autowired
    private SellerRepository sellerRepository;
    private static final Random random = new Random();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        publishMarketPlacedData();
        publishProducerdData();
        publishSellerInfoData();
        publishSellerData();
    }


    private void publishMarketPlacedData() {
        List<String> marketplaceNames = Arrays.asList(
                "Amazon",
                "eBay",
                "Alibaba",
                "Etsy",
                "Rakuten",
                "Walmart Marketplace",
                "Newegg",
                "Shopify",
                "Flipkart",
                "MercadoLibre"
        );


        for (String name : marketplaceNames) {
            MarketplaceEntity marketplace = new MarketplaceEntity();
            marketplace.setId(name.toLowerCase().replaceAll("\\s+", "_")); // Using name as ID after normalization
            marketplace.setDescription(name + " Marketplace"); // Set description or any other fields if needed
            marketplaceRepository.save(marketplace);
        }

    }


    private void publishProducerdData() {

        List<String> producerNames = Arrays.asList(
                "Global Goods Inc.",
                "TechPioneers",
                "FreshFarms Organic",
                "StyleMasters Apparel",
                "HomeComforts Furnishings",
                "AdventureGear Outdoors",
                "NextGen Electronics",
                "LittleLearners Toys",
                "HealthFirst Pharmaceuticals",
                "AutoInnovators Parts"
        );

        // Check if the data already exists to prevent duplicate insertions

        for (String name : producerNames) {
            ProducerEntity producer = new ProducerEntity();
            producer.setName(name);
            producer.setCreatedAt(LocalDateTime.now());
            producerRepository.save(producer);
        }

    }

    public void publishSellerInfoData() {

        List<String> countryNames = List.of(
                "United States", "Canada", "Mexico", "France", "Germany",
                "Japan", "South Korea", "Australia", "Brazil", "India",
                "Russia", "China", "South Africa", "Italy", "Spain"
        );

        List<MarketplaceEntity> marketplaces = new ArrayList<>();
        marketplaceRepository.findAll().forEach(marketplaces::add);

        List<SellerInfoEntity> sellerInfos = IntStream.range(0, 100)
                .mapToObj(i -> {
                    SellerInfoEntity sellerInfo = new SellerInfoEntity();
                    sellerInfo.setName(" Seller_Name_" + i);
                    sellerInfo.setMarketplace(marketplaces.get(random.nextInt(marketplaces.size())));
                    sellerInfo.setUrl("http://example-seller-info.com/" + UUID.randomUUID());
                    sellerInfo.setCountry(countryNames.get(random.nextInt(countryNames.size()))); // Random country name
                    sellerInfo.setExternalId(UUID.randomUUID().toString());
                    return sellerInfo;
                })
                .toList();

        sellerInfoRepository.saveAll(sellerInfos);
    }

    private void publishSellerData() {


        // Fetch the first 10 ProducerEntity instances
        List<ProducerEntity> producers = StreamSupport.stream(producerRepository.findAll().spliterator(), false)
                .limit(10)
                .toList();


        // Fetch all SellerInfoEntity instances
        List<SellerInfoEntity> sellerInfos = sellerInfoRepository.findAll();

        sellerInfos.forEach(sellerInfo -> {
            // For each SellerInfoEntity, create and save 10 SellerEntity instances
            List<SellerEntity> sellers = IntStream.range(0, 10).mapToObj(i -> {
                SellerEntity seller = new SellerEntity();
                seller.setProducer(producers.get(i % producers.size())); // Reuse producers for each SellerInfoEntity
                seller.setSellerInfo(sellerInfo);
                seller.setState(getRandomSellerState()); // Assuming SellerState.REGULAR is an enum value
                return seller;
            }).collect(Collectors.toList());

            sellerRepository.saveAll(sellers); // Save all SellerEntity instances
        });
    }

    private SellerState getRandomSellerState() {
        SellerState[] states = SellerState.values();
        int randomIndex = random.nextInt(states.length);
        return states[randomIndex];
    }

}

