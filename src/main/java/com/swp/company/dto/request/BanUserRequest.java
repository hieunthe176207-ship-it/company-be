package com.swp.company.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BanUserRequest {
    private int userId;
    private String response;
    private int isBan;
}
