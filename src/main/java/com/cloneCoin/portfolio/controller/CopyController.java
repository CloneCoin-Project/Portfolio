package com.cloneCoin.portfolio.controller;

import com.cloneCoin.portfolio.dto.CopyPutRequestDto;
import com.cloneCoin.portfolio.dto.CopyStartRequestDto;
import com.cloneCoin.portfolio.service.CopyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portfolio")
public class CopyController {

    private final CopyService copyService;

    @PostMapping("/copy")
    public void copyStart(@RequestBody CopyStartRequestDto copyStartRequestDto){
        copyService.createCopy(copyStartRequestDto);
    }

    @PutMapping("/copy")
    public void copyPut(@RequestBody CopyPutRequestDto copyPutRequestDto){
        copyService.copyPut(copyPutRequestDto);
    }
}
