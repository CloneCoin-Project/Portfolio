package com.cloneCoin.portfolio.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
//@JsonInclude(JsonInclude.Include.)
public class ResponsePortfolio {
    private String userId;
    private Long portfolioProfit;
}
