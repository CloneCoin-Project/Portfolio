package com.cloneCoin.portfolio.service.Impl;

import com.cloneCoin.portfolio.client.WalletReadServiceClient;
import com.cloneCoin.portfolio.domain.Coin;
import com.cloneCoin.portfolio.domain.Copy;
import com.cloneCoin.portfolio.dto.BuySellDto;
import com.cloneCoin.portfolio.dto.CoinDto;
import com.cloneCoin.portfolio.dto.PortfolioDto;
import com.cloneCoin.portfolio.domain.Portfolio;
import com.cloneCoin.portfolio.dto.WalletDto;
import com.cloneCoin.portfolio.repository.CoinRepository;
import com.cloneCoin.portfolio.repository.CopyRepository;
import com.cloneCoin.portfolio.repository.PortfolioRepository;
import com.cloneCoin.portfolio.service.PortfolioService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final CoinRepository coinRepository;

    // 포트폴리오 생성
    @Override
    public void createPortfolio(Long userId) {
        System.out.println("createPorfoilo called!");
        Portfolio portfolio = new Portfolio(userId, 0L, 10000.0);

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

        PortfolioDto portfolioDto = new PortfolioDto(portfolio, walletDtoList);
        return portfolioDto;
    }

    // analysis 매수, 매도 이벤트 발생시
    @Override
    @Transactional
    public void UpdatePortfolio(BuySellDto buySellDto) {
        // 카프카로 받은 leaderId가 copy에 존재한지 찾기
        List<Copy> copyList = copyRepository.findByLeaderId(buySellDto.getLeaderId());

        // 카피한 리더가 있을경우만
        if(!copyList.isEmpty()){
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
            List<CoinDto> beforeCoins = buySellDto.getBeforeCoins();
            Double beforeKRW = buySellDto.getBeforeKRW();
            Double beforeRatio = 0.0;

            List<CoinDto> afterCoins = buySellDto.getAfterCoins();
            Double afterKRW = buySellDto.getAfterKRW();
            Double afterRatio = 0.0;

            for(int i=0; i <= beforeCoins.size(); i++){
                String coinName = beforeCoins.get(i).getName();
                // before
                Double beforeQuantity = beforeCoins.get(i).getCoinQuantity();
                Double beforeAvgPrice = beforeCoins.get(i).getAvgPrice();
                beforeRatio = (beforeQuantity * beforeAvgPrice) / beforeKRW;

                // after
                Double afterQuantity = afterCoins.get(i).getCoinQuantity();
                Double afterAvgPrice = afterCoins.get(i).getAvgPrice();
                afterRatio = (afterQuantity * afterAvgPrice) / beforeKRW;

                // 수량이 다를경우 매수 또는 매도 발생
                if(!beforeQuantity.equals(afterQuantity)){
                    // 매도 발생
                    if(beforeQuantity > afterQuantity){
                        Double resultRatio = beforeRatio - afterRatio;
                        for(int j=0; j<=userList.size(); j++){

                            // user에 Coin이 존재하는지 확인
                            Coin coin = coinRepository.findByUserId(userList.get(j));

                            // coin이 존재하다면 resultRatio 만큼 매도 진행
                            if(coin != null){
                                Portfolio portfolio = portfolioRepository.findByUserId(userList.get(j));

                                // user 가지고 있는 코인 수량에 resultRatio만큼 매도하면 되나?
                                // 매도하면 평단가가 안바뀌니까 user에 수익률 신경안써도 되겠지?
                                Double sellQuantity = coin.getQuantity() * (resultRatio / 100);
                                coin.UpdateSellQuantity(sellQuantity);

                                // 매도하면 user balance는 증가해야함
                                // 매도할때 현재가로 팔아야 하잖음 현재가 어캐 구하지?
                                Double sellBalance = sellQuantity * (현재가)
                                portfolio.PlusBalance(sellBalance);
                           }
                        }
                    }
                    // 매수 발생
                    else{
                        Double resultRatio = afterRatio - beforeRatio;
                        for(int j=0; j<=userList.size(); j++){

                            // user에 Coin이 존재하는지 확인
                            Coin coin = coinRepository.findByUserId(userList.get(j));

                            // 코인이 없다면
                            if(coin == null){

                                Portfolio portfolio = portfolioRepository.findByUserId(userList.get(j));

                                // 구매할 돈 (ex resultRatio가 10퍼 라면 현재 balance에서 코인 10퍼만큼 더사면 되는건가?)
                                Double buyKRW = portfolio.getBalance() * (resultRatio / 100);

                                // 소수점으로 계산하더라도 나머지가 있을 수 있는데 어떻게하지? => 코인을 사고 팔기만해도 돈이 달라질 수 있다.
                                Double buyQuantity = buyKRW / (현재가);

                                // 코인 생성
                                // 코인 처음살때는 현재가가 평단가 이다.
                                Coin newCoin = new Coin(userList.get(j), buySellDto.getLeaderId(), coinName, buyQuantity, 현재가);

                                // 코인 샀으면 balance에서 빼줘야함
                                portfolio.MinusBalance(buyKRW);
                            }
                            // 코인이 존재한다면
                            else{

                                Portfolio portfolio = portfolioRepository.findByUserId(userList.get(j));

                                // 구매할 돈 (ex resultRatio가 10퍼 라면 현재 balance에서 코인 10퍼만큼 더사면 되는건가?)
                                Double buyKRW = portfolio.getBalance() * (resultRatio / 100);

                                // 소수점으로 계산하더라도 나머지가 있을 수 있는데 어떻게하지? => 코인을 사고 팔기만해도 돈이 달라질 수 있다.
                                Double buyQuantity = buyKRW / (현재가);

                                Coin oldCoin = coinRepository.findByCoinName(coinName);

                                // 코인 구매시 수량 증가 => 수량 증가시 평단가 바뀜
                                Double totalQuantity = oldCoin.getQuantity() + buyQuantity;

                                Double oldMoney = oldCoin.getQuantity() * oldCoin.getAvgPrice();
                                Double newMoney = buyQuantity * (현재가);

                                // 평단가 계산식 => 총사용한 돈 / 총 수량
                                Double totalAvgPrice = oldMoney + newMoney / totalQuantity;

                                oldCoin.UpdateBuyQuantity(buyQuantity, totalAvgPrice);

                                // 코인 구매시 balance에서 뺴줘야함
                                portfolio.MinusBalance(buyKRW);
                            }


                        }
                    }
                }
            }



        }



        // 리더가 얼마큼 변했는지 비율 계산
        //before (List coins(name, amount, avgPrice), totalKRW) amount(수량) * avgPrice(평단가) / totalKRW => 비율
        //after (List coins(name, amount, avgPrice), totalKRW) amount(수량) * avgPrice(평단가) / totalKRW => 비율
        // 비율이 얼마나 달라졌는지에 따라 포트폴리오 코인 매수매도 진행

        // 변한 비율만큼 userList 매수, 매도 진행

        // 포트폴리오 총 수익률 변경
    }
}
