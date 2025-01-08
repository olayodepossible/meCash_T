package com.possible.meCash.utils;

import com.possible.task.dto.enums.TransactionType;
import com.possible.task.dto.enums.UserType;
import com.possible.task.model.AppUser;
import com.possible.task.model.Transaction;
import com.possible.task.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DiscountCalculator {
    @Autowired
    private TransactionRepository transactionRepository;

    public double calculateDiscount(AppUser user, double amount, TransactionType type) {
        // No discount for airtime
        if (type == TransactionType.AIRTIME_RECHARGE) {
            return 0;
        }

        // Customer for over 4 years
        LocalDateTime now = LocalDateTime.now();
        long customerYears = java.time.Period.between(
                LocalDate.from(user.getCreatedAt()),
                now.toLocalDate()
        ).getYears();

        // First three monthly transactions for long-term customers
        List<Transaction> monthlyTransactions = transactionRepository.findByAppUserAndMonthAndYear(
                user, now.getMonth(), now.getYear()
        );

        if (customerYears >= 4 && monthlyTransactions.size() < 3) {
            return 0.10; // 10% discount
        }

        // Business/Retail-specific discounts
        if (user.getUserType() == UserType.BUSINESS &&
                amount > 150000 &&
                monthlyTransactions.size() >= 3) {
            return 0.27; // 27% for business
        }

        if (user.getUserType() == UserType.RETAIL &&
                amount > 50000 &&
                monthlyTransactions.size() >= 3) {
            return 0.18; // 18% for retail
        }

        return 0;
    }
}
