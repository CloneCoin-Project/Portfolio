package com.cloneCoin.portfolio.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class UserPeriodContent {

    private LocalDate localDate;

    private Double profit;
}
