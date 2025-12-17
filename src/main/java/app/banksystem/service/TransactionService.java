package app.banksystem.service;

import app.banksystem.model.Account;
import app.banksystem.model.Transaction;
import app.banksystem.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for handling financial transactions
 * All methods are transactional to ensure data consistency
 */
@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Transfer amount must be positive");
        }
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new RuntimeException("Cannot transfer to the same account");
        }
        Account from = accountService.findByAccountNumber(fromAccountNumber);
        Account to = accountService.findByAccountNumber(toAccountNumber);
        if (from.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance for transfer");
        }
        accountService.updateBalance(from, from.getBalance().subtract(amount));
        accountService.updateBalance(to, to.getBalance().add(amount));
        transactionRepository.save(new Transaction(from, "TRANSFER_OUT", amount));
        transactionRepository.save(new Transaction(to, "TRANSFER_IN", amount));
    }

    public void deposit(String accountNumber, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Deposit amount must be positive");
        }
        Account account = accountService.findByAccountNumber(accountNumber);
        accountService.updateBalance(account, account.getBalance().add(amount));
        transactionRepository.save(new Transaction(account, "DEPOSIT", amount));
    }

    public void withdraw(String accountNumber, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Withdrawal amount must be positive");
        }
        Account account = accountService.findByAccountNumber(accountNumber);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance for withdrawal");
        }
        accountService.updateBalance(account, account.getBalance().subtract(amount));
        transactionRepository.save(new Transaction(account, "WITHDRAW", amount));
    }

    public List<Transaction> getAccountTransactions(String accountNumber) {
        Account account = accountService.findByAccountNumber(accountNumber);
        return transactionRepository.findByAccount(account);
    }

    public List<Transaction> getAccountTransactions(String accountNumber, LocalDateTime startDate, LocalDateTime endDate) {
        Account account = accountService.findByAccountNumber(accountNumber);
        return transactionRepository.findByAccountAndTimestampBetween(account, startDate, endDate);
    }
}