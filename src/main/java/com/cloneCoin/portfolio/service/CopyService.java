package com.cloneCoin.portfolio.service;

import com.cloneCoin.portfolio.dto.CopyDeleteRequestDto;
import com.cloneCoin.portfolio.dto.CopyPutRequestDto;
import com.cloneCoin.portfolio.dto.CopyStartRequestDto;
import com.cloneCoin.portfolio.dto.CopyStartResponseDto;

public interface CopyService {

    CopyStartResponseDto createCopy(CopyStartRequestDto copyStartRequestDto);

    boolean copyPut(CopyPutRequestDto copyPutRequestDto);

    void copyDelete(CopyDeleteRequestDto copyDeleteRequestDto);
}
