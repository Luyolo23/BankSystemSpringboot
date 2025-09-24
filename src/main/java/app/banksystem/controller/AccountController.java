package app.banksystem.controller;

import app.banksystem.model.Account;

import app.banksystem.model.Transaction;
import app.banksystem.repository.AccountRepository;
import app.banksystem.repository.CustomerRepository;
import app.banksystem.repository.TransactionRepository;
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

    //transfer
    @PostMapping("/transfer")
    public ResponseEntity<Account> transfer(@RequestParam Long fromAccountId, @RequestParam Long toAccountId, @RequestParam double amount) {
        Account from = accountRepository.findById(fromAccountId).orElse(null);
        Account to = accountRepository.findById(toAccountId).orElse(null);
        if (from == null || to == null) return ResponseEntity.badRequest().body("Invalid account ID(s)");
        if (from.getBalance() < amount) return ResponseEntity.badRequest().body("Insufficient funds");

        // perform transfer
        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        accountRepository.save(from);
        accountRepository.save(to);

        // record transactions
        transactionRepository.save(new Transaction(from, "TRANSFER_OUT", amount));
        transactionRepository.save(new Transaction(to, "TRANSFER_IN", amount));

        return ResponseEntity.ok("Transfer successful");
    }

}
