package com.swp.company.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChatBotResponse {
    private String question;
    private String answer;
    private List<RoleResponseDto> roles;
    private String createdAt;
    private int id;
}
