package com.swp.company.dto.response;

import com.swp.company.entity.Document;
import com.swp.company.util.common.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {
    private int id;
    private String email;
    private String name;
    private RoleResponseDto role;
    private UserStatus status;
    private ProfileResponseDto profile;
    private DepartmentResponseDto department;
    private List<DocumentResponseDto> documents;
    private String avatar;
    private String createAt;
    private String updateAt;
    private NoteResponseDto note;
    private List<String> permissions;
}
