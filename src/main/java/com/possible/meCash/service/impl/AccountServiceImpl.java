package com.possible.mecash.service.impl;

import com.possible.mecash.dto.enums.AccountType;
import com.possible.mecash.dto.enums.TransactionType;
import com.possible.mecash.dto.req.TransactionDto;
import com.possible.mecash.dto.req.TransactionReq;
import com.possible.mecash.dto.response.ResponseDto;
import com.possible.mecash.dto.response.TransactionResp;
import com.possible.mecash.exceptiion.AccountNotFoundException;
import com.possible.mecash.exceptiion.ResourceNotFoundException;
import com.possible.mecash.model.Account;
import com.possible.mecash.model.AppUser;
import com.possible.mecash.model.Transaction;
import com.possible.mecash.repository.AccountRepository;
import com.possible.mecash.repository.TransactionRepository;
import com.possible.mecash.repository.UserRepository;
import com.possible.mecash.service.AccountService;
import com.possible.mecash.utils.AccountUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


    /*@Override
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
    }*/

    @Override
    public ResponseDto activateAccount(Long userId, Long accountId) {
        AppUser user = userRepository.findById(userId).orElseThrow( () -> new ResourceNotFoundException("User not found"));
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
        AppUser user = userRepository.findById(userId).orElseThrow( () -> new ResourceNotFoundException("User not found"));
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

    private ResponseDto<Object> getLoginUser(){
        AppUser userDetails = (AppUser) AccountUtil.getCurrentUserDetails();
        if (userDetails == null){
            return ResponseDto.builder()
                    .statusCode(401)
                    .responseMessage("unauthorized user")
                    .data(null)
                    .build();
        }
        return ResponseDto.builder().data(userDetails).build();
    }
    @Override
    @Transactional
    public ResponseDto saveToAccount(TransactionReq req) {
        AppUser userDetails = (AppUser) getLoginUser().getData();
        String accountNumber = req.getDestinationAccount();
        Account beneficiaryAcct = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(String.format(ACCT_NOT_FOUND, accountNumber)));
        BigDecimal creditAcctBalance = beneficiaryAcct.getBalance().add(req.getAmount());

        Transaction transaction = Transaction.builder()
                .credit(req.getAmount())
                .debit(BigDecimal.ZERO)
                .amount(req.getAmount())
                .balance(creditAcctBalance)
                .beneficiaryAcct(req.getDestinationAccount())
                .senderAcct(req.getSourceAccount())
                .transactionType(TransactionType.CREDIT)
                .transactionRef(AccountUtil.generateTransactionRef())
                .appUser(userDetails)
                .narration(req.getNarration().isEmpty()? "Credit account by self" : req.getNarration())
                .status("SUCCESS")
                .build();

        beneficiaryAcct.setBalance(creditAcctBalance);
        String message = String.format("Credit Account number %s successfully",  accountNumber);
        accountRepository.save(beneficiaryAcct);
        Transaction savedTransaction = transactionRepository.save(transaction);
        TransactionResp resp = convertToTransactionResp(savedTransaction);

        return ResponseDto.builder()
                .statusCode(200)
                .responseMessage(message)
                .data(resp)
                .build();

        //TODO: additional check for failure and track the failure record in DB
    }

    @Override
    public ResponseDto withdrawFromAccount(TransactionReq req) {
        String initiatorAcctNum = req.getSourceAccount();
        String beneficiaryAcctNum = req.getDestinationAccount();
        BigDecimal tranxAmount = req.getAmount();

        Account initiatorAcct = accountRepository.findByAccountNumber(initiatorAcctNum)
                .orElseThrow(() -> new AccountNotFoundException(String.format(ACCT_NOT_FOUND, initiatorAcctNum)));

        Account beneficiaryAcct = accountRepository.findByAccountNumber(beneficiaryAcctNum)
                .orElseThrow(() -> new AccountNotFoundException(String.format(ACCT_NOT_FOUND, beneficiaryAcctNum)));


        String message = "Insufficient Balance";
        BigDecimal previousBalance = initiatorAcct.getBalance();
        if ( previousBalance.compareTo(tranxAmount) > 0){
            Transaction transaction = performTransaction(initiatorAcct, beneficiaryAcct, req);

            accountRepository.save(initiatorAcct);
            accountRepository.save(beneficiaryAcct);
            Transaction savedTransaction = transactionRepository.save(transaction);

            //TODO: Transaction notification can be triggered here

            TransactionDto dto = TransactionDto.builder()
                    .initiatorAccount(savedTransaction.getSenderAcct())
                    .beneficiaryAccount(savedTransaction.getBeneficiaryAcct())
                    .amount(savedTransaction.getAmount())
                    .description(savedTransaction.getNarration())
                    .transactionType(savedTransaction.getTransactionType().name())
                    .ref(savedTransaction.getTransactionRef())
                    .build();
            message = String.format("Debit Account number %s and credit %s", initiatorAcctNum, beneficiaryAcctNum);
            return ResponseDto.builder()
                    .statusCode(200)
                    .responseMessage(message)
                    .data(dto)
                    .build();
        }

        return ResponseDto.builder()
                .statusCode(400)
                .responseMessage(message)
                .data(null)
                .build();
    }

    private Transaction performTransaction(Account initiatorAcct, Account beneficiaryAcct, TransactionReq req){
        AppUser userDetails = (AppUser) getLoginUser().getData();
        String initiatorAcctNum = req.getSourceAccount();
        String beneficiaryAcctNum = req.getDestinationAccount();
        BigDecimal tranxAmount = req.getAmount();

        BigDecimal availableBalance = initiatorAcct.getBalance().subtract(tranxAmount);
        BigDecimal creditAcctBal = beneficiaryAcct.getBalance().add(tranxAmount);
        Transaction transaction = Transaction.builder()
                .credit(BigDecimal.ZERO)
                .debit(tranxAmount)
                .amount(tranxAmount)
                .balance(availableBalance)
                .beneficiaryAcct(beneficiaryAcctNum)
                .senderAcct(initiatorAcctNum)
                .transactionType(req.getTransactionType().name().isEmpty()? TransactionType.DEBIT : req.getTransactionType())
                .transactionRef(AccountUtil.generateTransactionRef())
                .narration(req.getNarration().isEmpty()? "Debit account by self" : req.getNarration())
                .appUser(userDetails)
                .status("SUCCESS")
                .build();
        initiatorAcct.setBalance(availableBalance);
        beneficiaryAcct.setBalance(creditAcctBal);
        return transaction;
    }

    @Transactional
    @Override
    public ResponseDto walletTransfer(TransactionReq req) {
        String sourceWalletAcctNum = req.getSourceAccount();
        String beneficiaryWalletAcctNum = req.getDestinationAccount();
        BigDecimal tranxAmount = req.getAmount();

        Account sourceWallet = accountRepository.findByAccountNumber(sourceWalletAcctNum)
                .orElseThrow(() -> new AccountNotFoundException(String.format(ACCT_NOT_FOUND, sourceWalletAcctNum)));

        Account targetWallet = accountRepository.findByAccountNumber(beneficiaryWalletAcctNum)
                .orElseThrow(() -> new AccountNotFoundException(String.format(ACCT_NOT_FOUND, beneficiaryWalletAcctNum)));

        String message = "Insufficient Balance";
        if (!sourceWallet.getAccountCurrency().name().equals(req.getDestinationCurrency())) {
            //TODO: currency conversion: an external API can be called here
            Map<String, Double> exchangeRate = new HashMap<>();
            exchangeRate.put("NAIRA_DOLLAR", 0.5);
            exchangeRate.put("NAIRA_POUNDS", 0.6);
            exchangeRate.put("NAIRA_CAD", 0.4);
            exchangeRate.put("DOLLAR_POUNDS", 0.7);
            exchangeRate.put("DOLLAR_CAD", 0.45);
            double conversionAmount =AccountUtil.convertCurrency(tranxAmount.doubleValue(), sourceWallet.getAccountCurrency().name(), targetWallet.getAccountCurrency().name(), exchangeRate);
            tranxAmount = BigDecimal.valueOf(conversionAmount);
        }

        //TODO NIP call and validation
        if (sourceWallet.getBalance().compareTo(tranxAmount) < 0) {
            Transaction transaction = performTransaction(sourceWallet, targetWallet, req);

            accountRepository.save(sourceWallet);
            accountRepository.save(targetWallet);
            Transaction savedTransaction = transactionRepository.save(transaction);

            //TODO: Transaction notification can be triggered here

            TransactionDto dto = TransactionDto.builder()
                    .initiatorAccount(savedTransaction.getSenderAcct())
                    .beneficiaryAccount(savedTransaction.getBeneficiaryAcct())
                    .amount(savedTransaction.getAmount())
                    .description(savedTransaction.getNarration())
                    .transactionType(savedTransaction.getTransactionType().name())
                    .ref(savedTransaction.getTransactionRef())
                    .build();
            message = String.format("Debit Account number %s and credit %s", sourceWalletAcctNum, beneficiaryWalletAcctNum);
            return ResponseDto.builder()
                    .statusCode(200)
                    .responseMessage(message)
                    .data(dto)
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
    public ResponseDto getAccountByAccType(AccountType accType) {

        return ResponseDto.builder()
                .statusCode(200)
                .responseMessage("List of Accounts fetched successfully")
                .data(accountRepository.findAllByAccountType(accType))
                .build();
    }


    public List<TransactionResp> getTransactionHistory(String accountNumber) {
        AppUser user = (AppUser) getLoginUser().getData();
        List<Transaction> transactions = transactionRepository.findByAppUser(user);
        //TODO: Filter transactions by accountNumber
        return  transactions.stream()
                .map(this::convertToTransactionResp)
                .collect(Collectors.toList());

    }


    private TransactionResp convertToTransactionResp(Transaction transaction) {
        return  TransactionResp.builder()
                .sender(transaction.getSenderAcct())
                .beneficiary(transaction.getBeneficiaryAcct())
                .amount(transaction.getAmount())
                .narration(transaction.getNarration())
                .status(transaction.getStatus())
                .valueDate(transaction.getValueDate())
                .transactionType(transaction.getTransactionType())
                .transactionRef(transaction.getTransactionRef())
                .build();


    }

}
