package com.cloneCoin.portfolio.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name="portfolio")
@NoArgsConstructor
public class Portfolio{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;


    private Double balance;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Profit> profits = new ArrayList<>();

    public Portfolio(Long userId, Double balance) {
        this.userId = userId;
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
