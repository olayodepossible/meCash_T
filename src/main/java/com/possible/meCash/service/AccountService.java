package com.possible.mecash.service;


import com.possible.mecash.dto.enums.AccountType;
import com.possible.mecash.dto.req.TransactionDto;
import com.possible.mecash.dto.response.ResponseDto;

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

