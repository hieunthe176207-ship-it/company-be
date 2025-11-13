package com.swp.company.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.swp.company.dto.request.*;
import com.swp.company.dto.response.ListAndPageUserResponseDto;
import com.swp.company.dto.response.ListCandidateResponse;
import com.swp.company.dto.response.ProfileHistoryResponseDto;
import com.swp.company.dto.response.UserResponseDTO;
import com.swp.company.exception.ApiException;
import com.swp.company.util.common.UserStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    public void addUser(AddUserRequest data) throws ApiException;
    public ListCandidateResponse listCandidate(int page, int size) throws ApiException;
    void updateProfileByAdmin(ProfileRequest data, int userId, MultipartFile frontImage, MultipartFile backImage) throws ApiException, IOException;
    void updateMyProfile(ProfileRequest data, MultipartFile frontImage, MultipartFile backImage) throws IOException, ApiException;
    List<ProfileHistoryResponseDto> getHistoryUpdate(int userId) throws ApiException;
    void uploadCVandInfo(String token, MultipartFile cv, UploadCVAndInfo data) throws ApiException, IOException;
    void uploadAvatar(String token, MultipartFile avatar) throws ApiException, IOException;
    UserResponseDTO getUserDetail(int id) throws ApiException;
    public void banUser(BanUserRequest data) throws ApiException;
    public void deleteUser(int id) throws ApiException;
    public ListAndPageUserResponseDto getAllUsers(int page, int size, int departmentId, UserStatus status, int roleId, String name, String sortDirection, int note) throws ApiException;
}
