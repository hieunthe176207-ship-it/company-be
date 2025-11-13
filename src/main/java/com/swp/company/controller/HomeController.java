package com.swp.company.controller;

import com.swp.company.dto.response.FormResponseDto;
import com.swp.company.dto.response.ResponseSuccess;
import com.swp.company.exception.ApiException;
import com.swp.company.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {
    private final HomeService homeService;
    @GetMapping("/dash-board")
    public ResponseEntity<?> findAllForm() throws ApiException {
        return ResponseEntity.ok().body(
                ResponseSuccess.builder()
                        .code(200)
                        .message("Thêm biểu mẫu thành công")
                        .data(homeService.getDashBoard())
                        .build()
        );
    }
}
