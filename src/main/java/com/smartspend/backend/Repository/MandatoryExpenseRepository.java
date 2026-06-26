package com.smartspend.backend.Repository;

import com.smartspend.backend.Entity.MandatoryExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MandatoryExpenseRepository
        extends JpaRepository<MandatoryExpense, Long> {

    List<MandatoryExpense> findByUid(String uid);
    void deleteByUid(String uid);
}
