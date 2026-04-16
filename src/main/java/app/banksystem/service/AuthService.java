package app.banksystem.service;

import app.banksystem.dto.LoginRequest;
import app.banksystem.model.Customer;
import app.banksystem.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Customer register(Customer customer) {
        // Encode password before saving
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        return customerRepository.save(customer);
    }

    public Optional<Customer> login(LoginRequest loginRequest) {
        Optional<Customer> customerOpt = customerRepository.findByUsername(loginRequest.getUsername());
        
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), customer.getPassword())) {
                return Optional.of(customer);
            }
        }
        return Optional.empty();
    }
}
