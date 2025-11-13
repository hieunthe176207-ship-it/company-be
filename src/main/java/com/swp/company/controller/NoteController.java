package com.swp.company.controller;

import com.swp.company.dto.request.InterviewRequestDto;
import com.swp.company.dto.request.NoteRequestDto;
import com.swp.company.dto.response.ResponseSuccess;
import com.swp.company.exception.ApiException;
import com.swp.company.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/note")
public class NoteController {
    private final NoteService noteService;

    @PostMapping("/add")
    public ResponseEntity<?> addNote(@RequestBody NoteRequestDto data) throws ApiException {
        noteService.addNote(data);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .message("thành công")
                .code(200)
                .build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateNote(@RequestBody NoteRequestDto data, @PathVariable int id) throws ApiException {
        noteService.updateNote(data,id);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .message("thành công")
                .code(200)
                .build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable int id) throws ApiException {
        noteService.deleteNote(id);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .message("thành công")
                .code(200)
                .build());
    }


}
