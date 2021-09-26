package com.cloneCoin.portfolio.domain;

import com.cloneCoin.portfolio.dto.CopyRequestDto;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@Getter
public class Copy {
    @Id
    private Long id;
    private Long userId;
    private Long leaderId;
    private Date registerDate;
    private Long totalInvestAmout;
    private Long copyProfit;

    @ManyToOne
    @JoinColumn(name = "PORTFOLIO_ID")
    private Portfolio portfolio;

    public Copy(CopyRequestDto copyRequestDto) {
        this.userId = copyRequestDto.getUserid();
        this.leaderId = copyRequestDto.getLeaderId();
        this.totalInvestAmout = copyRequestDto.getAmount();
    }
}
