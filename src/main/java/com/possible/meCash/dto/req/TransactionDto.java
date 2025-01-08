package com.possible.mecash.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    private String initiatorAccount;
    private String initiatorCurrency;
    private String beneficiaryAccount;
    private String beneficiaryCurrency;
    private BigDecimal amount;
    private String transactionType;
    private String description;

}
