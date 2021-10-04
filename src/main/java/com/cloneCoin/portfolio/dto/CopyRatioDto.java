package com.cloneCoin.portfolio.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CopyRatioDto {
    private Long leaderId;
    private String leaderName;
    private Double copyRatio;

    public CopyRatioDto(Long leaderId, String leaderName, Double ratio) {
        this.leaderId = leaderId;
        this.leaderName = leaderName;
        this.copyRatio = ratio;
    }
}
