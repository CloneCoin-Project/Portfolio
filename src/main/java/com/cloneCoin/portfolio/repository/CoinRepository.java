package com.cloneCoin.portfolio.repository;


import com.cloneCoin.portfolio.domain.Coin;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CoinRepository extends CrudRepository<Coin, Long> {
    Coin findByUserId(Long userId);

    Coin findByCoinName(String coinName);

    Coin findByUserIdAndCoinName(Long aLong, String coinName);

    List<Coin> findByCopyId(Long id);

    List<Coin> findByUserIdAndLeaderId(Long userId, Long leaderId);
}
