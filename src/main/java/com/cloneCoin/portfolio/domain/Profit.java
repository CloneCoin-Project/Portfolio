package com.cloneCoin.portfolio.domain;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
public class Profit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profit")
    private Double profit;

    @Column(name = "investment")
    private Double investment;

    @Column(name = "registerDate")
    private LocalDate localDate;

    @ManyToOne
    @JoinColumn(name = "portfolioId")
    private Portfolio portfolio;
}
