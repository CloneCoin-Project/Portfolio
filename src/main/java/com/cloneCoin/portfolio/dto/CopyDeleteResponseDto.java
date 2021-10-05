package com.cloneCoin.portfolio.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CopyDeleteResponseDto {
    private Long userId;
    private Long leaderId;
    private String leaderName;
    private Double resultBalance;

    public CopyDeleteResponseDto(Long userId, Long leaderId, String leaderName, Double returnKRW) {
        this.userId = userId;
        this.leaderId = leaderId;
        this.leaderName = leaderName;
        this.resultBalance = returnKRW;
    }
}
