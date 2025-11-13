package com.swp.company.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SystemHistoryResponse {
    private int id;
    private String content;
    private UserResponseDTO actor;
    private String createdAt;
}
