package com.swp.company.dto.response;

import com.swp.company.util.common.FormType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FormDetailResponseDto {
    private int id;
    private String name;
    private FormType type;
    private int order;
}
