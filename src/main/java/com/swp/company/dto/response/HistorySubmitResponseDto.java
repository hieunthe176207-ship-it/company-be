package com.swp.company.dto.response;

import com.swp.company.util.common.FormStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HistorySubmitResponseDto {
    private int id;
    private String name;
    private FormStatus status;
    private String response;
    private String createAt;
    private UserResponseDTO employee;
}
