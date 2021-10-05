package com.cloneCoin.portfolio.controller;

import com.cloneCoin.portfolio.dto.*;
import com.cloneCoin.portfolio.dto.CopyRatioDto;
import com.cloneCoin.portfolio.service.CopyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portfolio")
public class CopyController {

    private final CopyService copyService;

    @GetMapping("/copy/{leaderId}")
    public CopyAmountDto copyGet(@PathVariable Long leaderId){
        return copyService.copyGet(leaderId);

    }

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
    public ResponseEntity<CopyPutResponseDto> copyPut(@RequestBody CopyPutRequestDto copyPutRequestDto){
        CopyPutResponseDto check = copyService.copyPut(copyPutRequestDto);
        System.out.println(check);
        if(check.getLeaderId() != null){
            return new ResponseEntity<>(check, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(check, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/copy")
    public CopyDeleteResponseDto copyDelete(@RequestBody CopyDeleteRequestDto copyDeleteRequestDto){
        return copyService.copyDelete(copyDeleteRequestDto);
    }

    @GetMapping("/copy/ratio/{userId}")
    public List<CopyRatioDto> copyRatio(@PathVariable Long userId){
        return copyService.copyRatio(userId);
    }

}
