package com.eleks.dto;

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
public class PaymentRequestDto {

    @Size(max = 200, message = "Payment description shouldn't be more than 200 symbols")
    @NotBlank(message = "Payment description shouldn't be blank")
    private String paymentDescription;

    @NotNull(message = "Price shouldn't be null")
    private Double price;

    @NotNull(message = "Co-payers shouldn't be null")
    private List<Long> coPayers;
}
