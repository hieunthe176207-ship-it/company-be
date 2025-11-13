package com.swp.company.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostRequestDto {
    private String content;
    // Danh sách id ảnh muốn xóa
    private List<Integer> deletedImageIds;
    // Danh sách file ảnh mới muốn upload thêm
    private List<MultipartFile> files;
} 