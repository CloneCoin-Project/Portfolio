package com.cloneCoin.portfolio.dto;

import lombok.Getter;

@Getter
public class CopyPutRequestDto {
    private Long userId;
    private Long leaderId;
    private Double amount;
    private String type;
}
