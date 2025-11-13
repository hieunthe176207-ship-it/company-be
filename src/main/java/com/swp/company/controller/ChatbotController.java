package com.swp.company.controller;

import com.swp.company.dto.request.ChatBotRequest;
import com.swp.company.dto.request.InterviewRequestDto;
import com.swp.company.dto.response.ResponseSuccess;
import com.swp.company.exception.ApiException;
import com.swp.company.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat-bot")
public class ChatbotController {
    private final ChatbotService chatbotService;
    @PostMapping("/add")
    public ResponseEntity<?> addQuestion(@RequestBody ChatBotRequest data) throws ApiException {
        chatbotService.addQuestion(data);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .message("thành công")
                .code(200)
                .build());
    }
    @GetMapping("/get")
    public ResponseEntity<?> getQuestions() throws ApiException {
        return ResponseEntity.ok(ResponseSuccess.builder()
                .message("thành công")
                .data(chatbotService.getQuestions())
                .code(200)
                .build());
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateQuestions(@PathVariable int id, @RequestBody ChatBotRequest data) throws ApiException {
        chatbotService.updateQuestion(data, id);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .message("thành công")
                .code(200)
                .build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteQuestions(@PathVariable int id) throws ApiException {
        chatbotService.deleteQuestion(id);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .message("thành công")
                .code(200)
                .build());
    }
    @GetMapping("/get-chat")
    public ResponseEntity<?> getQuestionsForChat() throws ApiException {
        return ResponseEntity.ok(ResponseSuccess.builder()
                .message("thành công")
                .data(chatbotService.getQuestionsByRole())
                .code(200)
                .build());
    }

}
