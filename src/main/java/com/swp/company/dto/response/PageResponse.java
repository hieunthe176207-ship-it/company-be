package com.swp.company.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageResponse {
    private int totalPage;
    private int activePage;
    private int pageSize;
}
