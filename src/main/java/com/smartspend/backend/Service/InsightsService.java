package com.smartspend.backend.Service;

import com.smartspend.backend.DTO.ExpenseCategoryInsightDTO;
import com.smartspend.backend.Entity.TransactionType;
import com.smartspend.backend.Entity.User;
import com.smartspend.backend.Repository.TransactionRepository;
import com.smartspend.backend.Util.FirebaseTokenUtil;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class InsightsService {
  private final TransactionRepository transactionRepository;

  public List<ExpenseCategoryInsightDTO> getCurrentMonthExpenseInsights(String token) throws Exception {
      String uid = FirebaseTokenUtil.getUidFromToken(token);
      LocalDate today=LocalDate.now();
      LocalDateTime startDate= today
              .withDayOfMonth(1)
              .atStartOfDay();
      LocalDateTime endDate=today
              .withDayOfMonth(today.lengthOfMonth())
              .atTime(LocalTime.MAX);
      return transactionRepository.findCurrentMonthExpenseByCategory(
              uid,
              TransactionType.EXPENSE,
              startDate,
              endDate
      );
  }


}
