package app.banksystem.controller;

import app.banksystem.model.Transaction;
import app.banksystem.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<Transaction> history(@RequestParam String accountNumber) {
        return transactionService.getAccountTransactions(accountNumber);
    }

    @GetMapping("/range")
    public List<Transaction> historyRange(@RequestParam String accountNumber,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return transactionService.getAccountTransactions(accountNumber, start, end);
    }
}