package com.example.demo.controller;
import jakarta.validation.Valid;
import com.example.demo.model.Transaction;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TransactionService;
import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

  public TransactionController(
        TransactionService transactionService,
        UserRepository userRepository,
        CategoryRepository categoryRepository) {

    this.transactionService = transactionService;
    this.userRepository = userRepository;
    this.categoryRepository = categoryRepository;
}


    // ==========================================
    // GET CURRENT USER'S TRANSACTIONS
    // ==========================================
@GetMapping
public ResponseEntity<List<TransactionResponse>> getMyTransactions(
        Authentication authentication) {

    User user = userRepository
            .findByEmail(authentication.getName())
            .orElseThrow();

    List<Transaction> transactions =
            transactionService.getUserTransactions(
                    user.getId()
            );

    List<TransactionResponse> response =
            transactions.stream()
                    .map(TransactionResponse::new)
                    .toList();

    return ResponseEntity.ok(response);
}


    // ==========================================
    // ADD TRANSACTION
    // ==========================================

@PostMapping
public ResponseEntity<TransactionResponse> addTransaction(
        @Valid @RequestBody Transaction transaction,
        Authentication authentication) {

    User user = userRepository
            .findByEmail(authentication.getName())
            .orElseThrow();

    Category category =
            categoryRepository
                    .findById(
                            transaction.getCategory().getId()
                    )
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Category not found"
                            )
                    );

    if (!category.getType()
            .equalsIgnoreCase(transaction.getType())) {

        return ResponseEntity.badRequest()
                .body(null);
    }

    transaction.setCategory(category);
    transaction.setUser(user);

    Transaction saved =
        transactionService.addTransaction(transaction);

return ResponseEntity.ok(
        new TransactionResponse(saved)
);
        }


    // ==========================================
    // UPDATE TRANSACTION
    // ==========================================

  @PutMapping("/{id}")
public ResponseEntity<?> updateTransaction(
        @PathVariable Long id,
        @Valid @RequestBody Transaction transaction,
        Authentication authentication) {

    User user = userRepository
            .findByEmail(authentication.getName())
            .orElseThrow();

    Optional<Transaction> existing =
            transactionService.getUserTransactionById(
                    id,
                    user.getId()
            );

    if (existing.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    Category category =
            categoryRepository
                    .findById(
                            transaction.getCategory().getId()
                    )
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Category not found"
                            )
                    );

    if (!category.getType()
            .equalsIgnoreCase(transaction.getType())) {

        return ResponseEntity.badRequest()
                .body("Category type does not match transaction type");
    }

    Transaction existingTransaction = existing.get();

    existingTransaction.setAmount(transaction.getAmount());
    existingTransaction.setType(transaction.getType());
    existingTransaction.setDescription(transaction.getDescription());
    existingTransaction.setTransactionDate(
            transaction.getTransactionDate()
    );
    existingTransaction.setCategory(category);

Transaction updated =
        transactionService.updateTransaction(
                existingTransaction
        );

return ResponseEntity.ok(
        new TransactionResponse(updated)
);
        }


    // ==========================================
    // DELETE TRANSACTION
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(
            @PathVariable Long id,
            Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        Optional<Transaction> transaction =
                transactionService.getUserTransactionById(
                        id,
                        user.getId()
                );

        if (transaction.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        transactionService.deleteTransaction(id);

        return ResponseEntity.ok(
                "Transaction deleted successfully"
        );
    }
}
