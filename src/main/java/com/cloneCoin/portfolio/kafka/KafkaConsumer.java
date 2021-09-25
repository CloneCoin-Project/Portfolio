package com.cloneCoin.portfolio.kafka;

import com.cloneCoin.portfolio.jpa.PortfolioEntity;
import com.cloneCoin.portfolio.jpa.PortfolioRepository;
import com.cloneCoin.portfolio.service.PortfolioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

// REST TEMPLETE
// feign : API (비동기)

@Service
@Slf4j
public class KafkaConsumer {

    PortfolioRepository portfolioRepository;
    PortfolioService portfolioService;

    @Autowired
    public KafkaConsumer(PortfolioRepository portfolioRepository, PortfolioService portfolioService) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioService = portfolioService;
    }

    // postman 으로 topic, message 를 전달하면 produce 해주는 서버를 하나 만들자.

     @KafkaListener(topics = "quickstart-events")
     public void createPortfolio(String kafkaMessage) {
        log.info("Kafka message: =====> " + kafkaMessage);

        Map<Object,Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {
            });

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        log.info("(String)map.get : " + map.get("userId"));
        portfolioService.createPortfolio((String)map.get("userId"));
    }
}
