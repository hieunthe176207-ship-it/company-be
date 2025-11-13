package com.swp.company.service.imp;

import com.swp.company.dto.request.ChatBotRequest;
import com.swp.company.dto.response.ChatBotResponse;
import com.swp.company.dto.response.RoleResponseDto;
import com.swp.company.entity.Chatbot;
import com.swp.company.entity.Role;
import com.swp.company.entity.User;
import com.swp.company.exception.ApiException;
import com.swp.company.repository.ChatbotRepository;
import com.swp.company.repository.RoleRepository;
import com.swp.company.repository.UserRepository;
import com.swp.company.service.ChatbotService;
import com.swp.company.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {
    private final ChatbotRepository chatbotRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    @Override
    public void addQuestion(ChatBotRequest data) {
        List<Role> roles = roleRepository.findAllById(data.getRoles());
        chatbotRepository.save(Chatbot.builder()
                        .answer(data.getAnswer())
                        .question(data.getQuestion())
                        .roles(roles)
                .build());
    }

    @Override
    public void updateQuestion(ChatBotRequest data, int id) throws ApiException {
        Chatbot question = chatbotRepository.findById(id)
                .orElseThrow(()-> new ApiException(400, "Không được để trống tiêu đề trường dữ liệu"));
        List<Role> roles = roleRepository.findAllById(data.getRoles());
        question.setAnswer(data.getAnswer());
        question.setQuestion(data.getQuestion());
        question.setRoles(roles);
        chatbotRepository.save(question);
    }

    @Override
    public void deleteQuestion(int id) throws ApiException {
        Chatbot question = chatbotRepository.findById(id)
                .orElseThrow(() -> new ApiException(400, "Không tìm thấy câu hỏi để xóa"));

        // Xóa liên kết với role trước (tránh lỗi khóa ngoại)
        question.setRoles(null);
        chatbotRepository.save(question);

        // Sau đó mới xóa câu hỏi
        chatbotRepository.delete(question);
    }

    @Override
    public List<ChatBotResponse> getQuestions() {
        List<Chatbot> chatbots = chatbotRepository.findAll();
        return chatbots.stream().map(item -> {
            return ChatBotResponse.builder()
                    .answer(item.getAnswer())
                    .question(item.getQuestion())
                    .id(item.getId())
                    .createdAt(item.getCreatedAt().toString())
                    .roles(item.getRoles().stream().map(role -> {
                        return RoleResponseDto.builder()
                                .name(role.getName())
                                .id(role.getId())
                                .build();
                    }).toList())
                    .build();
        }).toList();
    }

    @Override
    public List<ChatBotResponse> getQuestionsByRole() throws ApiException {
        User user = getUserByEmail(securityUtil.getEmailRequest());
        List<Chatbot> questions = chatbotRepository.findByRoleId(user.getRole().getId());
        return questions.stream().map(item -> {
           return ChatBotResponse.builder()
                    .answer(item.getAnswer())
                    .question(item.getQuestion())
                    .id(item.getId())
                    .createdAt(item.getCreatedAt().toString())
                    .build();
        }).toList();
    }

    public User getUserByEmail(String email) throws ApiException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ApiException(404, "Không tìm thấy người dùng");
        }
        return user;
    }
}
