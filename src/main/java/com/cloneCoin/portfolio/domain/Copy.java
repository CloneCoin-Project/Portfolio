package com.cloneCoin.portfolio.domain;

import com.cloneCoin.portfolio.dto.CopyStartRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class Copy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long leaderId;
    //private Date registerDate;
    private Double totalInvestAmout;
    private Double InvestBalance;
    //private Double copyProfit;

    @ManyToOne
    @JoinColumn(name = "PORTFOLIO_ID")
    private Portfolio portfolio;

    public Copy(CopyStartRequestDto copyStartRequestDto, Portfolio portfolio) {
        this.userId = copyStartRequestDto.getUserId();
        this.leaderId = copyStartRequestDto.getLeaderId();
        this.totalInvestAmout = copyStartRequestDto.getAmount();
        this.InvestBalance = copyStartRequestDto.getAmount();
        this.portfolio = portfolio;

    }

    // 카피 돈 추가/축소
    public void UpdateInvest(Long amount) {
        this.totalInvestAmout += amount;
    }

    public void CopyMinusBalance(Double buyKRW) {
        this.InvestBalance -= buyKRW;
    }

    public void CopyPlusBalance(Double sellBalance) {
        this.InvestBalance += sellBalance;
    }

    //
}
