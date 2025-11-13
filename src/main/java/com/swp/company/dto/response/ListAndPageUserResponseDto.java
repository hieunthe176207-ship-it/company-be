package com.swp.company.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ListAndPageUserResponseDto {
    private List<ItemUserResponseFindAllDTO> data;
    private PageResponse page;
}
