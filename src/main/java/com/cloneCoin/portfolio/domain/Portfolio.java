package com.cloneCoin.portfolio.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Entity
@Table(name="portfolio")
public class Portfolio implements Serializable {
    // What is Serializable ?
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column
    private Long portfolioProfit;

    private Long balance;

    public Portfolio(Long userId, Long portfolioProfit) {
        this.userId = userId;
        this.portfolioProfit = portfolioProfit;
    }

    public Long UpdateBalance(Long amount){
        this.balance -= amount;
        return balance;
    }
}
