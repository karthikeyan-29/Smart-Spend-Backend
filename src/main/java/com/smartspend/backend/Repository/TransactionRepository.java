package com.smartspend.backend.Repository;

import com.smartspend.backend.DTO.ExpenseCategoryInsightDTO;
import com.smartspend.backend.Entity.Transaction;
import com.smartspend.backend.Entity.TransactionType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserUid(String userUid);

    List<Transaction> findByUserUidAndType(String userUid, TransactionType type);

    List<Transaction> findByUserUidAndCategoriesContaining(String userUid, String category);

    // Recent transactions (using Pageable for dynamic limit)
    List<Transaction> findByUserUidOrderByCreatedAtDesc(String userUid, Pageable pageable);
    Transaction findByTxnId(String txnId);
    boolean existsByTxnId(String txnId);
    List<Transaction> findByUserUidAndCreatedAtBetweenOrderByCreatedAtDesc(
            String userUid,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );


    @Query("""
    SELECT new com.smartspend.backend.DTO.ExpenseCategoryInsightDTO(
        t.mainCategory,
        SUM(t.amount)
    )
    FROM Transaction t
    WHERE t.userUid = :uid
      AND t.type = :type
      AND t.createdAt BETWEEN :startDate AND :endDate
    GROUP BY t.mainCategory
    ORDER BY SUM(t.amount) DESC
""")
    List<ExpenseCategoryInsightDTO> findCurrentMonthExpenseByCategory(
            @Param("uid") String uid,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Transactional
    void deleteByUserUid(String userUid);




}
