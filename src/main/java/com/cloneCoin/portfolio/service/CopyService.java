package com.cloneCoin.portfolio.service;

import com.cloneCoin.portfolio.dto.*;

import java.util.List;

public interface CopyService {

    CopyStartResponseDto createCopy(CopyStartRequestDto copyStartRequestDto);

    CopyPutResponseDto copyPut(CopyPutRequestDto copyPutRequestDto);

    CopyDeleteResponseDto copyDelete(CopyDeleteRequestDto copyDeleteRequestDto);

    CopyAmountDto copyGet(Long leaderId);

    List<CopyRatioDto> copyRatio(Long userId);
}
