package com.cloneCoin.portfolio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class UserPeriodDto {

    private Long userId;

    @JsonProperty("profits")
    private List<UserPeriodContent> userPeriodContentList = new ArrayList<>();
}
