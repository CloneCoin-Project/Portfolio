package com.cloneCoin.portfolio.service;

import com.cloneCoin.portfolio.dto.BuySellDto;
import com.cloneCoin.portfolio.dto.PortfolioResponseDto;
import com.cloneCoin.portfolio.dto.UserPeriodDto;

public interface PortfolioService {

    void createPortfolio(Long userId);
    // 카피시작
    // 나의포트폴리오 가져오기 등등 추가해야한다.
    PortfolioResponseDto getPortfolioByUserId(Long userId);

    void UpdatePortfolio(BuySellDto buySellDto);

    UserPeriodDto getUserPeriod(Long userId, Long period);
}
