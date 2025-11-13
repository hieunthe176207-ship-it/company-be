package com.swp.company.controller;

import com.swp.company.dto.request.IdeaRequest;
import com.swp.company.dto.request.ReplyIdeaRequest;
import com.swp.company.dto.response.ResponseSuccess;
import com.swp.company.exception.ApiException;
import com.swp.company.service.IdeasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ideas")
public class IdeaController {
    private  final IdeasService ideasService;
    @PostMapping("/add")
    public ResponseEntity<?> addIdeas(@RequestBody IdeaRequest data) throws ApiException {
        ideasService.addIdeas(data);
        return ResponseEntity.ok(ResponseSuccess.builder()
                        .message("thành công")
                        .code(200)
                .build());
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllIdeas(@RequestParam int page , @RequestParam int size) throws ApiException {
        return ResponseEntity.ok(ResponseSuccess.builder()
                        .data(ideasService.getAllIdeas(page, size))
                        .code(200)
                        .message("thành công")
                .build());

    }
    @GetMapping("/me")
    public ResponseEntity<?> getMyIdeas(@RequestParam int page , @RequestParam int size) throws ApiException {
        return ResponseEntity.ok(ResponseSuccess.builder()
                .data(ideasService.getAllIdeas(page, size))
                .code(200)
                .message("thành công")
                .build());

    }

    @PutMapping("/reply")
    public ResponseEntity<?> replyIdeas(@RequestBody ReplyIdeaRequest data) throws ApiException {
        ideasService.updateIdea(data);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .code(200)
                .message("thành công")
                .build());
    }


}
