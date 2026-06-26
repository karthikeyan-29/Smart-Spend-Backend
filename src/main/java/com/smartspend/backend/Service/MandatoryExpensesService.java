package com.smartspend.backend.Service;


import com.smartspend.backend.DTO.MandatoryExpenseDTO;
import com.smartspend.backend.Entity.MandatoryExpense;
import com.smartspend.backend.Repository.MandatoryExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class MandatoryExpensesService {
     private final MandatoryExpenseRepository mandatoryExpenseRepository;

     public MandatoryExpenseDTO mapToDTO(MandatoryExpense mandatoryExpense){
         return MandatoryExpenseDTO.builder()
                 .id(mandatoryExpense.getId())
                 .name(mandatoryExpense.getName())
                 .amount(mandatoryExpense.getAmount())
                 .dueDate(mandatoryExpense.getDueDate())
                 .priority(mandatoryExpense.getPriority())
                 .build();
     }
    public MandatoryExpense mapToEntity(String uid , MandatoryExpenseDTO dto){
         return MandatoryExpense.builder()
                .uid(uid)
                .name(dto.getName())
                 .amount(dto.getAmount())
                .dueDate(dto.getDueDate())
                .priority(dto.getPriority())
                .build();
    }

    public List<MandatoryExpenseDTO> getMandatoryExpenses(String uid){
        return mandatoryExpenseRepository.findByUid(uid)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

     public MandatoryExpenseDTO addMandatoryExpense(String uid , MandatoryExpenseDTO dto){
         MandatoryExpense expense=mapToEntity(uid , dto);
         MandatoryExpense saved=mandatoryExpenseRepository.save(expense);
         return mapToDTO(saved);
     }
    public MandatoryExpenseDTO deleteMandatoryExpense(Long id, String uid) {

        // 1. Fetch expense
        MandatoryExpense expense = mandatoryExpenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mandatory expense not found"));

        // 2. Ownership validation (business rule)
        if (!expense.getUid().equals(uid)) {
            throw new RuntimeException("You are not authorized to delete this expense");
        }

        // 3. Delete
        mandatoryExpenseRepository.delete(expense);

        // 4. Return deleted data as DTO
        return mapToDTO(expense);
    }




}
