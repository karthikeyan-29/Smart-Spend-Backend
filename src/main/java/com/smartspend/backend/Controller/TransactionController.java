package com.smartspend.backend.Controller;

import com.smartspend.backend.DTO.TransactionDTO;
import com.smartspend.backend.DTO.TransactionResponseDTO;
import com.smartspend.backend.Entity.TransactionType;
import com.smartspend.backend.Service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    // ------------------- CREATE -------------------
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody TransactionDTO dto) throws Exception {
        TransactionResponseDTO created = transactionService.createTransaction(dto, authHeader);

        return ResponseEntity.status(201).body(created);
    }

    // ------------------- UPDATE -------------------
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody TransactionDTO dto) throws Exception {
        TransactionResponseDTO updated = transactionService.updateTransaction(id, dto, authHeader);
        return ResponseEntity.ok(updated);
    }

    // ------------------- DELETE -------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------- GET ALL -------------------
    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions(
            @RequestHeader("Authorization") String authHeader) throws Exception {
        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByUser(authHeader);
        return ResponseEntity.ok(transactions);
    }


    // ------------------- SEARCH BY CATEGORY -------------------
    @GetMapping("/category/{category}")
    public ResponseEntity<List<TransactionResponseDTO>> searchByCategory(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String category) throws Exception {
        List<TransactionResponseDTO> transactions = transactionService.searchByCategory(authHeader, category);
        return ResponseEntity.ok(transactions);
    }

    // ------------------- FILTER BY TYPE -------------------
    @GetMapping("/type/{type}")
    public ResponseEntity<List<TransactionResponseDTO>> filterByType(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable TransactionType type) throws Exception {
        List<TransactionResponseDTO> transactions = transactionService.filterByType(authHeader, type);
        return ResponseEntity.ok(transactions);
    }

    // ------------------- RECENT TRANSACTIONS -------------------
    @GetMapping("/recent")
    public ResponseEntity<List<TransactionResponseDTO>> getRecentTransactions(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "5") int limit) throws Exception {
        List<TransactionResponseDTO> transactions = transactionService.getRecentTransactions(authHeader, limit);
        return ResponseEntity.ok(transactions);
    }
    // CURRENT MONTH TRANSACTIONS
    @GetMapping("/current-month")
    public ResponseEntity<List<TransactionResponseDTO>> getCurrentMonthTransaction(
            @RequestHeader("Authorization") String authHeader) throws Exception {
        List<TransactionResponseDTO> list=transactionService.getCurrentMonthTransaction(authHeader);
        return ResponseEntity.ok(list);
    }
    // ------------------- TRANSACTION SUMMARY -------------------
    @GetMapping("/summary")
    public ResponseEntity<?> getTransactionSummary(
            @RequestHeader("Authorization") String authHeader) throws Exception {

        List<TransactionResponseDTO> transactions =
                transactionService.getTransactionsByUser(authHeader);

        long total = transactions.size();
        long income = 0;
        long expense = 0;

        for (TransactionResponseDTO txn : transactions) {
            if (txn.getType() == TransactionType.INCOME) {
                income++;
            } else if (txn.getType() == TransactionType.EXPENSE) {
                expense++;
            }
        }

        return ResponseEntity.ok(
                java.util.Map.of(
                        "totalTransactions", total,
                        "incomeCount", income,
                        "expenseCount", expense
                )
        );
    }

}
