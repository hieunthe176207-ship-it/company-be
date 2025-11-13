package com.swp.company.service;

import com.swp.company.dto.request.ActiveAccount;
import com.swp.company.dto.request.LoginDTO;
import com.swp.company.dto.request.ResetPasswordDto;
import com.swp.company.dto.response.LoginResponseDTO;
import com.swp.company.dto.response.UserResponseDTO;
import com.swp.company.exception.ApiException;

public interface AuthService {
    public LoginResponseDTO login(LoginDTO data) throws ApiException;
    public UserResponseDTO getAccount(String token) throws ApiException;
    public void sendMailForgotPassword(String email) throws ApiException;
    public String checkForgotToken(String token) throws ApiException;
    public void resetPassword(ResetPasswordDto data) throws ApiException;
    public void activeAccount(ActiveAccount data, String token) throws ApiException;

}
