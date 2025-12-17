package app.banksystem.repository;

import app.banksystem.model.Transaction;
import app.banksystem.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount(Account account);
    List<Transaction> findByAccountAndTimestampBetween(Account account, LocalDateTime start, LocalDateTime end);
}
