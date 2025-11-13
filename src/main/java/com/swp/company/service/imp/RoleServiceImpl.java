package com.swp.company.service.imp;

import com.swp.company.dto.request.RoleRequestDto;
import com.swp.company.dto.response.PermissionResponseDto;
import com.swp.company.dto.response.RoleResponseDto;
import com.swp.company.entity.Permission;
import com.swp.company.entity.Role;
import com.swp.company.exception.ApiException;
import com.swp.company.repository.PermissionRepository;
import com.swp.company.repository.RoleRepository;
import com.swp.company.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    @Override
    public List<RoleResponseDto> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(item -> {
            return RoleResponseDto.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .description(item.getDescription())
                    .build();
        }).toList();
    }

    @Override
    public void addRole(RoleRequestDto roleRequestDto) {
        roleRepository.save(Role.builder()
                        .name(roleRequestDto.getName())
                        .description(roleRequestDto.getDescription())
                .build());
    }

    @Override
    public void updateRole(RoleRequestDto roleRequestDto) throws ApiException {
        Role role = getRoleById(roleRequestDto.getId());
        role.setName(roleRequestDto.getName());
        role.setDescription(roleRequestDto.getDescription());
        roleRepository.save(role);
    }

    @Override
    public List<RoleResponseDto> getRolesAndPermission() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(item -> {
            return RoleResponseDto.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .description(item.getDescription())
                    .permissions(item.getPermissions().stream().map(Permission::getPermission).toList())
                    .build();
        }).toList();
    }

    @Override
    public void updatePermission(List<RoleRequestDto> requestList) throws ApiException {
        for (RoleRequestDto request : requestList) {
            Role role = getRoleById(request.getId());
            // Lấy quyền mới từ key string
            List<Permission> newPermissions = permissionRepository.findByPermissionIn(request.getPermissions());
            List<Integer> newPermissionIds = newPermissions.stream()
                    .map(Permission::getId)
                    .sorted() // sắp xếp để so sánh chính xác
                    .toList();

            // Lấy quyền hiện tại
            List<Integer> currentPermissionIds = role.getPermissions().stream()
                    .map(Permission::getId)
                    .sorted()
                    .toList();
            // So sánh 2 list (không xét thứ tự vì đã sort)
            if (!newPermissionIds.equals(currentPermissionIds)) {
                role.setPermissions(newPermissions);
                roleRepository.save(role);
            }
        }

    }

    private Role getRoleById(int id) throws ApiException{
        return roleRepository.findById(id).orElseThrow(() -> new ApiException(404, "Không tìm thấy vai trò"));
    }
}
