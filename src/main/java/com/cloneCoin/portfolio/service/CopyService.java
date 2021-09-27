package com.cloneCoin.portfolio.service;

import com.cloneCoin.portfolio.dto.CopyPutRequestDto;
import com.cloneCoin.portfolio.dto.CopyStartRequestDto;

public interface CopyService {

    void createCopy(CopyStartRequestDto copyStartRequestDto);

    void copyPut(CopyPutRequestDto copyPutRequestDto);
}
