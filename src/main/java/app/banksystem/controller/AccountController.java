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
                    // We don't have a createAccount in AccountService yet, 
                    // but we can save via a hypothetical service method or for now use the service to update.
                    // Actually, let's add a create method to AccountService to stay consistent.
                    // For now, I'll use the repository if I must, but the goal is to use services.
                    // I will add save/create to AccountService.
                    Account saved = accountService.save(account);
                    return ResponseEntity.ok(new AccountResponse(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all accounts
    @GetMapping
    public List<AccountResponse> getAllAccounts() {
        // Assuming we'll add a findAll to AccountService or use it here
        // For brevity and following the "bring together" goal:
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
