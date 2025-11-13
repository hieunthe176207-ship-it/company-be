package com.swp.company.controller;

import com.swp.company.dto.response.DepartmentResponseDto;
import com.swp.company.dto.response.ResponseSuccess;
import com.swp.company.entity.Department;
import com.swp.company.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/department")
@RequiredArgsConstructor
public class DepartmentController {
    public final DepartmentRepository departmentRepository;
    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(){
        List<Department> departments = departmentRepository.findAll();
        return ResponseEntity.ok(ResponseSuccess.builder()
                        .code(200)
                        .message("Thành công")
                        .data(departments.stream().map(item -> {
                            return DepartmentResponseDto.builder()
                                    .id(item.getId())
                                    .name(item.getName())
                                    .build();
                        }).toList())
                .build());
    }
}
