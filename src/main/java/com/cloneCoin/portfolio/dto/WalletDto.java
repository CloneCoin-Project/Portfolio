package com.cloneCoin.portfolio.dto;

public class WalletDto {
    private Long leaderId;
    private Long investment; // 총투자금액
    private Long totalAsset; // 총자산 : 원화 + (코인갯수 * 현재가)
    private Long coinAsset; // 코인 자산
}
