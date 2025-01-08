package com.possible.mecash.controller;


import com.possible.mecash.dto.req.TransactionReq;
import com.possible.mecash.dto.response.TransactionResp;
import com.possible.mecash.model.Transaction;
import com.possible.mecash.service.impl.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResp> transfer(@RequestBody TransactionReq request) {
        Transaction transaction = transactionService.transfer(
                request.getSourceAccount(),
                request.getDestinationAccount(),
                Double.parseDouble(String.valueOf(request.getAmount())));

        return ResponseEntity.ok(convertToResponse(transaction));
    }


    @GetMapping("/history/{accountNumber}")
    public ResponseEntity<List<TransactionResp>> getTransactionHistory(
            @PathVariable String accountNumber
    ) {
        List<Transaction> transactions = transactionService.getTransactionHistory(accountNumber);
        return ResponseEntity.ok(transactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
    }

    private TransactionResp convertToResponse(Transaction transaction) {
        // Convert transaction to response DTO
        return new TransactionResp(
                transaction.getId(),
                Double.parseDouble(String.valueOf(transaction.getAmount())),
//                transaction.getDiscountApplied(),
                transaction.getTransactionType()
        );
    }
}
