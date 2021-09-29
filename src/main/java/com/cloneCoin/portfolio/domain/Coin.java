package com.cloneCoin.portfolio.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Coin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long leaderId;
    private String coinName;
    private Double avgPrice;
    private Double quantity;

    @ManyToOne
    @JoinColumn(name = "COPY_ID")
    private Copy copy;

    public Coin(Long userId, Long leaderId, String coinName, Double buyQuantity, Double currentPrice) {
        this.userId = userId;
        this.leaderId = leaderId;
        this.coinName = coinName;
        this.quantity = buyQuantity;
        this.avgPrice = currentPrice;
    }


    public void UpdateSellQuantity(Double sellQuantity) {
        this.quantity -= sellQuantity;
    }

    public void UpdateBuyQuantity(Double buyQuantity, Double totalAvgPrice) {
        this.quantity += buyQuantity;
        this.avgPrice = totalAvgPrice;
    }
}
