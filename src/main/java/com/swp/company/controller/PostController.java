package com.swp.company.controller;

import com.swp.company.dto.request.PostRequestDto;
import com.swp.company.dto.request.UpdatePostRequestDto;
import com.swp.company.dto.response.ListAndPagePostResponseDto;
import com.swp.company.dto.response.PostDetailResponseDto;
import com.swp.company.dto.response.ResponseSuccess;
import com.swp.company.exception.ApiException;
import com.swp.company.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/add-posts")
    public ResponseEntity<ResponseSuccess<PostDetailResponseDto>> createPost(
            @RequestHeader("Authorization") String token,
            @ModelAttribute PostRequestDto postRequestDto) throws IOException, ApiException {
        PostDetailResponseDto post = postService.createPost(postRequestDto, token);
        return ResponseEntity.ok(ResponseSuccess.<PostDetailResponseDto>builder()
                .data(post)
                .message("đăng bài thành công")
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseSuccess<PostDetailResponseDto>> updatePost(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id,
            @ModelAttribute UpdatePostRequestDto updatePostRequestDto) {
        PostDetailResponseDto post = postService.updatePost(id, updatePostRequestDto, token);
        return ResponseEntity.ok(ResponseSuccess.<PostDetailResponseDto>builder()
                .data(post)
                .message("chỉnh sửa bài đăng thành công")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseSuccess<Void>> deletePost(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id) {
        postService.deletePost(id, token);
        return ResponseEntity.ok(ResponseSuccess.<Void>builder()
                .data(null)
                .message("xóa bài đăng thành công")
                .build());
    }
    @GetMapping("/get-all-post")
    public ResponseEntity<ResponseSuccess<ListAndPagePostResponseDto>> getAllPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        ListAndPagePostResponseDto posts = postService.getAllPosts(page, size);
        return ResponseEntity.ok(ResponseSuccess.<ListAndPagePostResponseDto>builder()
                .data(posts)
                .message("lấy danh sách bài đăng thành công")
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseSuccess<PostDetailResponseDto>> getPostById(@PathVariable Integer id) {
        PostDetailResponseDto post = postService.getPostById(id);
        return ResponseEntity.ok(ResponseSuccess.<PostDetailResponseDto>builder()
                .data(post)
                .message("lấy bài đăng thành công")
                .build());
    }

    @GetMapping("/get-all-post-by-user")
    public ResponseEntity<ResponseSuccess<ListAndPagePostResponseDto>> getAllPostsByUserToken(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        ListAndPagePostResponseDto posts = postService.getAllPostsByUserToken(token, page, size);
        return ResponseEntity.ok(ResponseSuccess.<ListAndPagePostResponseDto>builder()
                .data(posts)
                .message("lấy danh sách bài đăng thành công")
                .build());
    }
} 