package com.cloneCoin.portfolio.controller;

import com.cloneCoin.portfolio.dto.PortfolioResponseDto;
import com.cloneCoin.portfolio.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
