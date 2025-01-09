package com.possible.mecash.service;


import com.possible.mecash.dto.enums.AccountType;
import com.possible.mecash.dto.req.TransactionDto;
import com.possible.mecash.dto.req.TransactionReq;
import com.possible.mecash.dto.response.ResponseDto;
import com.possible.mecash.dto.response.TransactionResp;
import com.possible.mecash.model.Transaction;

import java.util.List;

public interface AccountService {


//    ResponseDto getUserTransactions(String accountNumber);

    ResponseDto balanceEnquiry(String accountNumber);
    ;

    ResponseDto getAccountByAccType(AccountType accType);

    ResponseDto getAllInActiveAccountList();

    ResponseDto getAllActiveAccountList();

    ResponseDto activateAccount(Long userId, Long accountId);

    ResponseDto deactivateUserAccount(Long userId, Long accountId);


    ResponseDto saveToAccount(TransactionReq req);

    ResponseDto withdrawFromAccount(TransactionReq req);

    ResponseDto walletTransfer(TransactionReq req);

    List<TransactionResp> getTransactionHistory(String accountNumber);
}

