package com.eleks.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserStatusResponse {

    private Long userId;

    private String userName;

    private String currency;

    private BigDecimal value;
}
