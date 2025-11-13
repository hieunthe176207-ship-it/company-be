package com.swp.company.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProfileHistoryResponseDto {
    private int id;
    private String oldContent;
    private String updateAt;
    private String description;
    private UserResponseDTO actor;
}
