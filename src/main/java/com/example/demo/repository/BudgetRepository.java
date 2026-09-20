package com.example.demo.repository;

import com.example.demo.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository
        extends JpaRepository<Budget, Long> {

    Optional<Budget> findByUserIdAndMonthAndYear(
            Long userId,
            Integer month,
            Integer year
    );
}
