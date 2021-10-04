package com.cloneCoin.portfolio.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CoinResponseDto {
    private Long userId;
    private Long leaderId;
    private String leaderName;
    private String name;
    private Double coinQuantity;
    private Double avgPrice; // 평단가

    public CoinResponseDto(Long userId, Long leaderId, String leaderName, String coinName, Double coinAvg, Double coinQuantity) {
        this.userId = userId;
        this.leaderId = leaderId;
        this.leaderName = leaderName;
        this.name = coinName;
        this.avgPrice = coinAvg;
        this.coinQuantity = coinQuantity;
    }
}
