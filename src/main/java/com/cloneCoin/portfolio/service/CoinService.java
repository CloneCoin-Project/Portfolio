package com.cloneCoin.portfolio.service;

import com.cloneCoin.portfolio.dto.CoinRequestDto;
import com.cloneCoin.portfolio.dto.CoinResponseDto;

import java.util.List;

public interface CoinService {

    List<CoinResponseDto> coinReturn(CoinRequestDto coinRequestDto);
}
