package com.cloneCoin.portfolio.controller;

import com.cloneCoin.portfolio.dto.CoinRequestDto;
import com.cloneCoin.portfolio.dto.CoinResponseDto;
import com.cloneCoin.portfolio.service.CoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portfolio")
public class CoinController {

    private final CoinService coinService;

    @GetMapping("/coin")
    public List<CoinResponseDto> coinReturn(@RequestBody CoinRequestDto coinRequestDto){
        return coinService.coinReturn(coinRequestDto);

    }
}
