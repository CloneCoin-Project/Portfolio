package com.cloneCoin.portfolio.repository;

import com.cloneCoin.portfolio.domain.Portfolio;
import org.springframework.data.repository.CrudRepository;

public interface PortfolioRepository extends CrudRepository<Portfolio, Long> {
    Portfolio findByUserId(long userId);
}
