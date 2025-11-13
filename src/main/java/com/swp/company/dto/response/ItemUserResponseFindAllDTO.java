package com.swp.company.dto.response;

import com.swp.company.util.common.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ItemUserResponseFindAllDTO {
    private int id;
    private String name;
    private String email;
    private String role;
    private String department;
    private UserStatus status;
    private String avatar;
    private boolean isBan;
    private NoteResponseDto note;

}


// List<UserDto> / tạo userDto / tạo responseDto (List<UserDto> , Page)
//Page