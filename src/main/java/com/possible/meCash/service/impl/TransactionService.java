package com.possible.meCash.service.impl;

import com.possible.task.dto.enums.TransactionType;
import com.possible.task.dto.req.AirTimeReq;
import com.possible.task.exceptiion.ResourceNotFoundException;
import com.possible.task.model.AppUser;
import com.possible.task.model.Transaction;
import com.possible.task.repository.TransactionRepository;
import com.possible.task.repository.UserRepository;
import com.possible.task.utils.DiscountCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private DiscountCalculator discountCalculator;

    @Transactional
    public Transaction transfer(String fromAccountNumber, String toAccountNumber, @DecimalMin(value = "0.01", message = "Amount must be greater than 0") double amount) {
        AppUser fromUser = userRepository.findByAccountNumber(fromAccountNumber).orElseThrow(()-> new ResourceNotFoundException("User Not found"));
        // provided is not an NIP transaction
        AppUser toUser = userRepository.findByAccountNumber(toAccountNumber).orElseThrow(()-> new ResourceNotFoundException("User Not found"));

        double discount = discountCalculator.calculateDiscount(fromUser, amount, TransactionType.TRANSFER);

        BigDecimal discountedAmount = BigDecimal.valueOf(amount * (1 - discount));

        // Perform transfer logic
        fromUser.setAccountBalance(fromUser.getAccountBalance().subtract(discountedAmount));
        toUser.setAccountBalance(toUser.getAccountBalance().add(discountedAmount));

        Transaction transaction = new Transaction();
        transaction.setAppUser(fromUser);
        transaction.setAmount(BigDecimal.valueOf(amount));
        transaction.setDiscountApplied(discount);
        transaction.setTransactionType(TransactionType.TRANSFER);

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction buyAirtime(AirTimeReq req) {
        AppUser user = userRepository.findByAccountNumber(req.getSourceAccount()).orElseThrow(()-> new ResourceNotFoundException("User Not found"));

        Transaction transaction = new Transaction();
        transaction.setAppUser(user);
        transaction.setAmount(BigDecimal.valueOf(Double.parseDouble(req.getAmount())));
        transaction.setTransactionType(TransactionType.AIRTIME_RECHARGE);
//TODO implement NetworK provoder api contract later based on their API Doc.
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionHistory(String accountNumber) {
        AppUser user = userRepository.findByAccountNumber(accountNumber).orElseThrow(()-> new ResourceNotFoundException("User Not found"));
        return transactionRepository.findByAppUser(user);
    }
}
