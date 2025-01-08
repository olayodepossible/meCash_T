package com.possible.meCash.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    private String initiatorAccount;
    private String beneficiaryAccount;
    private BigDecimal amount;
    private String transactionType;
    private String description;

}
