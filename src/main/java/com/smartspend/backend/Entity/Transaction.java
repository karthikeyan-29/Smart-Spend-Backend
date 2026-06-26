package com.smartspend.backend.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userUid; // renamed for clarity

    @ElementCollection
    @CollectionTable(name = "transaction_categories", joinColumns = @JoinColumn(name = "transaction_id"))
    @Column(name = "category")
    private List<String> categories;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private Boolean recurring; // nullable if needed
    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private Double amount;
    private String email;

    //for idempotency
    private String txnId;
    //for lock
    @Version
    private Long version;
    private String mainCategory;
}
