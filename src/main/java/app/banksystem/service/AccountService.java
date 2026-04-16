package app.banksystem.service;

import app.banksystem.model.Account;
import app.banksystem.model.Customer;
import app.banksystem.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service for managing bank accounts
 */
@Service
@Transactional // Ensures data consistency across operations
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
    }

    public BigDecimal getBalance(String accountNumber) {
        Account account = findByAccountNumber(accountNumber);
        return account.getBalance();
    }

    public void updateBalance(Account account, BigDecimal newBalance) {
        account.setBalance(newBalance);
        accountRepository.save(account);
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public List<Account> findByCustomer(Customer customer) {
        return accountRepository.findByCustomer(customer);
    }
}
