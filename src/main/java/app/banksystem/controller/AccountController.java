package app.banksystem.controller;

import app.banksystem.model.Account;

import app.banksystem.repository.AccountRepository;
import app.banksystem.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public AccountController(AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    // Create account for a customer
    @PostMapping("/create/{customerId}")
    public ResponseEntity<Account> createAccount(@PathVariable Long customerId, @RequestBody Account account) {
        return customerRepository.findById(customerId)
                .map(customer -> {
                    account.setCustomer(customer);
                    Account saved = accountRepository.save(account);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all accounts
    @GetMapping
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // Get accounts by customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Account>> getAccountsByCustomer(@PathVariable Long customerId) {
        return customerRepository.findById(customerId)
                .map(customer -> ResponseEntity.ok(accountRepository.findByCustomer(customer)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Deposit
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<Account> deposit(@PathVariable Long accountId, @RequestParam double amount) {
        return accountRepository.findById(accountId)
                .map(account -> {
                    account.setBalance(account.getBalance() + amount);
                    return ResponseEntity.ok(accountRepository.save(account));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Withdraw
    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable Long accountId, @RequestParam double amount) {
        return accountRepository.findById(accountId)
                .map(account -> {
                    if (account.getBalance() < amount) {
                        return ResponseEntity.badRequest().body("Insufficient funds");
                    }
                    account.setBalance(account.getBalance() - amount);
                    return ResponseEntity.ok(accountRepository.save(account));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
