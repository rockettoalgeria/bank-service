package com.example.bankservice.repository;

import com.example.bankservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findOneById(UUID id);
}
