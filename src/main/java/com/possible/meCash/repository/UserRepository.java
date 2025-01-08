package com.possible.meCash.repository;


import com.possible.task.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findByUsername(String email);
    Optional<AppUser> findByAccountNumber(String accountNumber);
}

