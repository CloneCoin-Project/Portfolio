package com.cloneCoin.portfolio.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "feign", url = "https://api.bithumb.com")
public interface BithumbServiceClient {

    @GetMapping("/public/ticker/{order_currency}_{payment_currency}")
    TickerDto getTiker(@PathVariable String order_currency, @PathVariable String payment_currency);

}
