package com.cloneCoin.portfolio.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Coin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long UserId;
    private Long leaderId;
    private String coinName;
    private Double avgPrice;
    private Double quantity;

    @ManyToOne
    @JoinColumn(name = "COPY_ID")
    private Copy copy;


    public void UpdateSellQuantity(Double sellQuantity) {
        this.quantity -= sellQuantity;
    }

    public void UpdateBuyQuantity(Double buyQuantity, Double totalAvgPrice) {
        this.quantity += buyQuantity;
        this.avgPrice = totalAvgPrice;
    }
}
