package com.swp.company.controller;

import com.swp.company.dto.request.ActiveAccount;
import com.swp.company.dto.request.LoginDTO;
import com.swp.company.dto.request.ResetPasswordDto;
import com.swp.company.dto.request.UploadCVAndInfo;
import com.swp.company.dto.response.LoginResponseDTO;
import com.swp.company.dto.response.ResponseSuccess;
import com.swp.company.dto.response.UserResponseDTO;
import com.swp.company.exception.ApiException;
import com.swp.company.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController {

    private final AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO data) throws ApiException {
        LoginResponseDTO resposne = authService.login(data);
        return ResponseEntity.ok(ResponseSuccess.builder()
                        .code(HttpStatus.OK.value())
                        .message("Đăng nhập thành công")
                        .data(resposne)
                .build());
    }

    @GetMapping("/me")
    public  ResponseEntity<?> getAccount (@RequestHeader("Authorization") String token) throws ApiException {
        UserResponseDTO user = authService.getAccount(token);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .code(HttpStatus.OK.value())
                .message("Đăng nhập thành công")
                .data(user)
                .build());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) throws ApiException {
        authService.sendMailForgotPassword(email);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .code(HttpStatus.OK.value())
                .message("Gửi mail thành công")
                .data(null)
                .build());
    }

    @GetMapping("/check-forgot-token")
    public ResponseEntity<?> checkForgotToken(@RequestParam String token) throws ApiException {
        String email = authService.checkForgotToken(token);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .code(HttpStatus.OK.value())
                .message("Đường dẫn hợp lệ")
                .data(email)
                .build());
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto data) throws ApiException {
        authService.resetPassword(data);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .code(HttpStatus.OK.value())
                .message("Đổi mật khẩu thành công")
                .data(null)
                .build());
    }
    @PutMapping("/active-account")
    public ResponseEntity<?> activeAccount(@RequestBody ActiveAccount data, @RequestHeader("Authorization") String token) throws ApiException {
        authService.activeAccount(data, token);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .code(HttpStatus.OK.value())
                .message("Đổi mật khẩu thành công")
                .data(null)
                .build());
    }
}
