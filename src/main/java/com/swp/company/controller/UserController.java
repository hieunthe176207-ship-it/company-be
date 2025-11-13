package com.swp.company.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp.company.dto.request.*;
import com.swp.company.dto.response.*;
import com.swp.company.exception.ApiException;
import com.swp.company.service.UserService;
import com.swp.company.util.common.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    public final UserService userService;

    @PreAuthorize("hasAuthority('user_add')")
    @PostMapping("/add")
    public ResponseEntity<?> addUser(@RequestBody AddUserRequest data) throws ApiException {
        userService.addUser(data);
        return ResponseEntity.ok(ResponseSuccess.builder()
                        .code(200)
                        .data(null)
                        .message("Thêm người dùng thành công")
                .build());
    }

    @PreAuthorize("hasAuthority('user_update')")
    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(
                                           @RequestParam(name = "userId", required = false, defaultValue = "0") int userId,
                                           @RequestParam(name = "frontImage", required = false) MultipartFile frontImage,
                                           @RequestParam(name = "backImage", required = false) MultipartFile backImage,
                                           @RequestParam(name = "data") String data
    ) throws ApiException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        ProfileRequest profileRequest = mapper.readValue(data, ProfileRequest.class);
        userService.updateProfileByAdmin(profileRequest, userId, frontImage, backImage);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .code(200)
                .data(null)
                .message("Cập nhật thông tin thành công")
                .build());
    }

    @PutMapping("/update-my-profile")
    public ResponseEntity<?> updateMyProfile(
                                           @RequestParam(name = "userId", required = false, defaultValue = "0") int userId,
                                           @RequestParam(name = "frontImage", required = false) MultipartFile frontImage,
                                           @RequestParam(name = "backImage", required = false) MultipartFile backImage,
                                           @RequestParam(name = "data") String data
    ) throws ApiException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        ProfileRequest profileRequest = mapper.readValue(data, ProfileRequest.class);
        userService.updateMyProfile( profileRequest,frontImage, backImage);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .code(200)
                .data(null)
                .message("Cập nhật thông tin thành công")
                .build());
    }

    @PreAuthorize("hasAuthority('user_view')")
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "0") int department,
            @RequestParam(required = false, defaultValue = "0") int role,
            @RequestParam(required = false, defaultValue = "desc") String sort,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false, defaultValue = "-1") int note
    ) throws ApiException {
        ListAndPageUserResponseDto data = userService.getAllUsers(page, size, department, status, role, name, sort, note);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .code(200)
                .data(data)
                .message("Tìm kiếm thành công")
                .build());
    }

    @PutMapping("/upload-cv")
    public ResponseEntity<?> uploadCv(@RequestParam("file") MultipartFile file, @RequestParam("data") String dataJson, @RequestHeader("Authorization") String token) throws ApiException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        UploadCVAndInfo data = objectMapper.readValue(dataJson, UploadCVAndInfo.class);
        userService.uploadCVandInfo(token, file, data);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .code(200)
                .data(null)
                .message("Thêm cv thành công")
                .build());
    }

    @GetMapping("/get-candidates")
    public ResponseEntity<?> getCandidates(@RequestParam int page, @RequestParam int size) throws ApiException {
        ListCandidateResponse data = userService.listCandidate(page,size);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .code(200)
                .data(data)
                .message("Thành công")
                .build());
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(@RequestHeader("Authorization") String token, @RequestParam("file") MultipartFile avatar) throws ApiException, IOException {
        userService.uploadAvatar(token, avatar);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .code(200)
                .data(null)
                .message("Thành công")
                .build());
    }

    @GetMapping("/get-detail/{id}")
    public ResponseEntity<?> getDetail(@PathVariable int id) throws ApiException {
        UserResponseDTO user = userService.getUserDetail(id);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .code(200)
                .data(user)
                .message("Thành công")
                .build());
    }
    @GetMapping("/get-history-update")
    public ResponseEntity<?> getHistoryUpdate(@RequestParam int userId) throws ApiException {
        List<ProfileHistoryResponseDto> data = userService.getHistoryUpdate(userId);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .code(200)
                .data(data)
                .message("Thành công")
                .build());
    }
    @PreAuthorize("hasAuthority('user_block')")
    @PutMapping("/ban")
    public ResponseEntity<?> banUser(@RequestBody BanUserRequest data) throws ApiException {
        userService.banUser(data);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .code(200)
                .data(null)
                .message("Thành công")
                .build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCandidate(@PathVariable int id) throws ApiException {
        userService.deleteUser(id);
        return ResponseEntity.ok(ResponseSuccess.builder()
                .code(200)
                .message("Thành công")
                .build());
    }



}
