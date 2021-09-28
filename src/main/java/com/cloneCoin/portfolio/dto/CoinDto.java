package com.cloneCoin.portfolio.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CoinDto {
    private String name;
    private Double coinQuantity;
    private Double avgPrice; // 평단가

    public CoinDto(String name, Double coinQuantity, Double avgPrice) {
        this.name = name;
        this.coinQuantity = coinQuantity;
        this.avgPrice = avgPrice;
    }
}
