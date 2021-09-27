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

    // 포트폴리오 생성
    @Override
    public void createPortfolio(Long userId) {
        System.out.println("createPorfoilo called!");
        Portfolio portfolio = new Portfolio(userId, 0L, 0L);

        System.out.println("Repository save called!");
        portfolioRepository.save(portfolio);

        //PortfolioDto portfolioDto = new ModelMapper().map(portfolio, PortfolioDto.class); modelmapper 사용시 setter 필요

    }

    // 포트폴리오 조회
    @Override
    public PortfolioDto getPortfolioByUserId(Long userId) {
        Portfolio portfolio = portfolioRepository.findByUserId(userId);

        List<Copy> copyList = copyRepository.findByPortfolioId(portfolio.getId());

        List<WalletDto> walletDtoList = new ArrayList<>();

        for (Copy copy : copyList) {            // for(int i=0; i<copyList.size(); i++)
            Long leaderId = copy.getLeaderId(); // Long leaderId = copyList.get(i).getLeaderId();

            // 페인 클라이언트 사용
            WalletDto walletDto = walletReadServiceClient.getWallets(leaderId);

            walletDtoList.add(walletDto);
        }

        // throw new exception 처리하자.
        PortfolioDto portfolioDto = new PortfolioDto(portfolio, walletDtoList);
        return portfolioDto;
    }

    // analysis 매수, 매도 이벤트 발생시
    @Override
    public void UpdatePortfolio(Long leaderId, Object before, Object after) {
        // 카프카로 받은 leaderId가 copy에 존재한지 찾기
        List<Copy> copyList = copyRepository.findByLeaderId(leaderId);

        // userId 담을 리스트
        List<Long> userList = new ArrayList<>();

        // 카피에 있는 리더를 카피한 user 포트폴리오 변경해야함
        for(int i=0; i <= copyList.size(); i++){
            Long userId = copyList.get(i).getUserId();

            userList.add(userId);
        }

        for(int i=0; i<=userList.size(); i++){
            System.out.println(userList.get(i));
        }

        // 카프카로 리스트 넘어오는거 어캐받지?
        // 리더가 얼마큼 변했는지 비율 계산
        //before (List coins(name, amount, avgPrice), totalKRW) amount(수량) * avgPrice(평단가) / totalKRW => 비율
        //after (List coins(name, amount, avgPrice), totalKRW) amount(수량) * avgPrice(평단가) / totalKRW => 비율
        // 비율이 얼마나 달라졌는지에 따라 포트폴리오 코인 매수매도 진행

        // 변한 비율만큼 userList 매수, 매도 진행
    }
}
