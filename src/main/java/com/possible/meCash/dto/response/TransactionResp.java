package com.possible.mecash.dto.response;


import com.possible.mecash.dto.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResp {
        private String sender;
        private String beneficiary;
        private BigDecimal amount;
        private LocalDateTime valueDate;
        private String status;
        private String narration;
        private String transactionRef;
        private TransactionType transactionType;

}
