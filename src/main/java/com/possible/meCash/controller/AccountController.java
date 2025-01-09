package com.possible.mecash.controller;



import com.possible.mecash.dto.req.TransactionReq;
import com.possible.mecash.dto.response.ResponseDto;
import com.possible.mecash.dto.response.TransactionResp;
import com.possible.mecash.model.Transaction;
import com.possible.mecash.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/balance")
    public ResponseEntity<ResponseDto<Object>> getBalance(@RequestParam String accountNumber){
        return new ResponseEntity<>(accountService.balanceEnquiry(accountNumber), HttpStatus.OK);
    }

    @PostMapping("/deposit")
    public ResponseEntity<ResponseDto<Object>> saveToWallet(@RequestBody TransactionReq req){
        return new ResponseEntity<>(accountService.saveToAccount(req), HttpStatus.OK);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ResponseDto<Object>> withdrawFromWallet(@RequestBody TransactionReq req){
        return new ResponseEntity<>(accountService.withdrawFromAccount(req), HttpStatus.OK);
    }

    @PostMapping("/transfer")
    public ResponseEntity<ResponseDto<Object>> transferFromWallet(@RequestBody TransactionReq req){
        return new ResponseEntity<>(accountService.walletTransfer(req), HttpStatus.OK);
    }

    /*@GetMapping("/transactions")
    public ResponseEntity<ResponseDto<Object>> getTransactions(@RequestParam String accountNumber){
        return new ResponseEntity<>(accountService.getUserTransactions(accountNumber), HttpStatus.OK);
    }*/

    @GetMapping("/history/{accountNumber}")
    public ResponseEntity<List<TransactionResp>> getTransactionHistory(@PathVariable String accountNumber) {
        List<TransactionResp> transactionResp = accountService.getTransactionHistory(accountNumber);
        return ResponseEntity.ok(transactionResp);
    }



}

