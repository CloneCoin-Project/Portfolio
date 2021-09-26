package com.cloneCoin.portfolio.service.Impl;

import com.cloneCoin.portfolio.client.WalletReadServiceClient;
import com.cloneCoin.portfolio.domain.Copy;
import com.cloneCoin.portfolio.domain.Portfolio;
import com.cloneCoin.portfolio.dto.CopyRequestDto;
import com.cloneCoin.portfolio.dto.WalletDto;
import com.cloneCoin.portfolio.repository.CopyRepository;
import com.cloneCoin.portfolio.repository.PortfolioRepository;
import com.cloneCoin.portfolio.service.CopyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CopyServiceImpl implements CopyService {

    private final CopyRepository copyRepository;
    private final PortfolioRepository portfolioRepository;
    private final WalletReadServiceClient walletReadServiceClient;

    @Override
    @Transactional
    public void createCopy(CopyRequestDto copyRequestDto) {

        // 카피 시작
        Copy copy = new Copy(copyRequestDto);
        copyRepository.save(copy);

        // 카피 후 포트폴리오 에서 금액 차감
        // Transaction 있어서 save 없어도 DB에 반영되는 확인해보기
        Portfolio portfolio = portfolioRepository.findByUserId(copyRequestDto.getUserid());
        Long balance = portfolio.UpdateBalance(copyRequestDto.getAmount());

        // 카피 한 코인 생성
        // 페인 사용
        // 리더에 코인에 대한 정보 가져와야함(구매비율)
        WalletDto walletDto = walletReadServiceClient.getWallets(copyRequestDto.getLeaderId());
    }
}
