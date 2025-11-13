package com.swp.company.controller;

import com.swp.company.dto.response.ResponseSuccess;
import com.swp.company.service.SystemHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/system")
public class SystemHistoryController {
    private final SystemHistoryService systemHistoryService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(@RequestParam int page,
                                    @RequestParam int size,
                                    @RequestParam(required = false) LocalDateTime startDate,
                                    @RequestParam(required = false) LocalDateTime endDate,
                                    @RequestParam(defaultValue = "desc") String sort) {
        return ResponseEntity.ok(ResponseSuccess.builder()
                        .data(systemHistoryService.getSystemHistory(page, size, startDate, endDate, sort))
                        .message("Thành công")
                        .code(200)
                .build());
    }
}
