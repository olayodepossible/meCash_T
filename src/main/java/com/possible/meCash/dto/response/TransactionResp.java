package com.possible.meCash.dto.response;

import com.possible.task.dto.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResp {
        private Long id;
        private double amount;
        private double discountApplied;
        private TransactionType transactionType;

}
