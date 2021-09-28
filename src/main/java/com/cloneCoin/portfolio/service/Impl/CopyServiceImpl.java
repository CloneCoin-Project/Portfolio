package com.cloneCoin.portfolio.service.Impl;

import com.cloneCoin.portfolio.client.WalletReadServiceClient;
import com.cloneCoin.portfolio.domain.Copy;
import com.cloneCoin.portfolio.domain.Portfolio;
import com.cloneCoin.portfolio.dto.CopyDeleteRequestDto;
import com.cloneCoin.portfolio.dto.CopyPutRequestDto;
import com.cloneCoin.portfolio.dto.CopyStartRequestDto;
import com.cloneCoin.portfolio.dto.WalletDto;
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
        Long balance = portfolio.MinusBalance(copyStartRequestDto.getAmount()); // 카피 후 잔액

        // 카피 한 코인 생성
        // 페인 사용
        // 리더에 코인에 대한 정보 가져와야함(구매비율)
//        WalletDto walletDto = walletReadServiceClient.getWallets(copyStartRequestDto.getLeaderId());
    }

    // 매수,매도 analysis에서 보내준값 확인

    // 카피 돈 추가/축소
    @Override
    @Transactional
    public void copyPut(CopyPutRequestDto copyPutRequestDto) {
        Copy copy = copyRepository.findByUserIdAndLeaderId(copyPutRequestDto.getUserid(), copyPutRequestDto.getLeaderId());

        // copy에게 총 투자금 변경
        copy.UpdateInvest(copyPutRequestDto.getAmount());

        // 투자금 변경으로 일어날일
    }

    @Override
    @Transactional
    public void copyDelete(CopyDeleteRequestDto copyDeleteRequestDto) {
        List<Copy> copyList = copyRepository.findByUserId(copyDeleteRequestDto.getUserId());

        // 카피 삭제
        for(int i=0; i<=copyList.size(); i++){

            Portfolio portfolio = portfolioRepository.findByUserId(copyList.get(i).getUserId());
            // 카피삭제시 투자했던 돈 반환
            Long returnMoney = copyList.get(i).getTotalInvestAmout();

            // 삭제완료시 돈 반환
            copyRepository.delete(copyList.get(i));
            portfolio.PlusBalance(returnMoney);
        }
    }
}
