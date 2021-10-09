package com.cloneCoin.portfolio.service.Impl;

import com.cloneCoin.portfolio.client.BithumbOpenApi;
import com.cloneCoin.portfolio.client.WalletReadServiceClient;
import com.cloneCoin.portfolio.domain.Coin;
import com.cloneCoin.portfolio.domain.Copy;
import com.cloneCoin.portfolio.domain.Portfolio;
import com.cloneCoin.portfolio.dto.*;
import com.cloneCoin.portfolio.repository.CoinRepository;
import com.cloneCoin.portfolio.repository.CopyRepository;
import com.cloneCoin.portfolio.repository.PortfolioRepository;
import com.cloneCoin.portfolio.service.PortfolioService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final BithumbOpenApi bithumbOpenApi;

    // 포트폴리오 생성
    @Override
    public void createPortfolio(Long userId) {
        System.out.println("createPorfoilo called!");
        Portfolio portfolio = new Portfolio(userId,10000000.0);

        System.out.println("Repository save called!");
        portfolioRepository.save(portfolio);


//        TickerDto tickerDto = bithumbServiceClient.getTiker("BTC", "KRW");
//        System.out.println(tickerDto.getClosing_price());


        //PortfolioDto portfolioDto = new ModelMapper().map(portfolio, PortfolioDto.class); modelmapper 사용시 setter 필요

    }

    // 포트폴리오 조회
    @Override
    public PortfolioResponseDto getPortfolioByUserId(Long userId) {
        Portfolio portfolio = portfolioRepository.findByUserId(userId);

        List<Copy> copyList = copyRepository.findByPortfolioId(portfolio.getId());

        List<WalletDto> walletDtoList = walletReadServiceClient.getWallets();

        List<WalletDto> walletReturnList = new ArrayList<>();

        for (Copy copy : copyList) {
            Long leaderId = copy.getLeaderId();
            for (WalletDto walletDto : walletDtoList) {
                if (leaderId.equals(walletDto.getLeaderId())) {
                    walletReturnList.add(walletDto);
                    break;
                }
            }
        }
        return new PortfolioResponseDto(portfolio.getBalance(), walletReturnList);

//        PortfolioDto portfolioDto = new PortfolioDto(portfolio, walletDtoList);
//        return portfolioDto;
    }

    public Double cal(Double x){
        return Math.round(x*1000) / 1000.0;
    }

    // analysis 매수, 매도 이벤트 발생시
    // 매수, 매도 마이너스 예외처리
    // 매수할때 카피잔액이 없으면 매수안되게
    @Override
    @Transactional
    public void UpdatePortfolio(BuySellDto buySellDto) {

        // 카프카로 받은 leaderId가 copy에 존재한지 찾기
        List<Copy> copyList = copyRepository.findByLeaderId(buySellDto.getLeaderId());

        // 카피한 리더가 있을경우만 실행
        if(!copyList.isEmpty()){
            // userId 담을 리스트
            List<Long> userList = new ArrayList<>();
            // 카피에 있는 리더를 카피한 user 포트폴리오 변경해야함
            for(int i=0; i < copyList.size(); i++){

                Long userId = copyList.get(i).getUserId();
                userList.add(userId);
            }

            List<CoinDto> beforeCoins = buySellDto.getBeforeCoins();
            Double beforeKRW = buySellDto.getBeforeKRW();
            Double beforeRatio = 0.0;

            List<CoinDto> afterCoins = buySellDto.getAfterCoins();
            Double afterKRW = buySellDto.getAfterKRW();
            Double afterRatio = 0.0;

            // 계산할때 소숫점 2번째 자리까지만 계산하는게 좋을듯

            // 카프카로 넘어온 코인 매수매도 for문
            for(int i=0; i < beforeCoins.size(); i++){

                String coinName = beforeCoins.get(i).getName();

                // 코인에 현재가
                Double currentPrice = bithumbOpenApi.TickerApi(coinName);

                // before
                Double beforeQuantity = beforeCoins.get(i).getCoinQuantity();
                Double beforeAvgPrice = beforeCoins.get(i).getAvgPrice();
                Double beforeTotalMoney = cal((beforeQuantity * beforeAvgPrice) + beforeKRW);
                beforeRatio = cal((beforeQuantity * beforeAvgPrice) / beforeTotalMoney);

                // after
                Double afterQuantity = afterCoins.get(i).getCoinQuantity();
                Double afterAvgPrice = afterCoins.get(i).getAvgPrice();
                Double afterTotalMoney = cal((afterQuantity * afterAvgPrice) + afterKRW);
                afterRatio = cal((afterQuantity * afterAvgPrice) / afterTotalMoney);

                // 수량이 다를경우 매수 또는 매도 발생
                if(!(beforeQuantity.equals(afterQuantity))){
                    // 매도 발생
                    if(beforeQuantity > afterQuantity){
                        Double resultRatio = beforeRatio - afterRatio;
                        Double resultQuantity = (beforeQuantity - afterQuantity) / beforeQuantity;
                        for(int j=0; j< userList.size(); j++){

                            // user에 Coin이 존재하는지 확인
                            Coin oldCoin = coinRepository.findByUserIdAndCoinName(userList.get(j), coinName);

                            Copy copy = copyRepository.findByUserIdAndLeaderId(userList.get(j), buySellDto.getLeaderId());

                            // coin이 존재하다면 resultRatio 만큼 매도 진행
                            if(oldCoin != null){
                                Portfolio portfolio = portfolioRepository.findByUserId(userList.get(j));

                                // user 가지고 있는 코인 수량에 resultRatio만큼 매도하면 되나?
                                // 20% -> 10% 바뀜 : 해당 코인은 절반을 팔은거임 포트폴리오도 해당 코인 절반을 팔아야함
                                // 매도에는 resultRatio를 쓰는것 보다 코인 수량으로 계산해서 매도하는게 좋을듯

                                Double sellQuantity = oldCoin.getQuantity() * resultQuantity;

                                Double totalQuantity = oldCoin.getQuantity() - sellQuantity;

                                // 수량을 다 매도했을경우 데이터베이스에서 삭제
                                if(totalQuantity <= 0){
                                    coinRepository.delete(oldCoin);
                                }

                                oldCoin.UpdateSellQuantity(totalQuantity);

                                // 매도하면 copy balance는 증가해야함
                                Double sellBalance = cal(sellQuantity * currentPrice); //(현재가)
                                copy.CopyPlusBalance(sellBalance);
                                //portfolio.PlusBalance(sellBalance);
                           }
                        }
                    }
                    // 매수 발생
                    else{
                        Double resultRatio = cal(afterRatio - beforeRatio);
                        System.out.println("========================");
                        System.out.println("afterRatio : "+afterRatio);
                        System.out.println("beforeRatio : "+beforeRatio);
                        System.out.println("========================");
                        Double resultQuantity = (afterQuantity - beforeQuantity) / beforeQuantity;
                        for(int j=0; j< userList.size(); j++){

                            // user에 Coin이 존재하는지 확인
                            Coin oldCoin = coinRepository.findByUserIdAndCoinName(userList.get(j), coinName);
                            Copy copy = copyRepository.findByUserIdAndLeaderId(userList.get(j), buySellDto.getLeaderId());

                            // 코인이 없다면
                            if(oldCoin == null){

                                Portfolio portfolio = portfolioRepository.findByUserId(userList.get(j));

                                // 구매할 돈 (ex resultRatio가 10퍼 라면 현재 총 투자금액에서 코인 10퍼만큼 더사면 되는건가?)
                                Double buyKRW = cal(copy.getTotalInvestAmout() * resultRatio);

                                // 카피 잔액보다 살 돈이 더 크다면 남은 잔액만큼만 산다.
                                if(copy.getInvestBalance() - buyKRW < 0){
                                    buyKRW = copy.getInvestBalance();

                                    // 소수점으로 계산하더라도 나머지가 있을 수 있는데 어떻게하지? => 코인을 사고 팔기만해도 돈이 달라질 수 있다.
                                    Double buyQuantity = buyKRW / currentPrice;// (현재가);

                                    // 코인 생성
                                    // 코인 처음살때는 현재가가 평단가 이다.
                                    Coin newCoin = new Coin(userList.get(j), buySellDto.getLeaderId(), coinName, buyQuantity, currentPrice, copy);

                                    // 코인저장
                                    coinRepository.save(newCoin);

                                    // 코인 샀으면 balance에서 빼줘야함 => copy 투자금액에서 뻄

                                    copy.CopyMinusBalance(buyKRW);
                                    //portfolio.MinusBalance(buyKRW);
                                }
                                else if(copy.getInvestBalance() == 0){
                                    // 잔액이 0원이라면 할게 없음
                                }
                                else{
                                    // 소수점으로 계산하더라도 나머지가 있을 수 있는데 어떻게하지? => 코인을 사고 팔기만해도 돈이 달라질 수 있다.
                                    Double buyQuantity = buyKRW / currentPrice;// (현재가);


                                    // 코인 생성
                                    // 코인 처음살때는 현재가가 평단가 이다.
                                    Coin newCoin = new Coin(userList.get(j), buySellDto.getLeaderId(), coinName, buyQuantity, currentPrice, copy);

                                    // 코인저장
                                    coinRepository.save(newCoin);

                                    // 코인 샀으면 balance에서 빼줘야함 => copy 투자금액에서 뻄

                                    copy.CopyMinusBalance(buyKRW);
                                    //portfolio.MinusBalance(buyKRW);
                                }


                            }
                            // 코인이 존재한다면
                            // 코인이 존재할떄는 잔액보다 큰돈은 아예 안사도록 했는데
                            // 어떻게 마이너스가 뜬거지?
                            else{

                                Portfolio portfolio = portfolioRepository.findByUserId(userList.get(j));

                                // 구매할 돈 (ex resultRatio가 10퍼 라면 현재 copy balance에서 코인 10퍼만큼 더사면 되는건가?)
                                // 총 투자금액에 10퍼만큼 더사면 되는건가?
                                Double buyKRW = cal(copy.getTotalInvestAmout() * resultRatio);

                                // 남은 투자 잔액이 더 많을경우에만 코인 매수
                                if(copy.getInvestBalance() > buyKRW){

                                    // 소수점으로 계산하더라도 나머지가 있을 수 있는데 어떻게하지? => 코인을 사고 팔기만해도 돈이 달라질 수 있다.
                                    Double buyQuantity = buyKRW / currentPrice;//(현재가);

                                    // 코인 구매시 수량 증가 => 수량 증가시 평단가 바뀜
                                    Double totalQuantity = oldCoin.getQuantity() + buyQuantity;

                                    Double oldMoney = cal(oldCoin.getQuantity() * oldCoin.getAvgPrice());
                                    Double newMoney = cal(buyQuantity * currentPrice);//(현재가);

                                    // 평단가 계산식 => 총사용한 돈 / 총 수량
                                    Double totalAvgPrice = cal((oldMoney + newMoney) / totalQuantity);

                                    Double currentQuantity = oldCoin.UpdateBuyQuantity(totalQuantity, totalAvgPrice);


                                    // (현재수량 * 평단가)) => 기존 금액
                                    Double usuallyResult = cal(currentQuantity * totalAvgPrice);

                                    // ((현재수량 * 현재가) => 변동 금액
                                    Double changeResult = cal(currentQuantity * currentPrice);

                                    // 이 수익률은 매수매도 일어났을경우 업데이트가 되기 때문에 의미가 없다 => 현재가가 바뀔때마다 수익률은 바뀌어야한다.
                                    // 수익률 계산식 : ((현재수량 * 현재가) - (현재수량 * 평단가)) / (현재수량 * 평단가) * 100
                                    // 이건 쓸떄없는것 같다
                                    Double coinRevenue = cal((changeResult - usuallyResult) / usuallyResult * 100);

                                    // 웹소켓을 사용하여 코인매수매도가 있을때마다 userId, leaderId, 코인이름, 코인수량, 코인평단가를 보내주면 되나?

                                    // 코인 샀으면 balance에서 빼줘야함 => copy 투자금액에서 뻄
                                    copy.CopyMinusBalance(buyKRW);
                                    //portfolio.MinusBalance(buyKRW);
                                }

                            }


                        }
                    }
                }
            }

            // 카프카로 넘어온 모든 코인 매수매도 끝나면 portfolio 총 수익률 변경해줘야함


        }



        // 리더가 얼마큼 변했는지 비율 계산
        //before (List coins(name, amount, avgPrice), totalKRW) amount(수량) * avgPrice(평단가) / totalKRW => 비율
        //after (List coins(name, amount, avgPrice), totalKRW) amount(수량) * avgPrice(평단가) / totalKRW => 비율
        // 비율이 얼마나 달라졌는지에 따라 포트폴리오 코인 매수매도 진행

        // 변한 비율만큼 userList 매수, 매도 진행

        // 포트폴리오 총 수익률 변경
    }

    // 리더의 (1, 7, 30) 기간별 수익률 제공
    public UserPeriodDto getUserPeriod(Long userId, Long period) {

        UserPeriodDto userPeriodDTO = new UserPeriodDto();
        userPeriodDTO.setUserId(userId);

        List<UserPeriodContent> userPeriodContentList = new ArrayList<>();


        Portfolio portfolio = portfolioRepository.findByUserId(userId);

        portfolio.getProfits().stream().forEach(profit -> {
            UserPeriodContent leaderPeriodContent = new UserPeriodContent();
            leaderPeriodContent.setProfit(profit.getProfit());
            leaderPeriodContent.setLocalDate(profit.getLocalDate());
            userPeriodContentList.add(leaderPeriodContent);
        });



        if (period == 1) {
            userPeriodDTO.setUserPeriodContentList(userPeriodContentList);
        }
        if (period == 7) {
            List<UserPeriodContent> leaderPeriodContentList_7 = getLeaderPeriodContentList(userPeriodContentList, 7);
            userPeriodDTO.setUserPeriodContentList(leaderPeriodContentList_7);
        }
        if (period == 30) {
            List<UserPeriodContent> leaderPeriodContentList_30 = getLeaderPeriodContentList(userPeriodContentList, 30);
            userPeriodDTO.setUserPeriodContentList(leaderPeriodContentList_30);
        }

        return userPeriodDTO;
    }

    // 1일 기준 수익률 -> 원하는 기간별 수익률
    public List<UserPeriodContent> getLeaderPeriodContentList(List<UserPeriodContent> leaderPeriodContentList, long period) {
        List<UserPeriodContent> leaderPeriodContentList2 = new ArrayList<>();
        int count = 0;
        double sum = 0;
        for (UserPeriodContent leaderPeriodContent : leaderPeriodContentList) {
            sum += leaderPeriodContent.getProfit();
            count++;
            if (count == period) {
                UserPeriodContent leaderPeriodContent2 = new UserPeriodContent();
                leaderPeriodContent2.setProfit(sum / 7);
                leaderPeriodContent2.setLocalDate(leaderPeriodContent.getLocalDate());
                leaderPeriodContentList2.add(leaderPeriodContent2);

                sum=0;
                count=0;
            }
        }
        if (count != 0) {
            UserPeriodContent leaderPeriodContent2 = new UserPeriodContent();
            leaderPeriodContent2.setProfit(sum / count);
            leaderPeriodContent2.setLocalDate(LocalDate.now());
            leaderPeriodContentList2.add(leaderPeriodContent2);
        }

        leaderPeriodContentList2.stream().forEach(leaderPeriodContent -> System.out.println(leaderPeriodContent.toString()));
        System.out.println("\n\n");
        return leaderPeriodContentList2;
    }

}
