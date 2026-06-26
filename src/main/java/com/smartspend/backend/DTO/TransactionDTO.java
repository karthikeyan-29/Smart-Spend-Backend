package com.smartspend.backend.DTO;



import com.smartspend.backend.Entity.TransactionType;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {
    private Long id;
    private String userUid;
    private Double amount;
    private List<String> categories;
    private LocalDate date;
    private TransactionType type;
    private Boolean recurring;
    private String description;
    private String txnId;
    private String mainCategory;
}

