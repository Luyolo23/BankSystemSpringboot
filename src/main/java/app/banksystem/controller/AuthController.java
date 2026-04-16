package app.banksystem.controller;

import app.banksystem.dto.LoginRequest;
import app.banksystem.model.Customer;
import app.banksystem.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Customer> register(@RequestBody Customer customer) {
        Customer savedCustomer = authService.register(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest)
                .<ResponseEntity<?>>map(customer -> ResponseEntity.ok(customer))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password"));
    }
}
