package com.cloneCoin.portfolio.service.Impl;

import com.cloneCoin.portfolio.dto.PortfolioDto;
import com.cloneCoin.portfolio.jpa.PortfolioEntity;
import com.cloneCoin.portfolio.jpa.PortfolioRepository;
import com.cloneCoin.portfolio.service.PortfolioService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Data
@Slf4j
@Service
public class PortfolioServiceImpl implements PortfolioService {
    PortfolioRepository portfolioRepository;

    @Autowired
    public PortfolioServiceImpl(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public PortfolioDto createPortfolio(String userId) {
        System.out.println("createPorfoilo called!");
        PortfolioEntity portfolioEntity = new PortfolioEntity();
        portfolioEntity.setUserId(userId);
        portfolioEntity.setPortfolioProfit(0L);

        System.out.println("Repository save called!");
        portfolioRepository.save(portfolioEntity);

        PortfolioDto portfolioDto = new ModelMapper().map(portfolioEntity, PortfolioDto.class);

        System.out.println("createPortfolio return dto");
        System.out.println(portfolioDto);
        return portfolioDto;
    }

    @Override
    public PortfolioDto getPortfolioByUserId(String userId) {
        PortfolioEntity portfolioEntity = portfolioRepository.findByUserId(userId);

        // throw new exception 처리하자.

        PortfolioDto portfolioDto = new ModelMapper().map(portfolioEntity, PortfolioDto.class);
        return portfolioDto;
    }
}
