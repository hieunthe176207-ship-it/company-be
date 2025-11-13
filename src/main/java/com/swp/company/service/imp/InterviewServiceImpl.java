package com.swp.company.service.imp;

import com.swp.company.dto.request.InterviewRequestDto;
import com.swp.company.dto.response.InterviewResponseDto;
import com.swp.company.dto.response.UserResponseDTO;
import com.swp.company.entity.Interview;
import com.swp.company.entity.SystemHistory;
import com.swp.company.entity.User;
import com.swp.company.exception.ApiException;
import com.swp.company.repository.InterviewRepository;
import com.swp.company.repository.UserRepository;
import com.swp.company.service.EmailService;
import com.swp.company.service.InterviewService;
import com.swp.company.service.SystemHistoryService;
import com.swp.company.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {
    private final InterviewRepository interviewRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final EmailService emailService;
    private final SystemHistoryService systemHistoryService;
    @Override
    public void addInterview(InterviewRequestDto interviewRequestDto) throws ApiException {
        User sender = getUserById(interviewRequestDto.getSenderId());
        User receiver = getUserById(interviewRequestDto.getReceiverId());
        interviewRepository.save(Interview.builder()
                        .date(interviewRequestDto.getDate())
                        .deadline(interviewRequestDto.getDeadline())
                        .description(interviewRequestDto.getDescription())
                        .sender(sender)
                        .candidate(receiver)
                .build());
        systemHistoryService.addHistory(SystemHistory.builder()
                        .actor(sender)
                        .content(sender.getName()+ " (" + sender.getEmail() + ") " +"đã tạo lịch phỏng vấn cho ứng viên " + receiver.getName() + " (" + receiver.getEmail() + ") ")
                .build());
        String emailContent = "<h1>Lịch phỏng vấn</h1>" +
                "<p>Bạn đã nhận được lịch phỏng vấn từ HR, vui lòng truy cập vào website để xem thông tin chi tiết và phản hồi</p>" +
                "<p><b>Lưu ý:</b> Vui lòng phản hồi trước thời hạn cho phép </p>";
        emailService.sendEmail(
                receiver.getEmail(),
                "Lịch phỏng vấn",
                emailContent
        );
    }

    @Override
    public InterviewResponseDto getInterviewById() throws ApiException {
        String email = securityUtil.getEmailRequest();
        Interview interview = interviewRepository.findByUserEmail(email);
        if (interview == null) {
            throw new ApiException(404, "Không tìm thấy lịch phỏng vấn");
        }
        return InterviewResponseDto.builder()
                .id(interview.getId())
                .date(interview.getDate().toString())
                .deadline(interview.getDeadline().toString())
                .description(interview.getDescription())
                .reason(interview.getReason())
                .response(interview.getResponse())
                .user(UserResponseDTO.builder()
                        .email(interview.getSender().getEmail())
                        .avatar(interview.getSender().getAvatar())
                        .name(interview.getSender().getName())
                        .build())
                .build();
    }

    @Override
    public void updateInterview(InterviewRequestDto interviewRequestDto) throws ApiException {
        Interview interview = findById(interviewRequestDto.getId());
        User receiver = getUserById(interview.getCandidate().getId());
        if (interviewRequestDto.getDate() != null) {
            interview.setDate(interviewRequestDto.getDate());
        }
        if (interviewRequestDto.getDeadline() != null) {
            interview.setDeadline(interviewRequestDto.getDeadline());
        }
        if (interviewRequestDto.getDescription() != null) {
            interview.setDescription(interviewRequestDto.getDescription());
        }
        interview.setReason(null);
        interview.setResponse(0);
        interviewRepository.save(interview);

        systemHistoryService.addHistory(SystemHistory.builder()
                .actor(getUser())
                .content(getUser().getName()+ " (" + getUser().getEmail() + ") " +"đã cập nhật lịch phỏng vấn cho ứng viên " + receiver.getName() + " (" + receiver.getEmail() + ") ")
                .build());
        String emailContent = "<h1>Lịch phỏng vấn</h1>" +
                "<p>HR đã cập nhật lại thông tin phỏng vấn, vui lòng vào website để xem chi tiết</p>" +
                "<p><b>Lưu ý:</b> Vui lòng phản hồi trước thời hạn cho phép </p>";


        emailService.sendEmail(
                interview.getCandidate().getEmail(),
                "Lịch phỏng vấn",
                emailContent);
    }

    private Interview findById (int id) throws ApiException {
        return interviewRepository.findById(id).orElseThrow(() -> new ApiException(404, "Không tìm thấy lịch phỏng vấn"));
    }

    @Transactional
    @Override
    public void deleteInterview(int id) throws ApiException {
        Interview interview = findById(id);
        User receiver = interview.getCandidate(); // lấy trước khi set null
        interview.setSender(null);
        interview.setCandidate(null);
        interviewRepository.save(interview);
        systemHistoryService.addHistory(SystemHistory.builder()
                .actor(getUser())
                .content(getUser().getName() + " (" + getUser().getEmail() + ") "
                        + "đã xóa lịch phỏng vấn cho ứng viên " + receiver.getName() + " (" + receiver.getEmail() + ")")
                .build());

        emailService.sendEmail(
                receiver.getEmail(),
                "Lịch phỏng vấn",
                "<h1>Lịch phỏng vấn</h1><p>HR đã xóa lịch phỏng vấn của bạn</p>"
        );
    }


    @Override
    public Object responseInterview(InterviewRequestDto interviewRequestDto ) throws ApiException {
        Interview interview = findById(interviewRequestDto.getId());
        if(checkDeadline(interview.getDeadline())){
            throw new ApiException(404, "Bạn đã quá thời hạn phản hồi");
        }
        if(interviewRequestDto.getResponse() == 1){
            interview.setResponse(1);
        }
        else if(interviewRequestDto.getResponse() == 2){
            interview.setResponse(2);
            interview.setReason(interviewRequestDto.getReason());
        }
        else{
            throw new ApiException(404, "Phản hồi không hợp lệ");
        }
        interviewRepository.save(interview);
        return null;
    }


    public User getUserById(int id) throws ApiException {
        return userRepository.findById(id).orElseThrow(() -> new ApiException(404, "Không tìm thấy người dùng"));
    }

    public boolean checkDeadline(LocalDateTime deadline) {
        LocalDateTime now = LocalDateTime.now(); // thời gian hiện tại theo server
        if (now.isAfter(deadline)) {
            return true;
        } else if (now.isBefore(deadline)) {
            return false;
        } else {
            return false;
        }
    }

    public User getUser() throws ApiException {
        String email = securityUtil.getEmailRequest();
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new ApiException(404, "Không tìm thấy user");
        }
        return user;
    }




}
