package com.swp.company.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {
    private int id;
    private String fullName;
    private String address;
    private String phone;
    private String dob;
} 