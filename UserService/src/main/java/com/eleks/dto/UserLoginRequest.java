package com.eleks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserLoginRequest {

    @NotBlank
    private String userName;

    @NotBlank
    private String password;
}
