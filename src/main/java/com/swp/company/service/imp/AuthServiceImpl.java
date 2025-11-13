package com.swp.company.service.imp;

import com.swp.company.dto.request.ActiveAccount;
import com.swp.company.dto.request.LoginDTO;
import com.swp.company.dto.request.ResetPasswordDto;
import com.swp.company.dto.response.*;
import com.swp.company.entity.Document;
import com.swp.company.entity.Permission;
import com.swp.company.entity.Profile;
import com.swp.company.entity.User;
import com.swp.company.exception.ApiException;
import com.swp.company.repository.DocumentReposiroty;
import com.swp.company.repository.UserRepository;
import com.swp.company.service.AuthService;
import com.swp.company.service.EmailService;
import com.swp.company.util.SecurityUtil;
import com.swp.company.util.common.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserRepository userRepository;
    private final SecurityUtil jwtService;
    private final EmailService emailService;
    private final DocumentReposiroty documentReposiroty;

    @Override
    public LoginResponseDTO login(LoginDTO data) throws ApiException {
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(data.getEmail(), data.getPassword());
        authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        User user = userRepository.findByEmail(data.getEmail());

        if (passwordEncoder.matches(data.getPassword(), user.getPassword())) {
            if(user.getIsDeleted() == 1) {
                throw new ApiException(400, "Tài khoản hoặc mật khẩu bị sai");
            }
            if (user.getStatus().equals(UserStatus.NOT_ACTIVE)) {
                throw new ApiException(400, "Vui lòng kích hoạt tài khoản của bạn");
            }
            if(user.isBan()){
                throw new ApiException(400, "Tài khoản đang bị chặn");
            }
            else {
                Profile profile = user.getProfile();
                ProfileResponseDto profileResponseDto = null;
                if(profile != null){
                    profileResponseDto = new ProfileResponseDto();
                    profileResponseDto.setId(profile.getId());
                    profileResponseDto.setBod(profile.getBirthDate().toString());
                    profileResponseDto.setPhone(profile.getPhone());
                    profileResponseDto.setFullName(profile.getFullName());
                    profileResponseDto.setAddress(profile.getAddress());
                }
                String accessToken = jwtService.createToken(user);
                String refreshToken = jwtService.createRefreshToken(user);
                user.setRefreshToken(refreshToken);

                List<Document> documents = user.getDocuments();
                userRepository.save(user);

                return LoginResponseDTO.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .user(
                                UserResponseDTO
                                        .builder()
                                        .id(user.getId())
                                        .email(user.getEmail())
                                        .avatar(user.getAvatar())
                                        .name(user.getName())
                                        .status(user.getStatus())
                                        .role(RoleResponseDto.builder()
                                                .id(user.getRole().getId())
                                                .name(user.getRole().getName())
                                                .build())
                                        .department(user.getDepartment() != null ? DepartmentResponseDto.builder()
                                                .id(user.getDepartment().getId())
                                                .name(user.getDepartment().getName())
                                                .build() : null)
                                        .profile(profileResponseDto)
                                        .documents(documents.stream().map(item -> {
                                            return DocumentResponseDto.builder()
                                                    .path(item.getUrl())
                                                    .type(item.getType())
                                                    .build();
                                        }).toList())
                                        .permissions(user.getRole().getPermissions().stream().map(Permission::getPermission).toList())
                                        .build()
                        )
                        .build();
            }
        } else {
            throw new ApiException(HttpStatus.UNAUTHORIZED.value(), "Tài khoản hoặc mật khẩu bị sai");
        }

    }

    @Override
    public UserResponseDTO getAccount(String token) throws ApiException {
        String email = jwtService.getEmailFromToken(token);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ApiException(404, "Không tìm thấy người dùng");
        }
        if(user.getIsDeleted() == 1){
            throw new ApiException(401, "Không tìm thấy người dùng");
        }
        if(user.isBan()){
            throw new ApiException(404, "Tài khoản đã bị chặn");
        }
        else{
            Profile profile = user.getProfile();
            ProfileResponseDto profileResponseDto = null;
            if(profile != null){
                profileResponseDto = new ProfileResponseDto();
                profileResponseDto.setId(profile.getId());
                profileResponseDto.setBod(profile.getBirthDate().toString());
                profileResponseDto.setPhone(profile.getPhone());
                profileResponseDto.setFullName(profile.getFullName());
                profileResponseDto.setAddress(profile.getAddress());
            }
            List<Document> documents = user.getDocuments();
            return UserResponseDTO
                    .builder()
                    .id(user.getId())
                    .avatar(user.getAvatar())
                    .email(user.getEmail())
                    .name(user.getName())
                    .role(RoleResponseDto.builder()
                            .id(user.getRole().getId())
                            .name(user.getRole().getName())
                            .build())
                    .status(user.getStatus())
                    .department(user.getDepartment() != null ? DepartmentResponseDto.builder()
                            .id(user.getDepartment().getId())
                            .name(user.getDepartment().getName())
                            .build() : null)
                    .profile(profileResponseDto)
                    .documents(documents.stream().map(item -> {
                        return DocumentResponseDto.builder()
                                .path(item.getUrl())
                                .type(item.getType())
                                .build();
                    }).toList())
                    .permissions(user.getRole().getPermissions().stream().map(Permission::getPermission).toList())
                    .createAt(user.getCreatedAt().toString())
                    .updateAt(user.getUpdatedAt().toString())
                    .build();
        }

    }

    @Override
    public void sendMailForgotPassword(String email) throws ApiException {
        if(email == null || email.isEmpty()){
            throw new ApiException(400, "Email không hợp lệ");
        }
        User user = userRepository.findByEmail(email);
        if(user == null || user.getIsDeleted() == 1){
            throw new ApiException(404, "Không tìm thấy người dùng");
        }
        else{
            String forgotPasswordToken = jwtService.createActiveAndForgotToken(user.getEmail());
            user.setForgotToken(forgotPasswordToken);
            userRepository.save(user);
            String resetLink = "http://localhost:5173" + "/check-forgot?token=" + forgotPasswordToken;
            String emailContent = "<h1>Lấy lại mật khẩu</h1>" +
                    "<p>Vui lòng click vào link dưới đây để đặt lại mật khẩu:</p>" +
                    "<p><a href=\"" + resetLink + "\">Đặt lại mật khẩu</a></p>" +
                    "<p>Link này có hiệu lực trong 5 phút.</p>" +
                    "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>";
            emailService.sendEmail(
                    email,
                    "Lấy lại mật khẩu",
                    emailContent
            );
        }
    }

    @Override
    public String checkForgotToken(String token) throws ApiException {
        String email= jwtService.getEmailFromToken(token);
        User user = userRepository.findByEmail(email);
        boolean check = jwtService.validateToken(token);
        if(!check){
            throw new ApiException(400,"Đường dẫn không hợp lệ hoặc đã hết hạn");
        }
        else{
            if(!user.getForgotToken().equals(token)){
                System.out.println("lỗi ");
                throw new ApiException(400,"Đường dẫn không hợp lệ hoặc đã hết hạn");
            }
            return jwtService.getEmailFromToken(token);
        }
    }

    @Override
    public void resetPassword(ResetPasswordDto data) throws ApiException {
        String regex = "^(?=.*[a-z])(?=.*\\d).{8,}$";
        if(data.getEmail() == null || data.getEmail().isEmpty()){
            throw new ApiException(400,"Email không hợp lệ");
        }
        User user = userRepository.findByEmail(data.getEmail());
        if(user == null){
            throw new ApiException(400,"Người dùng không tồn tại");
        }
        else{
            if(jwtService.validateToken(data.getToken()) && user.getForgotToken().equals(data.getToken())){
                if(data.getPassword().matches(regex)){
                    user.setPassword(passwordEncoder.encode(data.getPassword()));
                    user.setForgotToken(null);
                    userRepository.save(user);
                }
                else{
                    throw new ApiException(400,"Mật khẩu không hợp lệ");
                }
            }
            else{
                throw new ApiException(400,"Token không hợp lệ");
            }
        }

    }

    @Override
    public void activeAccount(ActiveAccount data, String token) throws ApiException {
        String email = jwtService.getEmailFromToken(token);
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new ApiException(400,"Người dùng không tồn tại");
        }
        else{
            String regex = "^(?=.*[a-z])(?=.*\\d).{8,}$";
            if(data.getPassword().matches(regex)){
                user.setPassword(passwordEncoder.encode(data.getPassword()));
                user.setStatus(UserStatus.ACTIVE);
                userRepository.save(user);
            }
            else{
                throw new ApiException(400,"Mật khẩu không hợp lệ");
            }
        }
    }



}
