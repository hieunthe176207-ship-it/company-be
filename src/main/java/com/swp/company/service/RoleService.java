package com.swp.company.service;

import com.swp.company.dto.request.RoleRequestDto;
import com.swp.company.dto.response.RoleResponseDto;
import com.swp.company.entity.Role;
import com.swp.company.exception.ApiException;

import java.util.List;

public interface RoleService {
    public List<RoleResponseDto> getAllRoles();
    public void addRole(RoleRequestDto roleRequestDto);
    public void updateRole(RoleRequestDto roleRequestDto) throws ApiException;
    public List<RoleResponseDto> getRolesAndPermission();
    public void updatePermission(List<RoleRequestDto> roleRequestDto) throws ApiException;

}
