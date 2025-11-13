package com.swp.company.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InterviewResponseDto {
    private int id;
    private String description;
    private String date;
    private String deadline;
    private int response;
    private String reason;
    UserResponseDTO user;
}
