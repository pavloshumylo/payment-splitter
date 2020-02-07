package com.eleks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroupResponseDto {

    private Long id;

    private String groupName;

    private String currency;

    List<Long> members;
}
