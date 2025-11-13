package com.swp.company.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostFileDto {
    private String url;
    private Integer postFileId;

    public PostFileDto(String url, Integer postFileId) {
        this.url = url;
        this.postFileId = postFileId;
    }
} 