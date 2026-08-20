package app.banksystem.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable = false)
    private String accountNumber;
    private String type;
    private BigDecimal balance;

    // Many accounts can belong to one customer
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference
    private Customer customer;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Transaction> tranactions = new ArrayList<>();
    public Account() {}

    public Account(String accountNumber, String type, BigDecimal balance, Customer customer) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.balance = balance;
        this.customer = customer;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public List<Transaction> getTranactions() {
        return tranactions;
    }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}
