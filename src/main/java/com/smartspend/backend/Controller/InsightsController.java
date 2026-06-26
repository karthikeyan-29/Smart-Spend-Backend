package com.smartspend.backend.Controller;

import com.smartspend.backend.DTO.ExpenseCategoryInsightDTO;
import com.smartspend.backend.Service.InsightsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
@RestController
@RequestMapping("api/insights")
@RequiredArgsConstructor
public class InsightsController {
    private final InsightsService insightsService;
    @GetMapping("/expense/current-month/category")
    public ResponseEntity<List<ExpenseCategoryInsightDTO>> getCurrentMonthExpenseInsights(
            @RequestHeader("Authorization") String token
    ) throws Exception {

       return ResponseEntity.ok(
               insightsService.getCurrentMonthExpenseInsights(token)
       );
    }
}
