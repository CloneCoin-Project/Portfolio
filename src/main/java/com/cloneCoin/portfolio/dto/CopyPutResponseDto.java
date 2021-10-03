package com.cloneCoin.portfolio.dto;

import lombok.Getter;

@Getter
public class CopyPutResponseDto {
    private Long userId;
    private Long leaderId;
    private Double amount;
    private Double resultBalance;
    private String type;
}
