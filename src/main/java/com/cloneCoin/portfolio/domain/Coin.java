package com.cloneCoin.portfolio.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Coin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long leaderId;
    private String coinName;
    private Long avgPrice;
    private Long quantity;

    @ManyToOne
    @JoinColumn(name = "COPY_ID")
    private Copy copy;


}
