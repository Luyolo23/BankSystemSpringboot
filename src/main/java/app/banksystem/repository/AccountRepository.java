package app.banksystem.repository;

import app.banksystem.model.Account;
import app.banksystem.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{

    List<Account> findByCustomer(Customer customer);
}
