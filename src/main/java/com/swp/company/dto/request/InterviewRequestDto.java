package com.swp.company.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InterviewRequestDto {
    int id;
    private String description;
    private LocalDateTime date;
    private  LocalDateTime deadline;
    private String reason;
    private int response;
    private int senderId;
    private int receiverId;
}
