package com.cloneCoin.portfolio.dto;

import lombok.Getter;

@Getter
public class CopyPutRequestDto {
    private Long userid;
    private Long leaderId;
    private Long amount;
    private String type;
}
