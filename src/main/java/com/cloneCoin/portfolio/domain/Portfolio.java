package com.cloneCoin.portfolio.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Entity
@Table(name="portfolio")
@NoArgsConstructor
public class Portfolio{
    // What is Serializable ?
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column
    private Long portfolioProfit;

    private Double balance;

    public Portfolio(Long userId, Long portfolioProfit, Double balance) {
        this.userId = userId;
        this.portfolioProfit = portfolioProfit;
        this.balance = balance;
    }

    public Double PlusBalance(Double amount){
        this.balance += amount;
        return balance;
    }

    public Double MinusBalance(Double amount){
        this.balance -= amount;
        return balance;
    }
}
