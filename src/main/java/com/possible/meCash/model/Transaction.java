package com.possible.meCash.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.possible.task.dto.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "transactions")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String status;
    private String narration;
    private String senderAcct;
    private String beneficiaryAcct;

    private BigDecimal debit;
    private BigDecimal amount;
    private BigDecimal credit;
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @CreationTimestamp
    private LocalDateTime postedDate;
    @UpdateTimestamp
    private LocalDateTime valueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private Account account;

    @ManyToOne
    private AppUser appUser;
    private double discountApplied;
}
