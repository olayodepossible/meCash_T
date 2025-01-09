package com.possible.mecash.dto.req;


import com.possible.mecash.dto.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionReq {

    @NotNull(message = "Source Account number is required")
    private String sourceAccount;

    private String sourceCurrency;
    @NotBlank(message = "Destination Account number is required")
    @NotNull(message = "Destination Account number is required")
    private String destinationAccount;
    @NotBlank(message = "Destination Account Currency is required")
    @NotNull(message = "Destination Account Currency is required")
    private String destinationCurrency;

    @DecimalMin(value = "1.00", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    private String narration;






}

