package com.swp.company.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDetailResponseDto {
    private Integer id;
    private String content;
    private UserResponseDTO user;
    private List<PostFileDto> files;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 