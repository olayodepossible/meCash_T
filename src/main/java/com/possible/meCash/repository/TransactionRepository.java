package com.possible.mecash.repository;


import com.possible.mecash.dto.enums.TransactionType;
import com.possible.mecash.model.AppUser;
import com.possible.mecash.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByAccount_AccountNumberAndPostedDateBetween(
            String accountNumber, LocalDateTime start, LocalDateTime end, Pageable pageable
    );

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId")
    List<Transaction> findAllTransactionByAccountId(Long accountId);

    List<Transaction> findByAppUser(AppUser user);


    // Find transactions for a specific user within a given month and year
    @Query("SELECT t FROM Transaction t WHERE " +
            "t.appUser = :user " +
            "AND EXTRACT(MONTH FROM t.postedDate) = :month " +
            "AND EXTRACT(YEAR FROM t.postedDate) = :year")
    List<Transaction> findByAppUserAndMonthAndYear(
            @Param("user") AppUser user,
            @Param("month") Month month,
            @Param("year") int year
    );

    // Find transactions by type for a specific user
    List<Transaction> findByAppUserAndTransactionType(AppUser user, TransactionType type);

    // Count transactions for a user within a specific month

    @Query("SELECT COUNT(t) FROM Transaction t WHERE " +
            "t.appUser = :user " +
            "AND MONTH(t.postedDate) = :month " +
            "AND YEAR(t.postedDate) = :year")
    long countByUserAndMonth(
            @Param("user") AppUser user,
            @Param("month") int month,
            @Param("year") int year
    );
}

