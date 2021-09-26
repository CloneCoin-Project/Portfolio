package com.cloneCoin.portfolio.domain;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
public class Coin {

    @Id
    private Long id;
    private Long leaderId;
    private String coinName;
    private Long avgPrice;
    private Long quantity;

    @ManyToOne
    @JoinColumn(name = "COPY_ID")
    private Copy copy;


}
