package app.banksystem.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    @JsonBackReference
    private Account account;

    private String type; // "DEPOSIT", "WITHDRAW", "TRANSFER"
    private BigDecimal amount;
    private LocalDateTime timestamp;

    public Transaction() {}

    public Transaction(Account account, String type, BigDecimal amount) {
        this.account = account;
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }


}
