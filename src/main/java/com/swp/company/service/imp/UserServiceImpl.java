package com.swp.company.service.imp;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp.company.dto.request.AddUserRequest;
import com.swp.company.dto.request.BanUserRequest;
import com.swp.company.dto.request.ProfileRequest;
import com.swp.company.dto.request.UploadCVAndInfo;
import com.swp.company.dto.response.*;
import com.swp.company.entity.*;
import com.swp.company.exception.ApiException;
import com.swp.company.repository.*;
import com.swp.company.service.EmailService;
import com.swp.company.service.NoteService;
import com.swp.company.service.SystemHistoryService;
import com.swp.company.service.UserService;
import com.swp.company.util.SecurityUtil;
import com.swp.company.util.Util;
import com.swp.company.util.common.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final SecurityUtil securityUtil;
    private final DocumentReposiroty documentReposiroty;
    private final DepartmentRepository departmentRepository;
    private final ProfileHistoryRepository profileHistoryRepository;
    private final SystemHistoryService systemHistoryService;
    private final InterviewRepository interviewRepository;

    @Override
    public void addUser(AddUserRequest data) throws ApiException {
        String regex = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[@#$%^&*]).{8,}$";
        if(data.getEmail() == null || data.getEmail().isEmpty()){
            throw new ApiException(400, "Vui lòng nhập email");
        }
        else if(data.getPassword() == null || data.getPassword().isEmpty()){
            throw new ApiException(400, "Vui lòng nhập mật khẩu");
        }

        else if(!data.getPassword().matches(regex) ){
            throw new ApiException(400, "Mật khẩu có ít nhất 8 ký tự gồm cả chữ và số");
        }
        User user = userRepository.findByEmail(data.getEmail());
        if(user == null){
            Role role = roleRepository.findById(5)
                    .orElseThrow(() -> new ApiException(400, "Role Candidate không tồn tại"));
            // Tạo người dùng mới
            User newUser = new User();
            newUser.setEmail(data.getEmail());
            newUser.setPassword(passwordEncoder.encode(data.getPassword()));
            newUser.setStatus(UserStatus.INACTIVE);
            newUser.setName(data.getName());
            newUser.setRole(role); // Gán role từ cơ sở dữ liệu
            userRepository.save(newUser);

            SystemHistory systemHistory = new SystemHistory();
            systemHistory.setActor(getUser());
            systemHistory.setContent("Thêm mới ứng viên "+ data.getName());
            systemHistoryService.addHistory(systemHistory);
            emailService.sendEmail(
                    data.getEmail(),
                    "Xin chúc mừng",
                    "<h1>Chào mừng bạn đến với công ty</h1>" +
                            "<p>Tài khoản của bạn đã được tạo thành công, dưới đây là thông tin đăng nhập: </p>" +
                            "<p>Email: "+data.getEmail()+"</p>" +
                            "<p>Password: "+data.getPassword()+"</p>" +
                            "Bạn hãy đăng nhập tại đây để kích hoạt tài khoản : <a href='" + "http://localhost:5173/auth'>Đến website của chúng tôi</a>"
            );
        }
        else{
            throw new ApiException(400, "Email đã tồn tại");
        }
    }

    @Override
    public ListCandidateResponse listCandidate(int page, int size) throws ApiException {
        Pageable pageable = PageRequest.of(page - 1, size);

        //page // page.getConent()
        Page<User> users = userRepository.findAllCandidate(pageable);

        ListCandidateResponse data = new ListCandidateResponse();
        List<CandidateResponse> candidateResponses = users.getContent().stream().map(item -> {
            Interview interview = item.getInterview();
            return CandidateResponse.builder()
                    .id(item.getId())
                    .avatar(item.getAvatar())
                    .fullName(item.getProfile() == null ? item.getName() : item.getProfile().getFullName())
                    .email(item.getEmail())
                    .status(item.getStatus())
                    .cv(item.getDocuments().isEmpty() ? null : item.getDocuments().get(0).getUrl())
                    .interview(interview == null ? null : InterviewResponseDto.builder()
                            .date(interview.getDate().toString())
                            .deadline(interview.getDeadline().toString())
                            .id(interview.getId())
                            .reason(interview.getReason())
                            .description(interview.getDescription())
                            .response(interview.getResponse())
                            .build())
                    .build();
        }).toList();

        PageResponse pageResponse = new PageResponse();
        pageResponse.setTotalPage(users.getTotalPages());
        pageResponse.setPageSize(size);
        pageResponse.setActivePage(page);
        data.setCandidates(candidateResponses);
        data.setPage(pageResponse);


        return data;
    }

    @Override
    @Transactional
    public void updateProfileByAdmin(ProfileRequest data, int userId, MultipartFile frontImage, MultipartFile backImage) throws ApiException, IOException {
        User u = getUserById(userId);
        updateProfileFuction(data, u, frontImage, backImage);
    }

    @Transactional
    public void updateMyProfile(ProfileRequest data, MultipartFile frontImage, MultipartFile backImage) throws IOException, ApiException {
        updateProfileFuction(data, getUser(), frontImage, backImage);
    }


    @Transactional
    public void updateProfileFuction(ProfileRequest data, User currentUser, MultipartFile frontImage, MultipartFile backImage) throws ApiException, IOException {
        Profile currentProfile = currentUser.getProfile();
        String description = "Đã cập nhật : ";
        ProfileResponseDto oldProfile = ProfileResponseDto.builder()
                .fullName(currentProfile.getFullName())
                .bod(currentProfile.getBirthDate().toString())
                .address(currentProfile.getAddress())
                .phone(currentProfile.getPhone())
                .build();
        Role currentRole = currentUser.getRole();
        Department cDepartment = currentUser.getDepartment();
        UserResponseDTO oldUser = UserResponseDTO.builder()
                .profile(oldProfile)
                .role(RoleResponseDto.builder()
                        .id(currentRole.getId())
                        .name(currentRole.getName())
                        .build())
                .department(cDepartment == null ? null : DepartmentResponseDto.builder()
                        .id(cDepartment.getId())
                        .name(cDepartment.getName())
                        .build())
                .documents(currentUser.getDocuments().stream().map(item -> {
                    return DocumentResponseDto.builder()
                            .path(item.getUrl())
                            .type(item.getType())
                            .build();
                }).toList())
                .name(currentUser.getName())
                .status(currentUser.getStatus() == null ? null:currentUser.getStatus())
                .build();
        List<Document> documents = new ArrayList<>();
        if(frontImage != null){
            documentReposiroty.deleteDocument(currentUser.getId(), "FrontImage");
            Document d = new Document();
            d.setUser(currentUser);
            d.setType("FrontImage");
            d.setUrl(Util.uploadImage(frontImage));
            documents.add(d);
            description += "ảnh căn cước mặt trước, ";
        }
        if(backImage != null){
            documentReposiroty.deleteDocument(currentUser.getId(), "BackImage");
            Document d = new Document();
            d.setUser(currentUser);
            d.setType("BackImage");
            d.setUrl(Util.uploadImage(backImage));
            documents.add(d);
            description += "ảnh căn cước mặt sau, ";
        }
        if(data.getUser().getName() != null){
            description += "tên tài khoản, ";
            currentUser.setName(data.getUser().getName());
        }

        if(data.getProfile().getFullName() != null){
            description += "họ và tên, ";
            currentProfile.setFullName(data.getProfile().getFullName());
        }
        if(data.getProfile().getPhone() != null){
            description += "số điện thoại, ";
            currentProfile.setPhone(data.getProfile().getPhone());
        }

        if(data.getProfile().getAddress() != null){
            description += "địa chỉ, ";
            currentProfile.setAddress(data.getProfile().getAddress());
        }

        if(data.getProfile().getDob() != null){
            description += "ngày sinh, ";
            currentProfile.setBirthDate(Util.parseDate(data.getProfile().getDob()));
        }

        if(data.getUser().getDepartment() != 0){
            Department department = getDepartment(data.getUser().getDepartment());
            currentUser.setDepartment(department);
            description += "phòng ban, ";
        }

        if(data.getUser().getRole() != 0){
            Role role = getRole(data.getUser().getRole());
            currentUser.setRole(role);
            description += "chức vụ, ";
        }

        if(data.getUser().getStatus() != null ){
            currentUser.setStatus(data.getUser().getStatus());
            description += "trạng thái, ";
        }
        currentUser.setProfile(currentProfile);
        currentUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(currentUser);
        description = description.trim();
        if(description.charAt(description.length() - 1) == ','){
            description = description.substring(0, description.length() - 1);
        }
        ObjectMapper mapper = new ObjectMapper();
        ProfileHistory history = new ProfileHistory();
        if(!documents.isEmpty()){
            documentReposiroty.saveAll(documents);
        }
        User userRequest = getUser();
        if(userRequest.getId() != currentUser.getId()){
            systemHistoryService.addHistory(SystemHistory.builder()
                            .actor(userRequest)
                            .content("Cập nhật tài khoản "+ currentUser.getName() + " ("+ currentUser.getEmail() +"),"+ description.split(":")[1])
                    .build());
        }
        String oldContent = mapper.writeValueAsString(oldUser);
        history.setUser(currentUser);
        history.setDescription(description);
        history.setOldContent(oldContent);
        history.setActor(getUser());
        profileHistoryRepository.save(history);
    }


    @Override
    public List<ProfileHistoryResponseDto> getHistoryUpdate(int userId) throws ApiException {
        List<ProfileHistory> profileHistories  = profileHistoryRepository.findByUserId(userId);
        return profileHistories.stream().map(item -> {
            User actor = item.getActor();
            return ProfileHistoryResponseDto.builder()
                    .description(item.getDescription())
                    .actor(UserResponseDTO.builder()
                            .email(actor.getEmail())
                            .avatar(actor.getAvatar())
                            .name(actor.getName())
                            .role(RoleResponseDto.builder()
                                    .name(actor.getRole().getName())
                                    .build())
                            .build())
                    .id(item.getId())
                    .updateAt(item.getCreatedAt().toString())
                    .oldContent(item.getOldContent())
                    .build();
        }).toList();
    }


    public Department getDepartment(int id) throws ApiException {
        return departmentRepository.findById(id).orElseThrow(() -> new ApiException(400, "Không tìm thấy phòng ban"));
    }

    public Role getRole(int id) throws ApiException {
        return roleRepository.findById(id).orElseThrow(() -> new ApiException(400, "Không tìm thấy chức vụ"));
    }

    @Override
    @Transactional
    public void uploadCVandInfo(String token, MultipartFile cv, UploadCVAndInfo data) throws ApiException, IOException {
        String email = securityUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email);
        Profile checkProfileByPhone = profileRepository.findByPhone(data.getPhone());

        if (user == null) throw new ApiException(404, "Không tìm thấy người dùng");
        if(checkProfileByPhone != null) throw new ApiException(404, "Số điện thoại đã tồn tại");
        if (cv == null || cv.isEmpty()) {
            throw new ApiException(400, "Vui lòng tải lên file CV");
        }
        // Kiểm tra định dạng file (phải là PDF)
        if (!Objects.equals(cv.getContentType(), "application/pdf")) {
            throw new ApiException(400, "CV phải là file PDF");
        }

        // Kiểm tra kích thước file (tối đa 5MB = 5 * 1024 * 1024 bytes)
        long maxFileSize = 5 * 1024 * 1024;
        if (cv.getSize() > maxFileSize) {
            throw new ApiException(400, "Kích thước file tối đa là 5MB");
        }

        String path =  Util.uploadImage(cv);
        Profile profile = new Profile();
        Document document = new Document();
        document.setUser(user);
        document.setType("CV");
        document.setUrl(path);
        profile.setFullName(data.getFullName());
        profile.setAddress(data.getAddress());
        profile.setPhone(data.getPhone());
        profile.setBirthDate(Util.parseDate(data.getDob()));
        profile.setUser(user);

        documentReposiroty.save(document);
        profileRepository.save(profile);
    }

    public void uploadAvatar(String token, MultipartFile avatar) throws ApiException, IOException {
        // Lấy thông tin user từ token
        User user = getUser();

        // Kiểm tra file avatar không null
        if (avatar == null || avatar.isEmpty()) {
            throw new ApiException(401,"File avatar không được để trống");
        }

        // Kiểm tra kích thước file (5MB = 5 * 1024 * 1024 bytes)
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (avatar.getSize() > maxSize) {
            throw new ApiException(401,"File avatar phải nhỏ hơn 5MB");
        }

        // Kiểm tra định dạng file (chỉ cho phép JPG, PNG, GIF)
        String contentType = avatar.getContentType();
        if (!isValidImageType(contentType)) {
            throw new ApiException(401,"File phải là ảnh (JPG, PNG, hoặc GIF)");
        }

        String url = Util.uploadImage(avatar);
        user.setAvatar(url);
        userRepository.save(user);
    }

    @Override
    public UserResponseDTO getUserDetail(int id) throws ApiException {
        User u = getUserById(id);
        Profile p = u.getProfile();
        ProfileResponseDto profile = ProfileResponseDto.builder()
                .id(p.getId())
                .bod(p.getBirthDate().toString())
                .phone(p.getPhone())
                .address(p.getAddress())
                .fullName(p.getFullName())
                .build();
        List<Document> documents = u.getDocuments();
       return UserResponseDTO.builder()
                .id(u.getId())
                .name(u.getName())
                .avatar(u.getAvatar())
               .role(RoleResponseDto.builder()
                       .id(u.getRole().getId())
                       .name(u.getRole().getName())
                       .build())
                .email(u.getEmail())
                .department(u.getDepartment() != null ? DepartmentResponseDto.builder()
                        .id(u.getDepartment().getId())
                        .name(u.getDepartment().getName())
                        .build() : null)
               .documents(documents.stream().map(item -> {
                   return DocumentResponseDto.builder()
                           .path(item.getUrl())
                           .type(item.getType())
                           .build();
               }).toList())
                .status(u.getStatus())
                .profile(profile)
                .createAt(u.getCreatedAt().toString())
                .updateAt(u.getUpdatedAt().toString())
                .build();

    }

    @Override
    public void banUser(BanUserRequest data) throws ApiException {
        User user = getUserById(data.getUserId());
        if(data.getIsBan() == 1 ){
            if(data.getResponse() == null || data.getResponse().isEmpty()){
                throw new ApiException(400, "Vui lòng thêm lý do");
            }
            user.setBan(true);
            userRepository.save(user);
            String emailContent = "<h1>Thông báo</h1>" +
                    "<p>Tài khoản "+ user.getEmail()+" đã bị chặn</p>" +
                    "<p>Tài khoản này sẽ không thể đăng nhập vào hệ thống</p>" +
                    "<p>Lý do: "+ data.getResponse() +"</p>";
            systemHistoryService.addHistory(SystemHistory.builder()
                    .content("Đã chặn đăng nhập tài khoản "+user.getName() +"("+ user.getEmail() +")")
                    .actor(getUser())
                    .build());
            emailService.sendEmail(user.getEmail(), "Thông tin tài khoản",emailContent);

        }
        else if(data.getIsBan() == 0 ){
            user.setBan(false);
            userRepository.save(user);
            systemHistoryService.addHistory(SystemHistory.builder()
                    .content("Đã bỏ chặn đăng nhập tài khoản "+user.getName() +"("+ user.getEmail() +")")
                    .actor(getUser())
                    .build());
        }
        else{
            throw new ApiException(400, "Chặn không hợp lệ");
        }
    }

    @Override
    public void deleteUser(int id) throws ApiException {
        User user = getUserById(id);
        Profile p = user.getProfile();
        Interview i = user.getInterview();
        if(p != null){
            p.setUser(null);
            profileRepository.save(p);
            profileRepository.delete(p);
        }
        if(i != null){
            i.setSender(null);
            i.setCandidate(null);

            interviewRepository.save(i);
            interviewRepository.delete(i);
        }


        user.setProfile(null);
        user.setInterview(null);
        userRepository.save(user);
        userRepository.delete(user);
        systemHistoryService.addHistory(SystemHistory.builder()
                .content("Đã xóa tài khoản ứng viên "+user.getName() +"("+ user.getEmail() +")")
                .actor(getUser())
                .build());
    }

    // Hàm phụ để kiểm tra định dạng ảnh
    private boolean isValidImageType(String contentType) {
        return contentType.equals("image/jpeg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif");
    }

    @Override
    public ListAndPageUserResponseDto getAllUsers(int page, int size, int departmentId, UserStatus status, int roleId, String name, String sortDirection, int hasNote) throws ApiException {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        // Pageable với sort theo createdAt
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, "createdAt"));
        Page<User> users = userRepository.findAllWithFilter(name,departmentId,roleId,status,hasNote,pageable);
        List<ItemUserResponseFindAllDTO> data = users.getContent().stream().map(item -> {
            Note note = item.getNote();
            ItemUserResponseFindAllDTO dto = new ItemUserResponseFindAllDTO();
            dto.setAvatar(item.getAvatar());
            dto.setId(item.getId());
            dto.setEmail(item.getEmail());
            dto.setName(item.getName());
            dto.setRole(item.getRole().getName());
            dto.setStatus(item.getStatus());
            dto.setBan(item.isBan());
            dto.setNote(note == null ? null : NoteResponseDto.builder()
                            .title(note.getTitle())
                            .content(note.getNote())
                            .id(note.getId())
                            .createdBy(UserResponseDTO.builder()
                                    .name(note.getCreatedBy().getName())
                                    .avatar(note.getCreatedBy().getAvatar())
                                    .email(note.getCreatedBy().getEmail())
                                    .build())
                    .build());
            dto.setDepartment(item.getDepartment() == null ? "" : item.getDepartment().getName());
            return dto;
        }).toList();

        PageResponse pageResponse = new PageResponse();
        pageResponse.setTotalPage(users.getTotalPages());
        pageResponse.setPageSize(users.getSize());
        pageResponse.setActivePage(page);

        return ListAndPageUserResponseDto
                .builder()
                .page(pageResponse)
                .data(data)
                .build();
    }




    public User getUser() throws ApiException {
        String email = securityUtil.getEmailRequest();
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new ApiException(404, "Không tìm thấy user");
        }
        return user;
    }

    public User getUserById(int id) throws ApiException {

        return userRepository.findById(id).orElseThrow(() -> new ApiException(404, "Không tìm thấy người dùng"));
    }





}
