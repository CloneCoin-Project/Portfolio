package com.cloneCoin.portfolio.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name="portfolio")
public class PortfolioEntity implements Serializable {
    // What is Serializable ?
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column
    private Long portfolioProfit;
}
