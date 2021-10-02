package com.cloneCoin.portfolio.repository;


import com.cloneCoin.portfolio.domain.Copy;
import com.cloneCoin.portfolio.domain.Portfolio;
import com.cloneCoin.portfolio.dto.CopyDeleteRequestDto;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CopyRepository extends CrudRepository<Copy, Long> {

    List<Copy> findByPortfolioId(Long portfolioId);

    Copy findByUserIdAndLeaderId(Long userid, Long leaderId);

    List<Copy> findByLeaderId(Long leaderId);

    List<Copy> findByUserId(Long userId);
}
