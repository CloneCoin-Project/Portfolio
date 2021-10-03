package com.cloneCoin.portfolio.service;

import com.cloneCoin.portfolio.dto.*;

public interface CopyService {

    CopyStartResponseDto createCopy(CopyStartRequestDto copyStartRequestDto);

    boolean copyPut(CopyPutRequestDto copyPutRequestDto);

    CopyDeleteResponseDto copyDelete(CopyDeleteRequestDto copyDeleteRequestDto);
}
