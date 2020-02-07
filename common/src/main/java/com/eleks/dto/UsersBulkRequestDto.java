package com.eleks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UsersBulkRequestDto {

    @NotEmpty(message = "Can not be null or empty")
    private List<Long> userIds;
}
