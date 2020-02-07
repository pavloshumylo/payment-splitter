package com.eleks.dto;

import com.eleks.validator.ValidateCurrency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroupRequestDto {

    @Size(max = 50, message = "Group name shouldn't be more than 50 symbols")
    @NotBlank(message = "Group name shouldn't be blank")
    private String groupName;

    @ValidateCurrency
    private String currency;

    @Size(min = 1, message = "Members size should be min 1")
    @NotNull(message = "Members field shouldn't be null")
    List<Long> members;
}
