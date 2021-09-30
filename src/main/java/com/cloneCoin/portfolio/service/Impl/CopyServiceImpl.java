package com.cloneCoin.portfolio.service.Impl;

import com.cloneCoin.portfolio.client.BithumbOpenApi;
import com.cloneCoin.portfolio.client.WalletReadServiceClient;
import com.cloneCoin.portfolio.domain.Coin;
import com.cloneCoin.portfolio.domain.Copy;
import com.cloneCoin.portfolio.domain.Portfolio;
import com.cloneCoin.portfolio.dto.CopyDeleteRequestDto;
import com.cloneCoin.portfolio.dto.CopyPutRequestDto;
import com.cloneCoin.portfolio.dto.CopyStartRequestDto;
import com.cloneCoin.portfolio.dto.WalletDto;
import com.cloneCoin.portfolio.repository.CoinRepository;
import com.cloneCoin.portfolio.repository.CopyRepository;
import com.cloneCoin.portfolio.repository.PortfolioRepository;
import com.cloneCoin.portfolio.service.CopyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CopyServiceImpl implements CopyService {

    private final CopyRepository copyRepository;
    private final PortfolioRepository portfolioRepository;
    private final WalletReadServiceClient walletReadServiceClient;
    private final CoinRepository coinRepository;
    private final BithumbOpenApi bithumbOpenApi;

    @Override
    @Transactional
    public void createCopy(CopyStartRequestDto copyStartRequestDto) {

        // 나의 포트폴리오 조회
        System.out.println(copyStartRequestDto.getUserId());
        Portfolio portfolio = portfolioRepository.findByUserId(copyStartRequestDto.getUserId());
        // 카피 시작
        Copy copy = new Copy(copyStartRequestDto, portfolio);
        copyRepository.save(copy);

        // 총 금액이랑 카피할 돈 비교해서 총금액보다 크다면 카피 불가

        // 카피 후 포트폴리오 에서 금액 차감
        Double balance = portfolio.MinusBalance(copyStartRequestDto.getAmount()); // 카피 후 잔액

        // 카피 한 코인 생성
        // 페인 사용
        // 리더에 코인에 대한 정보 가져와야함(구매비율)
//        WalletDto walletDto = walletReadServiceClient.getWallets(copyStartRequestDto.getLeaderId());
    }

    // 매수,매도 analysis에서 보내준값 확인


    public Double cal(Double x){
        return Math.floor(x*1000) / 1000.0;
    }

    // 카피 돈 추가/축소
    @Override
    @Transactional
    public boolean copyPut(CopyPutRequestDto copyPutRequestDto) {
        Copy copy = copyRepository.findByUserIdAndLeaderId(copyPutRequestDto.getUserid(), copyPutRequestDto.getLeaderId());
        Portfolio portfolio = portfolioRepository.findByUserId(copyPutRequestDto.getUserid());

        Double investAmount = copy.getTotalInvestAmout();

        if(copyPutRequestDto.getType().equals("withdraw") && copyPutRequestDto.getAmount() > portfolio.getBalance()){
            return false;
        }

        List<Coin> coinList = coinRepository.findByCopyId(copy.getId());

        // 투자금 변경으로 일어날일
        if(!coinList.isEmpty()){
            if(copyPutRequestDto.getType().equals("add")){
                for(int i=0; i< coinList.size(); i++){
                    String coinName = coinList.get(i).getCoinName();
                    Double coinAvg = coinList.get(i).getAvgPrice();
                    Double coinQuantity = coinList.get(i).getQuantity();
                    Double coinRatio = cal(coinAvg * coinQuantity / investAmount);

                    //현재가
                    Double currentPrice = bithumbOpenApi.TickerApi(coinName);

                    // 추가 투자 비율
                    Double buyKRW = cal(copyPutRequestDto.getAmount() * coinRatio);

                    Double buyQuantity = cal(buyKRW / currentPrice);

                    Double totalQuantity = cal(coinList.get(i).getQuantity() + buyQuantity);

                    // 기존 평단가
                    Double oldMoney = cal(coinQuantity * coinAvg);

                    // 실시간 평단가
                    Double newMoney = cal(buyQuantity * currentPrice);//(현재가);

                    // 새로운 평단가
                    Double totalAvgPrice = cal((oldMoney + newMoney) / totalQuantity);

                    Double currentQuantity = coinList.get(i).UpdateBuyQuantity(buyQuantity, totalAvgPrice);

                }
            }
            // 돈 축소인경우
            else{
                for(int i=0; i< coinList.size(); i++){
                    String coinName = coinList.get(i).getCoinName();
                    Double coinAvg = coinList.get(i).getAvgPrice();
                    Double coinQuantity = coinList.get(i).getQuantity();
                    Double coinRatio = cal(coinAvg * coinQuantity / investAmount);

                    //현재가
                    Double currentPrice = bithumbOpenApi.TickerApi(coinName);

                    // 돈 축소 비율
                    Double sellKRW = cal(copyPutRequestDto.getAmount() * coinRatio);

                    Double sellQuantity = cal(sellKRW / currentPrice);

                    Double totalQuantity = cal(coinList.get(i).getQuantity() - sellQuantity);

                    // 매도는 평단가 안바뀐다.

                    coinList.get(i).UpdateSellQuantity(totalQuantity);

                }
            }

        }
        Double KRWRatio = cal(copy.getInvestBalance() / investAmount);
        if(copyPutRequestDto.getType().equals("add")){
            // 잔액 비율만큼 잔액에 추가
            copy.CopyPlusBalance(cal(copyPutRequestDto.getAmount() * KRWRatio));
        }
        else{
            // 잔액 비율만큼 잔액 뺴기
            copy.CopyMinusBalance(cal(copyPutRequestDto.getAmount() * KRWRatio));
        }

        // copy에게 총 투자금 변경
        copy.UpdateInvest(copyPutRequestDto.getAmount());

        return true;

    }

    @Override
    @Transactional
    public void copyDelete(CopyDeleteRequestDto copyDeleteRequestDto) {
        List<Copy> copyList = copyRepository.findByUserId(copyDeleteRequestDto.getUserId());

        // 카피 삭제
        for(int i=0; i<=copyList.size(); i++){

            Portfolio portfolio = portfolioRepository.findByUserId(copyList.get(i).getUserId());
            // 카피삭제시 투자했던 돈 반환
            Double returnMoney = copyList.get(i).getTotalInvestAmout();

            // 삭제완료시 돈 반환
            copyRepository.delete(copyList.get(i));
            portfolio.PlusBalance(returnMoney);
        }
    }
}
