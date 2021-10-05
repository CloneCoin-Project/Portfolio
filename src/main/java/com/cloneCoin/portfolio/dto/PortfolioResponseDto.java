package com.cloneCoin.portfolio.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PortfolioResponseDto {
    private Double totalMoney;
    private Double balance;
    private List<WalletDto> leaders;

    public PortfolioResponseDto(Double balance, List<WalletDto> walletReturnList) {
        this.balance = balance;
        this.leaders = walletReturnList;
    }
}
