package com.smartspend.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class ExpenseCategoryInsightDTO {
   private String mainCategory;
   private Double totalAmount;

}
