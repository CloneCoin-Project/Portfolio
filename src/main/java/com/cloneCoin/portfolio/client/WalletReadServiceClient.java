package com.cloneCoin.portfolio.client;

import com.cloneCoin.portfolio.dto.WalletDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "walletRead")
public interface WalletReadServiceClient {

    @GetMapping("/wallet/leaders/{userId}")
    WalletDto getWallets(@PathVariable Long userId);

}
