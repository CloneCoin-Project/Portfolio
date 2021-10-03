package com.cloneCoin.portfolio.kafka;

import com.cloneCoin.portfolio.dto.BuySellDto;
import com.cloneCoin.portfolio.dto.CoinDto;
import com.cloneCoin.portfolio.repository.PortfolioRepository;
import com.cloneCoin.portfolio.service.PortfolioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// REST TEMPLETE
// feign : API (동기)

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioService portfolioService;


     @KafkaListener(topics = "user-create-topic") // quickstart_events
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
    @KafkaListener(topics = "buy-sell")
    public void analysisEvent(String kafkaMessage){

//        Map<Object, Object> map = new HashMap<>();
//        ObjectMapper mapper = new ObjectMapper();
//
//        try {
//            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {
//            });
//
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

        // 전체받아오기
        System.out.println(kafkaMessage);
        JSONObject rjson = new JSONObject(kafkaMessage);
        System.out.println(rjson);

        // leaderId
        Long leaderId = rjson.getLong("leaderId");

        // before 부분
        JSONObject jsonBefore = rjson.getJSONObject("before");

        // before안에 totalKRW
        Double beforeTotalKRW = jsonBefore.getDouble("totalKRW");

        // before안에 coins array
        JSONArray jsonBeforeCoins = jsonBefore.getJSONArray("coins");

        List<CoinDto> beforeCoinDtoList = new ArrayList<>();
        for(int i=0; i<jsonBeforeCoins.length(); i++){
            JSONObject coinJson = jsonBeforeCoins.getJSONObject(i);
            String name = coinJson.getString("name");
            Double coinQuantity = coinJson.getDouble("coinQuantity");
            Double avgPrice = coinJson.getDouble("avgPrice");
            CoinDto coinDto = new CoinDto(name, coinQuantity, avgPrice);
            beforeCoinDtoList.add(coinDto);
        }

        // after 부분
        JSONObject jsonAfter = rjson.getJSONObject("after");

        // after안에 totalKRW
        Double afterTotalKRW = jsonAfter.getDouble("totalKRW");

        // after안에 coins array
        JSONArray jsonAfterCoins = jsonAfter.getJSONArray("coins");

        List<CoinDto> afterCoinDtoList = new ArrayList<>();
        for(int i=0; i<jsonAfterCoins.length(); i++){

            JSONObject coinJson = jsonAfterCoins.getJSONObject(i);
            String name = coinJson.getString("name");
            Double coinQuantity = coinJson.getDouble("coinQuantity");
            Double avgPrice = coinJson.getDouble("avgPrice");
            CoinDto coinDto = new CoinDto(name, coinQuantity, avgPrice);
            afterCoinDtoList.add(coinDto);
        }

        BuySellDto buySellDto = new BuySellDto(leaderId, beforeCoinDtoList, afterCoinDtoList, beforeTotalKRW, afterTotalKRW);



        portfolioService.UpdatePortfolio(buySellDto);
    }
}
