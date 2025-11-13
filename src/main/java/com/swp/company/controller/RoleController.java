package com.swp.company.controller;

import com.swp.company.dto.request.RoleRequestDto;
import com.swp.company.dto.response.DepartmentResponseDto;
import com.swp.company.dto.response.ResponseSuccess;
import com.swp.company.dto.response.RoleResponseDto;
import com.swp.company.entity.Role;
import com.swp.company.exception.ApiException;
import com.swp.company.repository.RoleRepository;
import com.swp.company.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;
    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(ResponseSuccess.builder()
                .code(200)
                .message("Thành công")
                .data(roleService.getAllRoles())
                .build());
    }
    @PostMapping("/add")
    public ResponseEntity<?> addRole(@RequestBody RoleRequestDto roleRequestDto) {
        roleService.addRole(roleRequestDto);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .code(200)
                .message("Thành công")
                .build());
    }
    @PutMapping("/update")
    public ResponseEntity<?> updateRole(@RequestBody RoleRequestDto roleRequestDto) throws ApiException {
        roleService.updateRole(roleRequestDto);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .code(200)
                .message("Thành công")
                .build());
    }

    @GetMapping("/get-role-permissions")
    public ResponseEntity<?> getRoleAndPermissions() {
        return ResponseEntity.ok(ResponseSuccess.builder()
                .code(200)
                .message("Thành công")
                .data(roleService.getRolesAndPermission())
                .build());
    }

    @PutMapping("/update-permissions")
    public ResponseEntity<?> updatePermissions(@RequestBody List<RoleRequestDto> roleRequestDto) throws ApiException {
        roleService.updatePermission(roleRequestDto);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .code(200)
                .message("Thành công")
                .build());
    }
}
