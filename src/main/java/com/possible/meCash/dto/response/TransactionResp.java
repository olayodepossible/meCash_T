package com.possible.mecash.dto.response;


import com.possible.mecash.dto.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResp {
        private Long id;
        private double amount;
        private TransactionType transactionType;

}
