package com.cloneCoin.portfolio.dto;

import com.cloneCoin.portfolio.domain.Portfolio;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PortfolioDto {
    private Long userId;

    private List<WalletDto> walletDtoList;

    public PortfolioDto(Portfolio portfolio, List<WalletDto> walletDtoList) {
        this.userId = portfolio.getUserId();
        this.walletDtoList = walletDtoList;
    }
}
