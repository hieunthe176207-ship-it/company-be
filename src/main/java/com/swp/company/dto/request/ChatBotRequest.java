package com.swp.company.dto.request;

import com.swp.company.dto.response.RoleResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChatBotRequest {
    private String question;
    private String answer;
    private List<Integer> roles;
}
