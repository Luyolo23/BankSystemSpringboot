package app.banksystem.repository;

import app.banksystem.model.Account;
import app.banksystem.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{

    List<Account> findByCustomer(Customer customer);
    Optional<Account> findByAccountNumber(String accountNumber);
}
