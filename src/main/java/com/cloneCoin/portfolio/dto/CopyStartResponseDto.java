package com.cloneCoin.portfolio.dto;

import lombok.Getter;

@Getter
public class CopyStartResponseDto {
    private Long userId;
    private Long leaderId;
    private String leaderName;
    private Double amount;
    private Double copyBalance;

    public CopyStartResponseDto(CopyStartRequestDto copyStartRequestDto, Double balance) {
        this.userId = copyStartRequestDto.getUserId();
        this.leaderId = copyStartRequestDto.getLeaderId();
        this.leaderName = copyStartRequestDto.getLeaderName();
        this.amount = copyStartRequestDto.getAmount();
        this.copyBalance = balance;
    }

    public CopyStartResponseDto() {

    }
}
