package com.swp.company.service.imp;

import com.swp.company.dto.response.PageResponse;
import com.swp.company.dto.response.SystemHistoryResponse;
import com.swp.company.dto.response.UserResponseDTO;
import com.swp.company.entity.SystemHistory;
import com.swp.company.entity.User;
import com.swp.company.repository.SystemHistoryRepository;
import com.swp.company.service.SystemHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SystemHistoryServiceImpl implements SystemHistoryService {
    private final SystemHistoryRepository systemHistoryRepository;
    @Override
    public void addHistory(SystemHistory systemHistory) {
        systemHistoryRepository.save(systemHistory);
    }

    @Override
    public Map<String, Object> getSystemHistory(int page, int size, LocalDateTime startDate, LocalDateTime endDate, String sort) {
        Sort.Direction direction = Sort.Direction.DESC;
        if ("asc".equalsIgnoreCase(sort)) {
            direction = Sort.Direction.ASC;
        }
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, "createdAt"));
        Page<SystemHistory> histories = systemHistoryRepository.findAllByDateAndOrder(startDate, endDate, pageable);
        Map<String, Object> response = new HashMap<>();
        List<SystemHistoryResponse> systemHistoryResponses = histories.stream().map(item -> {
            User actor = item.getActor();
            return SystemHistoryResponse.builder()
                    .id(item.getId())
                    .actor(UserResponseDTO.builder()
                            .avatar(actor.getAvatar())
                            .name(actor.getName())
                            .email(actor.getEmail())
                            .build())
                    .content(item.getContent())
                    .createdAt(item.getCreatedAt().toString())
                    .build();
        }).toList();
        response.put("content", systemHistoryResponses);
        PageResponse pageResponse = new PageResponse();
        pageResponse.setActivePage(page);
        pageResponse.setTotalPage(histories.getTotalPages());
        pageResponse.setPageSize(size);
        response.put("page", pageResponse);
        return response;
    }
}
