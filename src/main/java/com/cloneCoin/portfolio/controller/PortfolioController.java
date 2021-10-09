package com.cloneCoin.portfolio.controller;

import com.cloneCoin.portfolio.dto.PortfolioResponseDto;
import com.cloneCoin.portfolio.dto.UserPeriodDto;
import com.cloneCoin.portfolio.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;


    @GetMapping("/welcome")
    public String welcome() {
        return "portfolio welcome test";
    }

    @GetMapping("/health_check")
    public String status(HttpServletRequest request) {
        return String.format("Portfolio server is working %s", request.getServerPort());
    }

    @GetMapping("/{userId}")
    public PortfolioResponseDto getPortfolio(@PathVariable("userId") Long userId) {

        return portfolioService.getPortfolioByUserId(userId);

        //ResponsePortfolio responsePortfolio = new ModelMapper().map(portfolioDto, ResponsePortfolio.class);

    }

    // 특정 유저의 기간별 수익률 가져오기
    @GetMapping("/user")
    public UserPeriodDto getLeaderPeriod(@RequestParam(value = "userId") Long leaderId,
                                         @RequestParam(value = "period") Long period) {
        return portfolioService.getUserPeriod(leaderId, period);
    }


}
