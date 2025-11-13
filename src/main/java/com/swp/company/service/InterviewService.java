package com.swp.company.service;

import com.swp.company.dto.request.InterviewRequestDto;
import com.swp.company.dto.response.InterviewResponseDto;
import com.swp.company.exception.ApiException;

public interface InterviewService {
    void addInterview(InterviewRequestDto interviewRequestDto) throws ApiException;
    InterviewResponseDto getInterviewById() throws ApiException;
    void updateInterview(InterviewRequestDto interviewRequestDto) throws ApiException;
    void deleteInterview(int id) throws ApiException;
    Object responseInterview(InterviewRequestDto interviewRequestDto ) throws ApiException;

}
