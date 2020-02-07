package com.eleks.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRequestDto {

    @Size(max = 50, message = "User name shouldn't be more than 50 symbols")
    @NotBlank(message = "User name shouldn't be blank")
    private String userName;

    @Size(max = 50, message = "First name shouldn't be more than 50 symbols")
    @NotBlank(message = "First name shouldn't be blank")
    private String firstName;

    @Size(max = 50, message = "Last name shouldn't be more than 50 symbols")
    @NotBlank(message = "Last name shouldn't be blank")
    private String lastName;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @NotNull(message = "Date of birth shouldn't be null")
    private LocalDate dateOfBirth;

    @Email(message = "Email should have email like format")
    private String email;

    @NotNull(message = "Receive notifications shouldn't be null")
    private Boolean receiveNotifications;

    @Size(max = 50, message = "Password shouldn't be more than 50 symbols")
    @NotBlank(message = "Password shouldn't be blank")
    private String password;
}
