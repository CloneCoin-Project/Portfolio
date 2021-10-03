package com.cloneCoin.portfolio.dto;

import lombok.Getter;

@Getter
public class CopyDeleteResponseDto {
    private Long userId;
    private Long leaderId;
    private Double stopBalance;

    public CopyDeleteResponseDto(Long userId, Long leaderId, Double returnKRW) {
        this.userId = userId;
        this.leaderId = leaderId;
        this.stopBalance = returnKRW;
    }
}
