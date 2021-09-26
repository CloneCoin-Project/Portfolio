package com.cloneCoin.portfolio.controller;

import com.cloneCoin.portfolio.dto.PortfolioDto;
import com.cloneCoin.portfolio.service.PortfolioService;
import com.cloneCoin.portfolio.vo.ResponsePortfolio;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/portfolio/")
public class PortfolioController {
    PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "portfolio welcome test";
    }

    @GetMapping("/health_check")
    public String status(HttpServletRequest request) {
        return String.format("Portfolio server is working %s", request.getServerPort());
    }

    @GetMapping("/portfolio/{userId}")
    public ResponseEntity<ResponsePortfolio> getPortfolio(@PathVariable("userId") String userId) {

        PortfolioDto portfolioDto = portfolioService.getPortfolioByUserId(userId);

        ResponsePortfolio responsePortfolio = new ModelMapper().map(portfolioDto, ResponsePortfolio.class);

        return ResponseEntity.status(HttpStatus.OK).body(responsePortfolio);
    }
}
