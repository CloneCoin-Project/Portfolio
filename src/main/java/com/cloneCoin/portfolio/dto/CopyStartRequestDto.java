package com.cloneCoin.portfolio.dto;

import lombok.Getter;

@Getter
public class CopyStartRequestDto {
    private Long userId;
    private Long leaderId;
    private Long amount;
}
