package com.swp.company.dto.response;

import com.swp.company.util.common.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CandidateResponse {
    private int id;
    private String email;
    private String fullName;
    private UserStatus status;
    private String cv;
    private String avatar;
    ProfileResponseDto profile;
    InterviewResponseDto interview;
}
