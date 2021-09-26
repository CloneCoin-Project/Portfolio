package com.cloneCoin.portfolio.service;

import com.cloneCoin.portfolio.dto.PortfolioDto;

public interface PortfolioService {

    PortfolioDto createPortfolio(String userId);
    // 카피시작
    // 나의포트폴리오 가져오기 등등 추가해야한다.
    PortfolioDto getPortfolioByUserId(String userId);
}
