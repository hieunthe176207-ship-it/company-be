package com.swp.company.dto.response;

import com.swp.company.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NoteResponseDto {
    private int id;
    private String title;
    private String content;
    private UserResponseDTO createdBy;
}
