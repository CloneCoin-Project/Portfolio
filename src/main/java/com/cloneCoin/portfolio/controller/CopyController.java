package com.cloneCoin.portfolio.controller;

import com.cloneCoin.portfolio.dto.*;
import com.cloneCoin.portfolio.service.CopyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portfolio")
public class CopyController {

    private final CopyService copyService;

    @PostMapping("/copy")
    public ResponseEntity<CopyStartResponseDto> copyStart(@RequestBody CopyStartRequestDto copyStartRequestDto){

        CopyStartResponseDto copyStartResponseDto = copyService.createCopy(copyStartRequestDto);

        if(copyStartResponseDto != null){
            return new ResponseEntity<>(copyStartResponseDto, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(copyStartResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/copy")
    public ResponseEntity<String> copyPut(@RequestBody CopyPutRequestDto copyPutRequestDto){
        boolean check = copyService.copyPut(copyPutRequestDto);
        if(check){
            return new ResponseEntity<>("copy 돈 추가/축소 되었습니다.", HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/copy")
    public CopyDeleteResponseDto copyDelete(@RequestBody CopyDeleteRequestDto copyDeleteRequestDto){
        return copyService.copyDelete(copyDeleteRequestDto);
    }
}
