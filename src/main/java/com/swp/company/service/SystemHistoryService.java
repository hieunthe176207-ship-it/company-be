package com.swp.company.service;

import com.swp.company.dto.response.SystemHistoryResponse;
import com.swp.company.entity.SystemHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SystemHistoryService {
    void addHistory(SystemHistory systemHistory);
    Map<String, Object> getSystemHistory(int page, int size , LocalDateTime startDate, LocalDateTime endDate, String sort);
}
