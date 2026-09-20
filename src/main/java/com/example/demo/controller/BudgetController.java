package com.example.demo.controller;

import com.example.demo.model.Budget;
import com.example.demo.model.User;
import com.example.demo.repository.BudgetRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.BudgetService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/budgets")
@CrossOrigin(origins = "*")
public class BudgetController {

    private final BudgetService budgetService;
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;

    public BudgetController(
            BudgetService budgetService,
            UserRepository userRepository,
            BudgetRepository budgetRepository) {

        this.budgetService = budgetService;
        this.userRepository = userRepository;
        this.budgetRepository = budgetRepository;
    }

    // GET CURRENT USER'S BUDGET
    @GetMapping("/{year}/{month}")
    public ResponseEntity<?> getBudget(
            @PathVariable Integer year,
            @PathVariable Integer month,
            Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        Optional<Budget> budget =
                budgetService.getBudget(
                        user.getId(),
                        month,
                        year
                );

        if (budget.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
        new BudgetResponse(budget.get())
);
    }

    // CREATE OR UPDATE BUDGET
    @PostMapping
    public ResponseEntity<?> saveBudget(
            @RequestBody BudgetRequest request,
            Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        Optional<Budget> existing =
                budgetService.getBudget(
                        user.getId(),
                        request.getMonth(),
                        request.getYear()
                );

        Budget budget;

        if (existing.isPresent()) {
            budget = existing.get();
            budget.setAmount(request.getAmount());
        } else {
            budget = new Budget(
                    user,
                    request.getMonth(),
                    request.getYear(),
                    request.getAmount()
            );
        }

        Budget saved = budgetService.saveBudget(budget);

        return ResponseEntity.ok(
        new BudgetResponse(saved)
);
    }

    // DELETE BUDGET
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(
            @PathVariable Long id,
            Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        Optional<Budget> budget =
                budgetRepository.findById(id);

        if (budget.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Security check:
        // User can delete only their own budget
        if (!budget.get().getUser().getId()
                .equals(user.getId())) {

            return ResponseEntity.status(403)
                    .body("You cannot delete this budget");
        }

        budgetService.deleteBudget(id);

        return ResponseEntity.ok(
                "Budget deleted successfully"
        );
    }

    // REQUEST DTO
    public static class BudgetRequest {

        private Integer month;
        private Integer year;
        private BigDecimal amount;

        public Integer getMonth() {
            return month;
        }

        public void setMonth(Integer month) {
            this.month = month;
        }

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }
public static class BudgetResponse {

    private Long id;
    private Integer month;
    private Integer year;
    private BigDecimal amount;

    public BudgetResponse(Budget budget) {
        this.id = budget.getId();
        this.month = budget.getMonth();
        this.year = budget.getYear();
        this.amount = budget.getAmount();
    }

    public Long getId() {
        return id;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getYear() {
        return year;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
}
