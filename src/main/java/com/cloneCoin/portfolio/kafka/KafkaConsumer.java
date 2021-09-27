package com.cloneCoin.portfolio.kafka;

import com.cloneCoin.portfolio.repository.PortfolioRepository;
import com.cloneCoin.portfolio.service.PortfolioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

// REST TEMPLETE
// feign : API (동기)

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

     @KafkaListener(topics = "transaction") // quickstart_events
     public void createPortfolio(String kafkaMessage) {
        log.info("Kafka message: =====> " + kafkaMessage);

        Map<Object,Long> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Long>>() {
            });

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        log.info("(Long)map.get : " + map.get("userId"));
        System.out.println(map.get("userId"));
        portfolioService.createPortfolio(map.get("userId"));
    }

    // 매수,매도 analysis에서 받기
    @KafkaListener(topics = "transactions")
    public void analysisEvent(String kafkaMessage){

        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {
            });

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        portfolioService.UpdatePortfolio((Long)map.get("leaderId") ,map.get("before"), map.get("after"));
    }
}
