package com.swp.company.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FormResponseDto {
    private String fullName;
    private int id;
    private String name;
    private String description;
    List<FormDetailResponseDto> details;
    private String createAt;
}
