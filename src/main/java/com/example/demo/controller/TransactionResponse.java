package com.example.demo.controller;

import com.example.demo.model.Transaction;

import java.time.LocalDate;

public class TransactionResponse {

    private Long id;
    private double amount;
    private String type;
    private String description;
    private LocalDate transactionDate;

    private CategoryResponse category;

    public TransactionResponse(Transaction transaction) {

        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.type = transaction.getType();
        this.description = transaction.getDescription();
        this.transactionDate =
                transaction.getTransactionDate();

        if (transaction.getCategory() != null) {

            this.category =
                    new CategoryResponse(
                            transaction.getCategory()
                    );
        }
    }

    public Long getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public CategoryResponse getCategory() {
        return category;
    }


    // ==============================
    // CATEGORY RESPONSE
    // ==============================

    public static class CategoryResponse {

        private Long id;
        private String name;
        private String type;

        public CategoryResponse(
                com.example.demo.model.Category category) {

            this.id = category.getId();
            this.name = category.getName();
            this.type = category.getType();
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }
}
