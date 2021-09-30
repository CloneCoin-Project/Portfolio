package com.cloneCoin.portfolio.service;

import com.cloneCoin.portfolio.dto.CopyDeleteRequestDto;
import com.cloneCoin.portfolio.dto.CopyPutRequestDto;
import com.cloneCoin.portfolio.dto.CopyStartRequestDto;

public interface CopyService {

    void createCopy(CopyStartRequestDto copyStartRequestDto);

    boolean copyPut(CopyPutRequestDto copyPutRequestDto);

    void copyDelete(CopyDeleteRequestDto copyDeleteRequestDto);
}
