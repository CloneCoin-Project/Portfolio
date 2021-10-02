package com.cloneCoin.portfolio.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BuySellDto {
    private Long leaderId;
    private List<CoinDto> beforeCoins;
    private List<CoinDto> afterCoins;
    private Double beforeKRW;
    private Double afterKRW;

    public BuySellDto(Long leaderId, List<CoinDto> beforeCoinDtoList, List<CoinDto> afterCoinDtoList, Double beforeTotalKRW, Double afterTotalKRW) {
        this.leaderId = leaderId;
        this.beforeCoins = beforeCoinDtoList;
        this.afterCoins = afterCoinDtoList;
        this.beforeKRW = beforeTotalKRW;
        this.afterKRW = afterTotalKRW;
    }
}
