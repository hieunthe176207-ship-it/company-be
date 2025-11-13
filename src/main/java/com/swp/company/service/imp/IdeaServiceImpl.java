package com.swp.company.service.imp;

import com.swp.company.dto.request.IdeaRequest;
import com.swp.company.dto.request.ReplyIdeaRequest;
import com.swp.company.dto.response.IdeasResponse;
import com.swp.company.dto.response.PageResponse;
import com.swp.company.dto.response.UserResponseDTO;
import com.swp.company.entity.Ideas;
import com.swp.company.entity.User;
import com.swp.company.exception.ApiException;
import com.swp.company.repository.IdeaRepository;
import com.swp.company.repository.UserRepository;
import com.swp.company.service.IdeasService;
import com.swp.company.util.SecurityUtil;
import com.swp.company.util.common.IdeaStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IdeaServiceImpl implements IdeasService {
    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    @Override
    public void addIdeas(IdeaRequest data) throws ApiException {
        User u =getUser();
        if(data.getContent().isEmpty()){
            throw new ApiException(400, "Nội dung không được để trống");
        }
        System.out.println(data.getIsAnonymous());
        ideaRepository.save(Ideas.builder()
                        .content(data.getContent())
                        .isAnonymous(data.getIsAnonymous() == 1)
                        .status(IdeaStatus.CHUA_PHAN_HOI)
                        .user(u)
                .build());
    }

    private HashMap<String, Object> buildIdeasResponse(Page<Ideas> ideasPage, int size, int activePage) {
        HashMap<String, Object> data = new HashMap<>();
        // Ánh xạ danh sách Ideas sang danh sách IdeasResponse
        List<IdeasResponse> ideasResponses = ideasPage.getContent().stream().map(item -> {
            // Sử dụng toán tử ternary để xử lý trường hợp ẩn danh gọn gàng hơn
            UserResponseDTO userResponse = !item.isAnonymous() && item.getUser() != null ?
                    UserResponseDTO.builder()
                            .avatar(item.getUser().getAvatar())
                            .email(item.getUser().getEmail())
                            .name(item.getUser().getName())
                            .build() : null;

            return IdeasResponse.builder()
                    .id(item.getId())
                    .content(item.getContent())
                    .isAnonymous(item.isAnonymous())
                    .status(item.getStatus().getText())
                    .date(item.getCreatedAt().toString())
                    .reply(item.getReply())
                    .user(userResponse)
                    .build();
        }).toList(); // Sử dụng collect(Collectors.toList())

        data.put("ideas", ideasResponses);

        // Xây dựng đối tượng phân trang
        PageResponse pageResponse = new PageResponse();
        pageResponse.setTotalPage(ideasPage.getTotalPages());
        pageResponse.setPageSize(size);
        pageResponse.setActivePage(activePage); // Sử dụng activePage được truyền vào
        data.put("page", pageResponse);

        return data;
    }

    // Phương thức để lấy tất cả ý tưởng
    @Override
    public HashMap<String, Object> getAllIdeas(int page, int size) throws ApiException {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Ideas> ideas = ideaRepository.findAll(pageable);
        return buildIdeasResponse(ideas, size, page);
    }

    // Phương thức để lấy ý tưởng của tôi
    @Override
    public HashMap<String, Object> getMyIdeas(int page, int size) throws ApiException {
        User u = getUser();
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Ideas> ideas = ideaRepository.findIdeasByUser(u.getId(), pageable);
        return buildIdeasResponse(ideas, size, page);
    }


    public User getUser() throws ApiException {
        String email = securityUtil.getEmailRequest();
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new ApiException(404, "Không tìm thấy user");
        }
        return user;
    }

    @Override
    public Ideas getIdea(int id) throws ApiException {
        return ideaRepository.findById(id).orElseThrow(() -> new ApiException(404, "Không tìm thấy"));
    }

    @Override
    public void updateIdea(ReplyIdeaRequest data) throws ApiException {
        Ideas ideas = getIdea(data.getId());
        if(ideas.getReply() == null){
            ideas.setReply(data.getReply());
            ideas.setStatus(IdeaStatus.DA_PHAN_HOI);
            ideaRepository.save(ideas);
        }
        else{
            throw new ApiException(400, "Ý kiến này đã được phản hồi");
        }
    }


}
