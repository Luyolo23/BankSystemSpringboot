package app.banksystem.controller;

import app.banksystem.dto.AccountResponse;
import app.banksystem.dto.TransferRequest;
import app.banksystem.model.Account;
import app.banksystem.service.AccountService;
import app.banksystem.service.CustomerService;
import app.banksystem.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final CustomerService customerService;

    public AccountController(AccountService accountService, 
                             TransactionService transactionService, 
                             CustomerService customerService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.customerService = customerService;
    }

    // Creates an account for a customer
    @PostMapping("/create/{customerId}")
    public ResponseEntity<AccountResponse> createAccount(@PathVariable Long customerId, @RequestBody Account account) {
        return customerService.getCustomerById(customerId)
                .map(customer -> {
                    account.setCustomer(customer);
                    if (account.getBalance() == null) {
                        account.setBalance(BigDecimal.ZERO);
                    }

                    Account saved = accountService.save(account);
                    return ResponseEntity.ok(new AccountResponse(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all accounts
    @GetMapping
    public List<AccountResponse> getAllAccounts() {
        return accountService.findAll().stream()
                .map(AccountResponse::new)
                .collect(Collectors.toList());
    }

    // Get accounts by customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountResponse>> getAccountsByCustomer(@PathVariable Long customerId) {
        return customerService.getCustomerById(customerId)
                .map(customer -> {
                    List<AccountResponse> accounts = accountService.findByCustomer(customer).stream()
                            .map(AccountResponse::new)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(accounts);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Deposit
    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<?> deposit(@PathVariable String accountNumber, @RequestParam BigDecimal amount) {
        try {
            transactionService.deposit(accountNumber, amount);
            Account account = accountService.findByAccountNumber(accountNumber);
            return ResponseEntity.ok(new AccountResponse(account));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Withdraw
    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable String accountNumber, @RequestParam BigDecimal amount) {
        try {
            transactionService.withdraw(accountNumber, amount);
            Account account = accountService.findByAccountNumber(accountNumber);
            return ResponseEntity.ok(new AccountResponse(account));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Transfer
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
        try {
            transactionService.transfer(request.getFromAccountNumber(), 
                                       request.getToAccountNumber(), 
                                       request.getAmount());
            return ResponseEntity.ok("Transfer successful");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
