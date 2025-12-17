package app.banksystem.controller;

import app.banksystem.model.Account;

import app.banksystem.model.Transaction;
import app.banksystem.repository.AccountRepository;
import app.banksystem.repository.CustomerRepository;
import app.banksystem.repository.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    public AccountController(AccountRepository accountRepository, CustomerRepository customerRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    // Creates an account for a customer
    @PostMapping("/create/{customerId}")
    public ResponseEntity<Account> createAccount(@PathVariable Long customerId, @RequestBody Account account) {
        return customerRepository.findById(customerId)
                .map(customer -> {
                    account.setCustomer(customer);
                    if (account.getBalance() == null) {
                        account.setBalance(BigDecimal.ZERO);
                    }
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
    @Transactional
    public ResponseEntity<?> deposit(@PathVariable Long accountId, @RequestParam BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Amount must be positive");
        }
        return accountRepository.findById(accountId)
                .map(account -> {
                    account.setBalance(account.getBalance().add(amount));
                    accountRepository.save(account);
                    transactionRepository.save(new Transaction(account, "DEPOSIT", amount));
                    return ResponseEntity.ok(account);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    // Withdraw
    @PostMapping("/{accountId}/withdraw")
    @Transactional
    public ResponseEntity<?> withdraw(@PathVariable Long accountId, @RequestParam BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Amount must be positive");
        }
        return accountRepository.findById(accountId)
                .map(account -> {
                    if (account.getBalance().compareTo(amount) < 0) {
                        return ResponseEntity.badRequest().body("Insufficient funds");
                    }
                    account.setBalance(account.getBalance().subtract(amount));
                    accountRepository.save(account);
                    transactionRepository.save(new Transaction(account, "WITHDRAW", amount));
                    return ResponseEntity.ok(account);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    //transfer
    @PostMapping("/transfer")
    @Transactional
    public ResponseEntity<?> transfer(@RequestParam Long fromAccountId,
                                      @RequestParam Long toAccountId,
                                      @RequestParam BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Amount must be positive");
        }
        Account from = accountRepository.findById(fromAccountId).orElse(null);
        Account to = accountRepository.findById(toAccountId).orElse(null);
        if (from == null || to == null) return ResponseEntity.badRequest().body("Invalid account ID(s)");
        if (from.getBalance().compareTo(amount) < 0) return ResponseEntity.badRequest().body("Insufficient funds");

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        accountRepository.save(from);
        accountRepository.save(to);

        transactionRepository.save(new Transaction(from, "TRANSFER_OUT", amount));
        transactionRepository.save(new Transaction(to, "TRANSFER_IN", amount));

        return ResponseEntity.ok("Transfer successful");
    }
}
