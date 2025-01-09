package com.possible.mecash.service;


import com.possible.mecash.dto.enums.AccountCurrency;
import com.possible.mecash.dto.enums.TransactionType;
import com.possible.mecash.dto.req.TransactionReq;
import com.possible.mecash.dto.response.ResponseDto;
import com.possible.mecash.dto.response.TransactionResp;
import com.possible.mecash.exceptiion.AccountNotFoundException;
import com.possible.mecash.model.Account;
import com.possible.mecash.model.AppUser;
import com.possible.mecash.model.Transaction;
import com.possible.mecash.repository.AccountRepository;
import com.possible.mecash.repository.TransactionRepository;
import com.possible.mecash.repository.UserRepository;
import com.possible.mecash.service.impl.AccountServiceImpl;
import com.possible.mecash.utils.AccountUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AccountServiceImplTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void balanceEnquiry_WhenAccountExists_ReturnsBalance() {
        String accountNumber = "1234567890";
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .balance(BigDecimal.valueOf(1000))
                .build();

        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));

        ResponseDto response = accountService.balanceEnquiry(accountNumber);

        assertEquals(200, response.getStatusCode());
        assertEquals(BigDecimal.valueOf(1000), response.getData());
    }

    @Test
    void balanceEnquiry_WhenAccountNotFound_ThrowsException() {
        String accountNumber = "1234567890";
        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> accountService.balanceEnquiry(accountNumber));
    }

    @Test
    void saveToAccount_Success() {
        String accountNumber = "1234567890";
        BigDecimal amount = BigDecimal.valueOf(100);
        AppUser user = AppUser.builder().id(1L).build();
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .balance(BigDecimal.valueOf(1000))
                .build();

        TransactionReq request = TransactionReq.builder()
                .destinationAccount(accountNumber)
                .amount(amount)
                .narration("test")
                .build();

        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));
        when(transactionRepository.save(any())).thenReturn(Transaction.builder().build());

        ResponseDto response = accountService.saveToAccount(request);

        assertEquals(200, response.getStatusCode());
        verify(accountRepository).save(any(Account.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void withdrawFromAccount_WithSufficientBalance_Success() {
        String sourceAccount = "1234567890";
        String destAccount = "0987654321";
        BigDecimal amount = BigDecimal.valueOf(100);
        AppUser user = AppUser.builder().id(1L).build();

        Account sourceAcc = Account.builder()
                .accountNumber(sourceAccount)
                .balance(BigDecimal.valueOf(1000))
                .accountCurrency(AccountCurrency.NAIRA)
                .build();

        Account destAcc = Account.builder()
                .accountNumber(destAccount)
                .balance(BigDecimal.valueOf(500))
                .accountCurrency(AccountCurrency.NAIRA)
                .build();

        TransactionReq request = TransactionReq.builder()
                .sourceAccount(sourceAccount)
                .destinationAccount(destAccount)
                .destinationCurrency("NAIRA")
                .transactionType(TransactionType.DEBIT)
                .narration("test")
                .amount(amount)
                .build();

        Transaction mockedTransaction = mock(Transaction.class);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockedTransaction);
        when(mockedTransaction.getSenderAcct()).thenReturn(AccountUtil.generateAccountNumber());
        when(mockedTransaction.getTransactionType()).thenReturn(TransactionType.DEBIT);

        when(accountRepository.findByAccountNumber(sourceAccount))
                .thenReturn(Optional.of(sourceAcc));
        when(accountRepository.findByAccountNumber(destAccount))
                .thenReturn(Optional.of(destAcc));

        ResponseDto response = accountService.withdrawFromAccount(request);

        assertEquals(200, response.getStatusCode());
        verify(accountRepository, times(2)).save(any(Account.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void withdrawFromAccount_WithInsufficientBalance_ReturnError() {
        String sourceAccount = "1234567890";
        String destAccount = "0987654321";
        BigDecimal amount = BigDecimal.valueOf(2000);

        Account sourceAcc = Account.builder()
                .accountNumber(sourceAccount)
                .balance(BigDecimal.valueOf(1000))
                .build();

        Account destAcc = Account.builder()
                .accountNumber(destAccount)
                .balance(BigDecimal.valueOf(500))
                .build();

        TransactionReq request = TransactionReq.builder()
                .sourceAccount(sourceAccount)
                .destinationAccount(destAccount)
                .amount(amount)
                .build();

        when(accountRepository.findByAccountNumber(sourceAccount))
                .thenReturn(Optional.of(sourceAcc));
        when(accountRepository.findByAccountNumber(destAccount))
                .thenReturn(Optional.of(destAcc));

        ResponseDto response = accountService.withdrawFromAccount(request);

        assertEquals(400, response.getStatusCode());
        assertEquals("Insufficient Balance", response.getResponseMessage());
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }


}
