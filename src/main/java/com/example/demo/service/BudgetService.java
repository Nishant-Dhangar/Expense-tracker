package com.example.demo.service;

import com.example.demo.model.Budget;
import com.example.demo.repository.BudgetRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public Optional<Budget> getBudget(
            Long userId,
            Integer month,
            Integer year) {

        return budgetRepository
                .findByUserIdAndMonthAndYear(
                        userId,
                        month,
                        year
                );
    }

    public Budget saveBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }
}