package com.swp.company.service;

import com.swp.company.dto.request.ChatBotRequest;
import com.swp.company.dto.response.ChatBotResponse;
import com.swp.company.exception.ApiException;

import java.util.List;

public interface ChatbotService {
    void addQuestion(ChatBotRequest data);
    void updateQuestion(ChatBotRequest data, int id) throws ApiException;
    void deleteQuestion(int id) throws ApiException;
    List<ChatBotResponse> getQuestions();
    List<ChatBotResponse> getQuestionsByRole() throws ApiException;
}
