package com.swp.company.controller;

import com.swp.company.dto.request.HandleFormRequest;
import com.swp.company.dto.request.SubmissionDto;
import com.swp.company.dto.response.FormResponseDto;
import com.swp.company.dto.response.ResponseSuccess;
import com.swp.company.entity.Form;
import com.swp.company.exception.ApiException;
import com.swp.company.service.FormService;
import com.swp.company.util.common.FormStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/form")
public class FormController {
    private final FormService formService;
    @PostMapping("/add")
    public ResponseEntity<?> addForm(@RequestBody Form data) throws ApiException {
        formService.addForm(data);
        return ResponseEntity.ok().body(
                ResponseSuccess.builder()
                        .code(200)
                        .message("Thêm biểu mẫu thành công")
                        .data(null)
                        .build()
        );
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> findFormById(@PathVariable int id) throws ApiException {
        FormResponseDto data = formService.getForm(id);
        return ResponseEntity.ok().body(
                ResponseSuccess.builder()
                        .code(200)
                        .message("Thêm biểu mẫu thành công")
                        .data(data)
                        .build()
        );
    }
    @GetMapping("/get-all")
    public ResponseEntity<?> findAllForm() throws ApiException {
        List<FormResponseDto> data = formService.getAllForms();
        return ResponseEntity.ok().body(
                ResponseSuccess.builder()
                        .code(200)
                        .message("Thêm biểu mẫu thành công")
                        .data(data)
                        .build()
        );
    }

    @PutMapping("/update-form/{id}")
    public ResponseEntity<?> updateForm(@RequestBody Form payload , @PathVariable int id) throws ApiException {
        formService.updateForm(id, payload);
        return ResponseEntity.ok().body(
                ResponseSuccess.builder()
                        .code(200)
                        .message("Thêm biểu mẫu thành công")
                        .data(null)
                        .build()
        );
    }

    @DeleteMapping("/delete-form/{id}")
    public ResponseEntity<?> updateForm(@PathVariable int id) throws ApiException {
        formService.deleteForm(id);
        return ResponseEntity.ok().body(
                ResponseSuccess.builder()
                        .code(200)
                        .message("Thêm biểu mẫu thành công")
                        .data(null)
                        .build()
        );
    }

    @PostMapping("/submisson")
    public ResponseEntity<?> submitForm(@RequestBody SubmissionDto data, @RequestHeader("Authorization") String token) throws ApiException {
            formService.submissForm(data, token);
            return ResponseEntity.ok()
                    .body(ResponseSuccess.builder()
                            .code(200)
                            .message("Gửi đơn thành công")
                            .data(null)
                            .build());
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@RequestHeader("Authorization") String token, @RequestParam int size, @RequestParam int page) throws ApiException {
        Map data =formService.getHistorySubmit(token, size, page);
        return ResponseEntity.ok()
                .body(ResponseSuccess.builder()
                        .code(200)
                        .message("Gửi đơn thành công")
                        .data(data)
                        .build());
    }
    @GetMapping("/all-form-submit")
    public ResponseEntity<?> getAllForm(@RequestParam(required = false, defaultValue = "-1") int id,
                                        @RequestParam(required = false) FormStatus status,
                                        @RequestParam(required = false) String date,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(required = false, defaultValue = "desc") String sort,
                                        @RequestParam(defaultValue = "10") int size) throws ApiException {
        Map data =formService.getAllSubmit(size, page, id, date , status, sort);
        return ResponseEntity.ok()
                .body(ResponseSuccess.builder()
                        .code(200)
                        .message("Gửi đơn thành công")
                        .data(data)
                        .build());
    }

    @PutMapping("/handle-form")
    public ResponseEntity<?> handleForm(@RequestBody HandleFormRequest request ) throws ApiException {
        formService.handleFormAction(request);
        return ResponseEntity.ok()
                .body(ResponseSuccess.builder()
                        .code(200)
                        .message("Gửi đơn thành công")
                        .data(null)
                        .build());
    }
    @GetMapping("/anwser-form/{id}")
    public ResponseEntity<?> getAnswerForm(@PathVariable int id ) throws ApiException {
        Map<String, Object> data = formService.getSubmitAnwer(id);
        return ResponseEntity.ok()
                .body(ResponseSuccess.builder()
                        .code(200)
                        .message("Gửi đơn thành công")
                        .data(data)
                        .build());
    }
}
