package com.smartspend.backend.DTO;


import com.smartspend.backend.Entity.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MandatoryExpenseDTO {
  private Long id;
  private String name;
  private Long amount;
  private Priority priority;
  private LocalDate dueDate;
}
