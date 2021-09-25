package com.cloneCoin.portfolio.jpa;

import org.springframework.data.repository.CrudRepository;

public interface PortfolioRepository extends CrudRepository<PortfolioEntity, Long> {
    PortfolioEntity findByUserId(String userId);
}
