package com.swp.company.dto.request;

import com.swp.company.util.common.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateUserRequest {
    private String name;
    private int role;
    private int department;
    private UserStatus status;
}
