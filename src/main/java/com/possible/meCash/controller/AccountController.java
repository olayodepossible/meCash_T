package com.possible.mecash.controller;



import com.possible.mecash.dto.response.ResponseDto;
import com.possible.mecash.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/balance")
    public ResponseEntity<ResponseDto<Object>> getBalance(@RequestParam String accountNumber){
        return new ResponseEntity<>(accountService.balanceEnquiry(accountNumber), HttpStatus.OK);
    }

    @GetMapping("/save")
    public ResponseEntity<ResponseDto<Object>> saveMoney(@RequestParam String accountNumber, @RequestParam Long amount){
        return new ResponseEntity<>(accountService.saveToAccount(accountNumber, amount), HttpStatus.OK);
    }

    @GetMapping("/transactions")
    public ResponseEntity<ResponseDto<Object>> getTransactions(@RequestParam String accountNumber){
        return new ResponseEntity<>(accountService.getUserTransactions(accountNumber), HttpStatus.OK);
    }

}

