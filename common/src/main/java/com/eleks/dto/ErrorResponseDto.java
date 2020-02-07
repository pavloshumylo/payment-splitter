package com.eleks.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorResponseDto {

    private int statusCode;

    private String message;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate timestamp;
}
