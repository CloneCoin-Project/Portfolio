package com.cloneCoin.portfolio.repository;


import com.cloneCoin.portfolio.domain.Coin;
import org.springframework.data.repository.CrudRepository;

public interface CoinRepository extends CrudRepository<Coin, Long> {
    Coin findByUserId(Long userId);

    Coin findByCoinName(String coinName);

    Coin findByUserIdAndCoinName(Long aLong, String coinName);
}
