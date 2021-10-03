package com.cloneCoin.portfolio.dto;

import lombok.Getter;

@Getter
public class CoinResponseDto {
    private Long userId;
    private Long leaderId;
    private String name;
    private Double coinQuantity;
    private Double avgPrice; // 평단가

    public CoinResponseDto(Long userId, Long leaderId, String coinName, Double coinAvg, Double coinQuantity) {
        this.userId = userId;
        this.leaderId = leaderId;
        this.name = coinName;
        this.avgPrice = coinAvg;
        this.coinQuantity = coinQuantity;
    }
}
