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
import com.cloneCoin.portfolio.dto.CopyRatioDto;
import com.cloneCoin.portfolio.service.CopyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CopyServiceImpl implements CopyService {

    private final CopyRepository copyRepository;
    private final PortfolioRepository portfolioRepository;
    private final WalletReadServiceClient walletReadServiceClient;
    private final CoinRepository coinRepository;
    private final BithumbOpenApi bithumbOpenApi;

    // 카피수 조회
    @Override
    public CopyAmountDto copyGet(Long leaderId) {
        List<Copy> copy = copyRepository.findByLeaderId(leaderId);
        return new CopyAmountDto(copy.size());
    }

    // 카피 비율
    @Override
    public List<CopyRatioDto> copyRatio(Long userId) {
        List<Copy> copyList = copyRepository.findByUserId(userId);

        Double copyMoney =0.0;

        for(int i=0; i<copyList.size(); i++){
            copyMoney += copyList.get(i).getTotalInvestAmout();
        }

        List<CopyRatioDto> copyRatioDtos = new ArrayList<>();

        for(int i=0; i<copyList.size(); i++){
            Double ratio = cal(copyList.get(i).getTotalInvestAmout() / copyMoney);
            CopyRatioDto copyRatioDto = new CopyRatioDto(copyList.get(i).getLeaderId(), copyList.get(i).getLeaderName(), cal(ratio * 100));
            copyRatioDtos.add(copyRatioDto);

        }

        return copyRatioDtos;


    }

    // 카피 시작
    @Override
    @Transactional
    public CopyStartResponseDto createCopy(CopyStartRequestDto copyStartRequestDto) {

        // 나의 포트폴리오 조회
        Portfolio portfolio = portfolioRepository.findByUserId(copyStartRequestDto.getUserId());

        Copy copyCheck = copyRepository.findByUserIdAndLeaderId(copyStartRequestDto.getUserId(), copyStartRequestDto.getLeaderId());

        if(portfolio.getBalance() > copyStartRequestDto.getAmount() && copyCheck == null){
            // 카피 시작
            Copy copy = new Copy(copyStartRequestDto, portfolio);
            copyRepository.save(copy);


            // 카피 후 포트폴리오 에서 금액 차감
            Double balance = portfolio.MinusBalance(copyStartRequestDto.getAmount()); // 카피 후 잔액

            return new CopyStartResponseDto(copyStartRequestDto, balance);
        }else{
            return new CopyStartResponseDto();
        }







        // 카피 한 코인 생성
        // 페인 사용
        // 리더에 코인에 대한 정보 가져와야함(구매비율)
//        WalletDto walletDto = walletReadServiceClient.getWallets(copyStartRequestDto.getLeaderId());
    }

    // 매수,매도 analysis에서 보내준값 확인


    public Double cal(Double x){
        return Math.round(x*1000) / 1000.0;
    }

    // 카피 돈 추가/축소
    @Override
    @Transactional
    public CopyPutResponseDto copyPut(CopyPutRequestDto copyPutRequestDto) {
        Copy copy = copyRepository.findByUserIdAndLeaderId(copyPutRequestDto.getUserId(), copyPutRequestDto.getLeaderId());
        Portfolio portfolio = portfolioRepository.findByUserId(copyPutRequestDto.getUserId());
        String leaderName = copy.getLeaderName();
        Double investAmount = copy.getTotalInvestAmout();

        if(copyPutRequestDto.getType().equals("withdraw") && copyPutRequestDto.getAmount() > copy.getTotalInvestAmout()){
            return new CopyPutResponseDto();
        }

        List<Coin> coinList = coinRepository.findByCopyId(copy.getId());

        Double sellKRW = 0.0;

        // 투자금 변경으로 일어날일
        if(!coinList.isEmpty()){
            if(copyPutRequestDto.getType().equals("add")){
                for(int i=0; i< coinList.size(); i++){
                    String coinName = coinList.get(i).getCoinName();
                    Double coinAvg = coinList.get(i).getAvgPrice();
                    Double coinQuantity = coinList.get(i).getQuantity();
                    Double coinRatio = cal(coinAvg * coinQuantity / investAmount);
                    System.out.println("전체 투자돈에 코인 비율 :" + coinRatio);

                    //현재가
                    Double currentPrice = bithumbOpenApi.TickerApi(coinName);

                    // 추가 투자 비율
                    Double buyKRW = cal(copyPutRequestDto.getAmount() * coinRatio);
                    System.out.println("코인에 추가 할 돈 : " + buyKRW);

                    Double buyQuantity = cal(buyKRW / currentPrice);

                    Double totalQuantity = coinList.get(i).getQuantity() + buyQuantity;

                    // 기존 평단가
                    Double oldMoney = cal(coinQuantity * coinAvg);

                    // 실시간 평단가
                    Double newMoney = cal(buyQuantity * currentPrice);//(현재가);

                    // 새로운 평단가
                    Double totalAvgPrice = cal((oldMoney + newMoney) / totalQuantity);

                    Double currentQuantity = coinList.get(i).UpdateBuyQuantity(totalQuantity, totalAvgPrice);

                    System.out.println("코인에 총 추가된 돈 : " + coinList.get(i).getQuantity() * coinList.get(i).getAvgPrice());

                }
            }
            // 돈 축소인경우
            else{
                for(int i=0; i< coinList.size(); i++){
                    String coinName = coinList.get(i).getCoinName();
                    Double coinAvg = coinList.get(i).getAvgPrice();
                    Double coinQuantity = coinList.get(i).getQuantity();
                    Double coinRatio = cal(coinAvg * coinQuantity / investAmount);
                    System.out.println("코인 수량 : " + coinQuantity);
                    System.out.println("코인 평단가 : " + coinAvg);
                    System.out.println("전체 투자돈에 코인 비율 :" + coinRatio);

                    //현재가
                    Double currentPrice = bithumbOpenApi.TickerApi(coinName);

                    // 돈 축소
                    sellKRW = cal(copyPutRequestDto.getAmount() * coinRatio);
                    System.out.println("코인 판매 할 돈 : " + sellKRW);

                    // 매도할 코인보다 없을 경우에는 가지고있는 코인만 팔고 코인 삭제
                    if(sellKRW > currentPrice * coinQuantity){
                        sellKRW = currentPrice * coinQuantity;

                        Double sellQuantity = sellKRW / currentPrice;

                        System.out.println("코인 수량 :" + coinList.get(i).getQuantity() );
                        System.out.println("팔 코인 수량 : " + sellQuantity);
                        Double totalQuantity = coinList.get(i).getQuantity() - sellQuantity;

                        // 매도는 평단가 안바뀐다.

                        // 코인 삭제
                        coinRepository.delete(coinList.get(i));

                        //portfolio.PlusBalance(sellKRW);

                        //copy.MinusInvest(sellKRW);

                        System.out.println("코인에 총 추가된 돈 : " + coinList.get(i).getQuantity() * coinList.get(i).getAvgPrice());
                    }
                    // 매도할 코인이 있을경우
                    else{
                        Double sellQuantity = sellKRW / currentPrice;

                        System.out.println("코인 수량 :" + coinList.get(i).getQuantity() );
                        System.out.println("팔 코인 수량 : " + sellQuantity);
                        Double totalQuantity = coinList.get(i).getQuantity() - sellQuantity;



                        // 매도는 평단가 안바뀐다.

                        coinList.get(i).UpdateSellQuantity(totalQuantity);

                        // 포트폴리오에 돈 추가
                        //portfolio.PlusBalance(sellKRW);

                        // 카피 돈 감소
                        //copy.MinusInvest(sellKRW);

                        System.out.println("코인에 총 추가된 돈 : " + coinList.get(i).getQuantity() * coinList.get(i).getAvgPrice());
                    }



                }
            }

        }
        Double KRWRatio = cal(copy.getInvestBalance() / investAmount);
        System.out.println("KRWRatio : " + KRWRatio);
        if(copyPutRequestDto.getType().equals("add")){
            // 잔액 비율만큼 잔액에 추가
            copy.CopyPlusBalance(cal(copyPutRequestDto.getAmount() * KRWRatio));
            System.out.println("잔액에 추가할 돈 : " + cal(copyPutRequestDto.getAmount() * KRWRatio));

            // 포트폴리오에 돈 빠져나감
            portfolio.MinusBalance(copyPutRequestDto.getAmount());

            // copy에게 총 투자금 변경
            copy.PlusInvest(copyPutRequestDto.getAmount());

        }
        else{
            // 잔액 비율만큼 잔액 뺴기
            if(copy.getInvestBalance() - copyPutRequestDto.getAmount() * KRWRatio <= 0){

                portfolio.PlusBalance(copy.getInvestBalance() + sellKRW);

                copy.MinusInvest(copy.getInvestBalance() + sellKRW);

                copy.CopyMinusBalance(copy.getInvestBalance());
                // 남은 잔액이 더 적을경우 남은 잔액만 뺀다.


            }else{
                copy.CopyMinusBalance(cal(copyPutRequestDto.getAmount() * KRWRatio));
                // 포트폴리오에 돈 들어옴
                portfolio.PlusBalance(copyPutRequestDto.getAmount());

                // copy에게 총 투자금 변경
                copy.MinusInvest(copyPutRequestDto.getAmount());
            }
            System.out.println(copy.getTotalInvestAmout());
            if(copy.getInvestBalance() < 0 || copy.getTotalInvestAmout() <= 0){
                coinRepository.deleteAll(coinList);
                copyRepository.delete(copy);
            }
        }

        return new CopyPutResponseDto(copyPutRequestDto, leaderName, portfolio.getBalance());

    }

    // 카피중지
    @Override
    @Transactional
    public CopyDeleteResponseDto copyDelete(CopyDeleteRequestDto copyDeleteRequestDto) {
        Copy copy = copyRepository.findByUserIdAndLeaderId(copyDeleteRequestDto.getUserId(),
                copyDeleteRequestDto.getLeaderId());

        System.out.println(copyDeleteRequestDto.getUserId());
        System.out.println(copyDeleteRequestDto.getLeaderId());
        Portfolio portfolio = portfolioRepository.findByUserId(copyDeleteRequestDto.getUserId());

        // 카피 중지를 하면 들고있던 코인은 현재가로 다 팔고 전체 돈 반환
        List<Coin> coinList = coinRepository.findByCopyId(copy.getId());
        Double totalCoin = 0.0;
        for (Coin coin : coinList) {
            Double currentPrice = bithumbOpenApi.TickerApi(coin.getCoinName());
            Double coinQuantity = coin.getQuantity();

            Double sellKRW = cal(coinQuantity * currentPrice);
            totalCoin += sellKRW;

            // 코인 삭제
            coinRepository.delete(coin);
        }
        // 코인 판돈이랑 남은 잔액 반환
        Double returnKRW = copy.getInvestBalance() + totalCoin;

        portfolio.PlusBalance(returnKRW);

        String leaderName = copy.getLeaderName();

        copyRepository.delete(copy);

        return new CopyDeleteResponseDto(copyDeleteRequestDto.getUserId(),
                copyDeleteRequestDto.getLeaderId(), leaderName, portfolio.getBalance());

    }
}
