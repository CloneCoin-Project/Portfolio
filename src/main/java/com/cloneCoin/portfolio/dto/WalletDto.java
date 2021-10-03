package com.cloneCoin.portfolio.dto;

import lombok.Getter;

@Getter
public class WalletDto {
    private Long leaderId;
    private String leaderName;
    private Double all;
    private Double best;
    private Double worst;
}
