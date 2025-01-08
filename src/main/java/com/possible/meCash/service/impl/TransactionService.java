package com.possible.mecash.service.impl;

import com.possible.mecash.dto.enums.TransactionType;
import com.possible.mecash.exceptiion.ResourceNotFoundException;
import com.possible.mecash.model.AppUser;
import com.possible.mecash.model.Transaction;
import com.possible.mecash.repository.TransactionRepository;
import com.possible.mecash.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    @Transactional
    public Transaction transfer(String fromAccountNumber, String toAccountNumber, double amount) {
        AppUser fromUser = userRepository.findByAccountNumber(fromAccountNumber).orElseThrow(()-> new ResourceNotFoundException("User Not found"));
        // provided is not an NIP transaction
        AppUser toUser = userRepository.findByAccountNumber(toAccountNumber).orElseThrow(()-> new ResourceNotFoundException("User Not found"));

//        double discount = discountCalculator.calculateDiscount(fromUser, amount, TransactionType.TRANSFER);
        double discount = 0;

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


    public List<Transaction> getTransactionHistory(String accountNumber) {
        AppUser user = userRepository.findByAccountNumber(accountNumber).orElseThrow(()-> new ResourceNotFoundException("User Not found"));
        return transactionRepository.findByAppUser(user);
    }
}
