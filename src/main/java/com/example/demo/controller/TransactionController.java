package com.example.demo.controller;

import com.example.demo.model.Transaction;
import com.example.demo.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
@PutMapping("/{id}")
public ResponseEntity<Transaction> updateTransaction(
        @PathVariable Long id,
        @RequestBody Transaction transaction) {

    transaction.setId(id);

    return ResponseEntity.ok(
            transactionService.updateTransaction(transaction)
    );
}
    @PostMapping
    public ResponseEntity<Transaction> addTransaction(
            @RequestBody Transaction transaction) {

        return ResponseEntity.ok(
                transactionService.addTransaction(transaction)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getUserTransactions(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                transactionService.getUserTransactions(userId)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(
            @PathVariable Long id) {

        transactionService.deleteTransaction(id);

        return ResponseEntity.ok("Transaction deleted successfully");
    }
}
