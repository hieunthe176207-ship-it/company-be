package com.swp.company.controller;

import com.swp.company.dto.request.IdeaRequest;
import com.swp.company.dto.request.InterviewRequestDto;
import com.swp.company.dto.response.ResponseSuccess;
import com.swp.company.exception.ApiException;
import com.swp.company.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/interview")
public class InterviewController {
    private final InterviewService interviewService;

    @PostMapping("/add")
    public ResponseEntity<?> addInterview(@RequestBody InterviewRequestDto data) throws ApiException {
       interviewService.addInterview(data);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .message("thành công")
                .code(200)
                .build());
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateInterview(@RequestBody InterviewRequestDto data) throws ApiException {
        interviewService.updateInterview(data);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .message("thành công")
                .code(200)
                .build());
    }

    @GetMapping("/get")
    public ResponseEntity<?> getInterview() throws ApiException {
        return ResponseEntity.ok(ResponseSuccess.builder()
                .message("thành công")
                        .data(interviewService.getInterviewById())
                .code(200)
                .build());
    }

    @PatchMapping("/response")
    public ResponseEntity<?> responseInterview(@RequestBody InterviewRequestDto data) throws ApiException {
        return ResponseEntity.ok(ResponseSuccess.builder()
                .data(interviewService.responseInterview(data))
                .code(200)
                .message("thành công")
                .build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteInterview(@PathVariable int id) throws ApiException {
        interviewService.deleteInterview(id);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .message("thành công")
                .code(200)
                .build());
    }
}
