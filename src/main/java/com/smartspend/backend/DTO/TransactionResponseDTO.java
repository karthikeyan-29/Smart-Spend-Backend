package com.smartspend.backend.DTO;

import com.smartspend.backend.Entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDTO {
   private Long id;
   private Double amount;
   private TransactionType type;
   private List<String> categories;
   private LocalDate date;
   private boolean isRecurring;
   private String description;
   private String mainCategory;
}
