package com.cloneCoin.portfolio.service.Impl;

import com.cloneCoin.portfolio.client.WalletReadServiceClient;
import com.cloneCoin.portfolio.domain.Copy;
import com.cloneCoin.portfolio.dto.PortfolioDto;
import com.cloneCoin.portfolio.domain.Portfolio;
import com.cloneCoin.portfolio.dto.WalletDto;
import com.cloneCoin.portfolio.repository.CopyRepository;
import com.cloneCoin.portfolio.repository.PortfolioRepository;
import com.cloneCoin.portfolio.service.PortfolioService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final CopyRepository copyRepository;
    private final WalletReadServiceClient walletReadServiceClient;


    @Override
    public void createPortfolio(Long userId) {
        System.out.println("createPorfoilo called!");
        Portfolio portfolio = new Portfolio(userId, 0L);

        System.out.println("Repository save called!");
        portfolioRepository.save(portfolio);

        PortfolioDto portfolioDto = new ModelMapper().map(portfolio, PortfolioDto.class);

        System.out.println("createPortfolio return dto");
        System.out.println(portfolioDto);
    }

    @Override
    public PortfolioDto getPortfolioByUserId(Long userId) {
        Portfolio portfolio = portfolioRepository.findByUserId(userId);

        List<Copy> copyList = copyRepository.findByPortfolioId(portfolio.getId());

        List<WalletDto> walletDtoList = new ArrayList<>();

        for(int i=0; i<copyList.size(); i++){
            Long leaderId = copyList.get(i).getLeaderId();

            // 페인 클라이언트 사용
            WalletDto walletDto = walletReadServiceClient.getWallets(leaderId);

            walletDtoList.add(walletDto);
        }

        // throw new exception 처리하자.
        PortfolioDto portfolioDto = new PortfolioDto(portfolio, walletDtoList);
        return portfolioDto;
    }
}
