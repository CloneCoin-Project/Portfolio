package com.cloneCoin.portfolio.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CopyAmountDto {
    private int copyAmount;

    public CopyAmountDto(int size) {
        this.copyAmount = size;
    }
}
