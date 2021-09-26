package com.cloneCoin.portfolio.repository;


import com.cloneCoin.portfolio.domain.Copy;
import com.cloneCoin.portfolio.domain.Portfolio;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CopyRepository extends CrudRepository<Copy, Long> {

    List<Copy> findByPortfolioId(Long portfolioId);
}
