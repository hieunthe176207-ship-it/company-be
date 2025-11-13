package com.swp.company.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class IdeasResponse {
    private int id;
    private String content;
    private String status;
    private boolean isAnonymous;
    private String date;
    private String reply;
    private UserResponseDTO user;

}
