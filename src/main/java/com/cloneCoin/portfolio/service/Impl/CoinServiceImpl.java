package com.cloneCoin.portfolio.service.Impl;

import com.cloneCoin.portfolio.domain.Coin;
import com.cloneCoin.portfolio.domain.Copy;
import com.cloneCoin.portfolio.dto.CoinRequestDto;
import com.cloneCoin.portfolio.dto.CoinResponseDto;
import com.cloneCoin.portfolio.repository.CoinRepository;
import com.cloneCoin.portfolio.repository.CopyRepository;
import com.cloneCoin.portfolio.service.CoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoinServiceImpl implements CoinService {

    private final CoinRepository coinRepository;
    private final CopyRepository copyRepository;

    // 클라이언트가 포트폴리오 접근시 카피하고 있는 리더들의 코인정보 리턴
    @Override
    @Transactional
    public List<CoinResponseDto> coinReturn(CoinRequestDto coinRequestDto) {

        // 현재 카피하고 있는 리더정보 가져옴
        List<Copy> copyList = copyRepository.findByUserId(coinRequestDto.getUserId());

        List<CoinResponseDto> coinResponseDtoList = new ArrayList<>();

        if(!copyList.isEmpty()){
            for (Copy copy : copyList) {

                List<Coin> coinList = coinRepository.findByUserIdAndLeaderId(coinRequestDto.getUserId(), copy.getLeaderId());

                if (!coinList.isEmpty()) {
                    for (Coin coin : coinList) {
                        String coinName = coin.getCoinName();
                        Double coinAvg = coin.getAvgPrice();
                        Double coinQuantity = coin.getQuantity();
                        CoinResponseDto coinResponseDto = new CoinResponseDto(coin.getUserId(), coin.getLeaderId(), copy.getLeaderName(),
                                coinName, coinAvg, coinQuantity);

                        coinResponseDtoList.add(coinResponseDto);
                    }
                }
            }
        }

        return coinResponseDtoList;
    }
}
