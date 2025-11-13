package com.swp.company.service.imp;

import com.swp.company.dto.request.AnswerDto;
import com.swp.company.dto.request.HandleFormRequest;
import com.swp.company.dto.request.SubmissionDto;
import com.swp.company.dto.response.*;
import com.swp.company.entity.*;
import com.swp.company.exception.ApiException;
import com.swp.company.repository.*;
import com.swp.company.service.FormService;
import com.swp.company.service.SystemHistoryService;
import com.swp.company.util.SecurityUtil;
import com.swp.company.util.Util;
import com.swp.company.util.common.FormStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class FormServiceImpl implements FormService {
    private final FormRepository formRepository;
    private final FormDetailRepository formDetailRepository;
    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final SecurityUtil securityUtil;
    private final SystemHistoryService systemHistoryService;
    private final SubmissionAnswerRepository submissionAnswerRepository;
    @Override
    public void addForm(Form form) throws ApiException {
        if (form.getName() == null || form.getName().trim().isEmpty()) {
            throw new ApiException(400, "Không được để trống tên biểu mẫu");
        }

        List<FormDetail> formDetails = form.getFormDetails();
        for (FormDetail item : formDetails) {
            if (item.getTitle() == null || item.getTitle().trim().isEmpty()) {
                throw new ApiException(400, "Không được để trống tiêu đề trường dữ liệu");
            }
            item.setVersion(1);
            item.setForm(form);
        }
        form.setFormDetails(formDetails);
        form.setVersion(1);
        formRepository.save(form);
        systemHistoryService.addHistory(SystemHistory.builder()
                .actor(getUser())
                .content("Thêm biểu mẫu "+form.getName())
                .build());
    }

    @Override
    public FormResponseDto getForm(int id) throws ApiException {
        Form form = getFormById(id);
        return FormResponseDto.builder()
                .id(form.getId())
                .name(form.getName())
                .description(form.getDescription())
                .createAt(form.getCreatedAt().toString())
                .details(formDetailRepository.findAllByVersion(form.getId(), form.getVersion()).stream().map(item -> FormDetailResponseDto.builder()
                        .id(item.getId())
                        .name(item.getTitle())
                        .type(item.getType())
                        .build()).toList())
                .build();
    }

    @Override
    public List<FormResponseDto> getAllForms() throws ApiException {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Form> forms = formRepository.findAllForm(sort);
        return forms.stream().map(item -> {
            return FormResponseDto.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .description(item.getDescription())
                    .createAt(item.getCreatedAt().toString())
                    .build();
        }).toList();
    }

    @Override
    @Transactional
    public void updateForm(int id, Form form) throws ApiException {
        Form oldForm = getFormById(id);
        if (form.getName() == null || form.getName().trim().isEmpty()) {
            throw new ApiException(400, "Không được để trống tên biểu mẫu");
        }
        int version = oldForm.getVersion() + 1;
        oldForm.setVersion(version);
        oldForm.setName(form.getName());
        oldForm.setDescription(form.getDescription());
        oldForm.setFormDetails(form.getFormDetails());
        List<FormDetail> formDetails = form.getFormDetails();
        for (FormDetail item : formDetails) {
            if (item.getTitle() == null || item.getTitle().trim().isEmpty()) {
                throw new ApiException(400, "Không được để trống tiêu đề trường dữ liệu");
            }
            item.setVersion(version);
            item.setForm(oldForm);
        }
        formRepository.save(oldForm);
        systemHistoryService.addHistory(SystemHistory.builder()
                .actor(getUser())
                .content("Cập nhật biểu mẫu "+form.getName())
                .build());
    }

    @Override
    public void deleteForm(int id) throws ApiException {
        Form form = getFormById(id);
        form.setDelete(true);
        formRepository.save(form);
        systemHistoryService.addHistory(SystemHistory.builder()
                .actor(getUser())
                .content("Đã xóa biểu mẫu "+form.getName())
                .build());
    }

    @Override
    public void submissForm(SubmissionDto data, String token) throws ApiException {
        User user = getUser();
        Form form = getFormById(data.getFormId());

        Submission submit = Submission.builder()
                .form(form)
                .employee(user)
                .status(FormStatus.PENDING)
                .formVersion(form.getVersion())
                .build();
        List<SubmissionAnswer> answers = data.getAnswers().stream().map(item -> {
            return SubmissionAnswer.builder()
                    .formDetail(formDetailRepository.findById(item.getFormDetailId()).get())
                    .submission(submit)
                    .answer(item.getAnswer())
                    .build();
        }).toList();
        submit.setAnswers(answers);
        submissionRepository.save(submit);
    }

    @Override
    public Map getHistorySubmit(String token, int size, int page) throws ApiException {
        User user = getUser();
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Submission> submissions = submissionRepository.findHistorySubmit(user.getId(), pageable);
        PageResponse pageResponse = new PageResponse();
        pageResponse.setPageSize(size);
        pageResponse.setActivePage(page);
        pageResponse.setTotalPage(submissions.getTotalPages());
        Map<String, Object> response = new HashMap<>();
        response.put("content", submissions.getContent().stream().map(item -> {
            return HistorySubmitResponseDto.builder()
                    .name(item.getForm().getName())
                    .status(item.getStatus())
                    .response(item.getResponse())
                    .id(item.getId())
                    .createAt(item.getCreatedAt().toString())
                    .build();
        }).toList());
        response.put("page", pageResponse);
        return response;
    }

    @Override
    public Map getAllSubmit(int size, int page, int formId, String date, FormStatus status, String sort) throws ApiException {
        Sort sortOrder = sort != null && sort.equalsIgnoreCase("asc")
                ? Sort.by("createdAt").ascending()
                : Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sortOrder);

        Page<Submission> submissions = submissionRepository.findAllSubmitForm(formId, status, date, pageable);
        PageResponse pageResponse = new PageResponse();
        pageResponse.setPageSize(size);
        pageResponse.setActivePage(page);
        pageResponse.setTotalPage(submissions.getTotalPages());

        Map<String, Object> response = new HashMap<>();
        response.put("content", submissions.getContent().stream().map(item ->

                HistorySubmitResponseDto.builder()
                        .employee(UserResponseDTO.builder()
                                .name(item.getEmployee().getName())
                                .email(item.getEmployee().getEmail())
                                .avatar(item.getEmployee().getAvatar())
                                .build())
                        .name(item.getForm().getName())
                        .status(item.getStatus())
                        .response(item.getResponse())
                        .id(item.getId())
                        .createAt(item.getCreatedAt().toString())
                        .build()
        ).toList());
        response.put("page", pageResponse);
        return response;
    }

    @Override
    public void handleFormAction(HandleFormRequest data) throws ApiException {
       Submission submission = submissionRepository.findById(data.getFormId())
                .orElseThrow(() -> new ApiException(404, "Không tìm thấy đơn"));
       submission.setResponse(data.getResponse());
        switch (data.getAction()){
            case "approve":
                submission.setStatus(FormStatus.APPROVE);
                submissionRepository.save(submission);
                systemHistoryService.addHistory(SystemHistory.builder()
                        .actor(getUser())
                        .content("Đã chấp thuận biểu mẫu " + submission.getForm().getName()
                                + " do " + submission.getEmployee().getName()
                                + " (" + submission.getEmployee().getEmail() + ") gửi")
                        .build());
                break;
            case "reject":
                submission.setStatus(FormStatus.REJECT);
                submissionRepository.save(submission);
                systemHistoryService.addHistory(SystemHistory.builder()
                        .actor(getUser())
                        .content("Đã từ chối biểu mẫu " + submission.getForm().getName()
                                + " do " + submission.getEmployee().getName()
                                + " (" + submission.getEmployee().getEmail() + ") gửi")
                        .build());
                break;
            default:
                throw new ApiException(400, "Trạng thái không hợp lệ");
        }


    }

    @Override
    public Map getSubmitAnwer(int submitId) throws ApiException {
        Submission submission = submissionRepository.findById(submitId)
                .orElseThrow(() -> new ApiException(404, "Không tìm thấy đơn"));
        int version = submission.getFormVersion();
        Form form = submission.getForm();
        List<FormDetail> details = formDetailRepository.findAllByVersion(form.getId(), version);
        List<SubmissionAnswer> answers = submissionAnswerRepository.findBySubmissionId(submission.getId());

        FormResponseDto formResponseDto = FormResponseDto.builder()
                .name(form.getName())
                .description(form.getDescription())
                .fullName(submission.getEmployee().getProfile().getFullName())
                .createAt(submission.getCreatedAt().toString())
                .details(details.stream().map(item -> {
                    return FormDetailResponseDto.builder()
                            .name(item.getTitle())
                            .id(item.getId())
                            .type(item.getType())
                            .build();
                }).toList())
                .build();

        List<SubmitAnswerResponse> anwsersForm = answers.stream().map((item -> {
            return SubmitAnswerResponse.builder()
                    .formDetailId(item.getFormDetail().getId())
                    .answer(item.getAnswer())
                    .build();
        })).toList();



        Map<String, Object> response = new HashMap<>();
        response.put("form", formResponseDto);
        response.put("answer", anwsersForm);
        return response;
    }


    public Form getFormById(int id) throws ApiException {
        return formRepository.findById(id).orElseThrow(() -> new ApiException(404, "Không tìm thấy biểu mẫu"));
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
