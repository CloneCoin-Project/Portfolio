package com.cloneCoin.portfolio.repository;

import com.cloneCoin.portfolio.domain.PortfolioEntity;
import org.springframework.data.repository.CrudRepository;

public interface PortfolioRepository extends CrudRepository<PortfolioEntity, Long> {
    PortfolioEntity findByUserId(String userId);
}
