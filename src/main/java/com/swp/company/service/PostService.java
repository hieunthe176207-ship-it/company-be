package com.swp.company.service;

import com.swp.company.dto.request.PostRequestDto;
import com.swp.company.dto.request.UpdatePostRequestDto;
import com.swp.company.dto.response.ListAndPagePostResponseDto;
import com.swp.company.dto.response.PostDetailResponseDto;
import com.swp.company.exception.ApiException;

import java.io.IOException;
import java.util.List;

public interface PostService {
    PostDetailResponseDto createPost(PostRequestDto postRequestDto, String token) throws ApiException, IOException;
    PostDetailResponseDto updatePost(Integer postId, UpdatePostRequestDto updatePostRequestDto, String token);
    void deletePost(Integer postId, String token);
    ListAndPagePostResponseDto getAllPosts(int page, int size);
    PostDetailResponseDto getPostById(Integer postId);
    ListAndPagePostResponseDto getAllPostsByUserToken(String token, int page, int size);
} 