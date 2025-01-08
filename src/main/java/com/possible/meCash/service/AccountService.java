package com.possible.meCash.service;


import com.possible.task.dto.req.AccountType;
import com.possible.task.dto.req.TransactionDto;
import com.possible.task.dto.response.ResponseDto;


public interface AccountService {


    ResponseDto getUserTransactions(String accountNumber);

    ResponseDto balanceEnquiry(String accountNumber);

    ResponseDto creditOrDebitAccountTransaction(TransactionDto transactionDto);

    ResponseDto byAccType(AccountType accType);

    ResponseDto getAllInActiveAccountList();

    ResponseDto getAllActiveAccountList();

    ResponseDto activateAccount(Long userId, Long accountId);

    ResponseDto deactivateUserAccount(Long userId, Long accountId);


    ResponseDto saveToAccount(String accountNumber, Long amount);

    ResponseDto withdrawFromAccount(String accountNumber, Long amount);

}

