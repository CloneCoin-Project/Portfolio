package com.cloneCoin.portfolio.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CopyPutResponseDto {
    private Long userId;
    private Long leaderId;
    private String leaderName;
    private Double amount;
    private String type;
    private Double resultBalance;

    public CopyPutResponseDto(CopyPutRequestDto copyPutRequestDto, String leaderName, Double balance) {
        this.userId = copyPutRequestDto.getUserId();
        this.leaderId = copyPutRequestDto.getLeaderId();
        this.leaderName = leaderName;
        this.amount = copyPutRequestDto.getAmount();
        this.type = copyPutRequestDto.getType();
        this.resultBalance = balance;
    }
}
