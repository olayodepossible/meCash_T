package com.possible.meCash.service.impl;

import com.possible.task.dto.req.AccountType;
import com.possible.task.dto.req.TransactionDto;
import com.possible.task.dto.response.ResponseDto;
import com.possible.task.exceptiion.AccountNotFoundException;
import com.possible.task.model.Account;
import com.possible.task.model.AppUser;
import com.possible.task.model.Transaction;
import com.possible.task.repository.AccountRepository;
import com.possible.task.repository.TransactionRepository;
import com.possible.task.repository.UserRepository;
import com.possible.task.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private static final String ACCT_NOT_FOUND = "Account with number %s not found";
    @Override
    public ResponseDto balanceEnquiry(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new AccountNotFoundException(String.format(ACCT_NOT_FOUND, accountNumber)));

        return ResponseDto.builder()
                .statusCode(200)
                .responseMessage("User balance retrieved successfully")
                .data(account.getBalance())
                .build();
    }


    @Override
    public ResponseDto getUserTransactions(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new AccountNotFoundException(String.format("Account with Account number %s not found", accountNumber)));
        List<Transaction> transactions = transactionRepository.findAllTransactionByAccountId(account.getId());
        if (transactions.isEmpty()){
            return ResponseDto.builder()
                    .statusCode(200)
                    .responseMessage("User Transaction history is empty")
                    .data(transactions)
                    .build();
        }
        return ResponseDto.builder()
                .statusCode(200)
                .responseMessage("User Transactions retrieved successfully")
                .data(transactions)
                .build();
    }

    public Page<Transaction> getAccountStatement(String accountNumber, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return transactionRepository.findByAccount_AccountNumberAndPostedDateBetween(account.getAccountNumber(), startDate, endDate, pageable);
    }

    @Transactional
    @Override
    public ResponseDto creditOrDebitAccountTransaction(TransactionDto transactionDto) {
        Account initiator = accountRepository.findByAccountNumber(transactionDto.getInitiatorAccount())
                .orElseThrow(() -> new AccountNotFoundException(String.format(ACCT_NOT_FOUND, transactionDto.getInitiatorAccount())));

        Account beneficiary = accountRepository.findByAccountNumber(transactionDto.getBeneficiaryAccount())
                .orElseThrow(() -> new AccountNotFoundException(String.format(ACCT_NOT_FOUND, transactionDto.getBeneficiaryAccount())));

        BigDecimal tranxAmount = transactionDto.getAmount();
        BigDecimal acctToCreditBalance = beneficiary.getBalance().add(tranxAmount);


        Transaction transaction;
        String message = "Insufficient Balance";
        if (transactionDto.getTransactionType().equalsIgnoreCase("DEBIT")) {
            BigDecimal previousBalance = initiator.getBalance();
            if ( previousBalance.compareTo(tranxAmount) > 0){
                BigDecimal availableBalance = initiator.getBalance().subtract(tranxAmount);
                transaction = Transaction.builder()
                        .account(initiator)
                        .credit(BigDecimal.ZERO)
                        .debit(tranxAmount)
                        .amount(tranxAmount)
                        .balance(availableBalance)
                        .beneficiaryAcct(transactionDto.getBeneficiaryAccount())
                        .senderAcct(transactionDto.getInitiatorAccount())
                        .narration(transactionDto.getDescription())
                        .build();
                initiator.setBalance(availableBalance);
                message = String.format("Credit Account number %s and Debit Account number %s",transactionDto.getBeneficiaryAccount(),  transactionDto.getInitiatorAccount());
            }
            else {
                return ResponseDto.builder()
                        .statusCode(200)
                        .responseMessage(message)
                        .data(List.of())
                        .build();
            }

        }
        else {
            transaction = Transaction.builder()
                    .account(initiator)
                    .credit(tranxAmount)
                    .debit(BigDecimal.ZERO)
                    .amount(tranxAmount)
                    .balance(acctToCreditBalance)
                    .beneficiaryAcct(transactionDto.getBeneficiaryAccount())
                    .senderAcct(transactionDto.getInitiatorAccount())
                    .narration(transactionDto.getDescription())
                    .build();

            initiator.setBalance(acctToCreditBalance);
            message = String.format("Credit Account number %s and Debit Account number %s",transactionDto.getBeneficiaryAccount(),  transactionDto.getInitiatorAccount());

        }

        accountRepository.save(initiator);
        Transaction savedTransaction = transactionRepository.save(transaction);

        return ResponseDto.builder()
                .statusCode(200)
                .responseMessage(message)
                .data(savedTransaction)
                .build();
    }

    @Override
    public ResponseDto activateAccount(Long userId, Long accountId) {
        AppUser user = userRepository.findById(userId).orElseThrow( () -> new UsernameNotFoundException("User not found"));
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if(user.getAccountList().contains(account)){
            account.setStatus("ACTIVE");
            Account updatedAcct = accountRepository.save(account);
            return ResponseDto.builder()
                    .statusCode(200)
                    .responseMessage("Deactivated Account for User with id: "+userId)
                    .data(updatedAcct)
                    .build();
        }

        return ResponseDto.builder()
                .statusCode(200)
                .responseMessage("Error deactivating Account for User with id: "+userId)
                .data("")
                .build();
    }

    @Override
    public ResponseDto deactivateUserAccount(Long userId, Long accountId) {
        AppUser user = userRepository.findById(userId).orElseThrow( () -> new UsernameNotFoundException("User not found"));
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if(user.getAccountList().contains(account)){
            account.setStatus("INACTIVE");
            Account updatedAcct = accountRepository.save(account);
            return ResponseDto.builder()
                    .statusCode(200)
                    .responseMessage("Deactivated Account for User with id: "+userId)
                    .data(updatedAcct)
                    .build();
        }

        return ResponseDto.builder()
                .statusCode(200)
                .responseMessage("Error deactivating Account for User with id: "+userId)
                .data("")
                .build();
    }

    @Override
    @Transactional
    public ResponseDto saveToAccount(String accountNumber, Long amount) {
        Account initiator = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(String.format(ACCT_NOT_FOUND, accountNumber)));
        BigDecimal acctToCreditBalance = initiator.getBalance().add(BigDecimal.valueOf(amount));

        Transaction transaction = Transaction.builder()
                .account(initiator)
                .credit(BigDecimal.valueOf(amount))
                .debit(BigDecimal.ZERO)
                .amount(BigDecimal.valueOf(amount))
                .balance(acctToCreditBalance)
                .beneficiaryAcct(accountNumber)
                .senderAcct(accountNumber)
                .narration("Credit account by self")
                .build();

        initiator.setBalance(acctToCreditBalance);
        String message = String.format("Credit Account number %s successfully",  accountNumber);
        accountRepository.save(initiator);
        Transaction savedTransaction = transactionRepository.save(transaction);

        return ResponseDto.builder()
                .statusCode(200)
                .responseMessage(message)
                .data(savedTransaction)
                .build();
    }

    @Override
    public ResponseDto withdrawFromAccount(String accountNumber, Long amount) {

        Account initiator = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(String.format(ACCT_NOT_FOUND, accountNumber)));

        BigDecimal tranxAmount = BigDecimal.valueOf(amount);
        String message = "Insufficient Balance";
        BigDecimal previousBalance = initiator.getBalance();
        if ( previousBalance.compareTo(tranxAmount) > 0){
            BigDecimal availableBalance = initiator.getBalance().subtract(tranxAmount);
            Transaction transaction = Transaction.builder()
                    .account(initiator)
                    .credit(BigDecimal.ZERO)
                    .debit(tranxAmount)
                    .amount(tranxAmount)
                    .balance(availableBalance)
                    .beneficiaryAcct(accountNumber)
                    .senderAcct(accountNumber)
                    .narration("Debit account by self")
                    .build();
            initiator.setBalance(availableBalance);
            message = String.format("Debit Account number %s", accountNumber);

            accountRepository.save(initiator);
            Transaction savedTransaction = transactionRepository.save(transaction);

            return ResponseDto.builder()
                    .statusCode(200)
                    .responseMessage(message)
                    .data(savedTransaction)
                    .build();
        }

        return ResponseDto.builder()
                .statusCode(400)
                .responseMessage(message)
                .data(null)
                .build();
    }


    @Override
    public ResponseDto getAllActiveAccountList() {
        return ResponseDto.builder()
                .statusCode(200)
                .responseMessage("List of Accounts fetched successfully")
                .data(accountRepository.findAllActiveAccounts())
                .build();

    }

    @Override
    public ResponseDto getAllInActiveAccountList() {

        return ResponseDto.builder()
                .statusCode(200)
                .responseMessage("List of Accounts fetched successfully")
                .data(accountRepository.findAllInActiveAccounts())
                .build();

    }


    @Override
    public ResponseDto byAccType(AccountType accType) {

        return ResponseDto.builder()
                .statusCode(200)
                .responseMessage("List of Accounts fetched successfully")
                .data(accountRepository.findAllByAccountType(accType))
                .build();
    }

}
