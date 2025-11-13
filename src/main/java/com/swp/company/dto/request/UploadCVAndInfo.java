package com.swp.company.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class UploadCVAndInfo {
    private String fullName;
    private String phone;
    private String dob;
    private String address;
}
