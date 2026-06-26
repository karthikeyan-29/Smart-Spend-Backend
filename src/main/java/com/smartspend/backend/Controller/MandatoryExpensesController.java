package com.smartspend.backend.Controller;

import com.smartspend.backend.DTO.MandatoryExpenseDTO;
import com.smartspend.backend.Entity.MandatoryExpense;
import com.smartspend.backend.Service.MandatoryExpensesService;
import com.smartspend.backend.Util.FirebaseTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mandatory")
@RequiredArgsConstructor
public class MandatoryExpensesController {
    private final MandatoryExpensesService mandatoryExpenseService;

    @RequestMapping
    public ResponseEntity<List<MandatoryExpenseDTO>> getMandatoryExpenses(
            @RequestHeader("Authorization") String authHeader
    ) throws Exception{
        String uid= FirebaseTokenUtil.getUidFromToken(authHeader);

        List<MandatoryExpenseDTO> mandatoryExpenseList=mandatoryExpenseService.getMandatoryExpenses(uid);
        return ResponseEntity.ok(mandatoryExpenseList);
    }
    @PostMapping
    public ResponseEntity<MandatoryExpenseDTO> addMandatoryExpense(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody MandatoryExpenseDTO dto
    )throws Exception{
        String uid=FirebaseTokenUtil.getUidFromToken(authHeader);
        MandatoryExpenseDTO created=mandatoryExpenseService.addMandatoryExpense(uid ,dto);
        return ResponseEntity.ok(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MandatoryExpenseDTO> deleteMandatoryExpense(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id
    ) throws Exception {

        // 1. Extract UID from token (authentication concern)
        String uid = FirebaseTokenUtil.getUidFromToken(authHeader);

        // 2. Delegate business logic to service
        MandatoryExpenseDTO deleted =
                mandatoryExpenseService.deleteMandatoryExpense(id, uid);

        // 3. Return 200 OK
        return ResponseEntity.ok(deleted);
    }



}
