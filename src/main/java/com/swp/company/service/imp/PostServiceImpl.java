package com.swp.company.service.imp;

import com.swp.company.dto.request.PostRequestDto;
import com.swp.company.dto.request.UpdatePostRequestDto;
import com.swp.company.dto.response.ListAndPagePostResponseDto;
import com.swp.company.dto.response.PageResponse;
import com.swp.company.dto.response.PostDetailResponseDto;
import com.swp.company.dto.response.PostFileDto;
import com.swp.company.dto.response.UserResponseDTO;
import com.swp.company.entity.Post;
import com.swp.company.entity.PostFile;
import com.swp.company.entity.User;
import com.swp.company.exception.ApiException;
import com.swp.company.repository.PostRepository;
import com.swp.company.repository.PostFileRepository;
import com.swp.company.repository.UserRepository;
import com.swp.company.service.PostService;
import com.swp.company.util.SecurityUtil;
import com.swp.company.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final PostFileRepository postFileRepository;

    @Override
    @Transactional
    public PostDetailResponseDto createPost(PostRequestDto postRequestDto, String token) throws ApiException, IOException {
        if((postRequestDto.getContent() == null || postRequestDto.getContent().isEmpty())  && postRequestDto.getFiles() == null){
            throw new ApiException(400, "Vui lòng thêm nội dung bài viết hoặc là ảnh");
        }
        String email = securityUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ApiException(404,"không tìm thấy người dùng");
        }

        Post post = new Post();
        post.setContent(postRequestDto.getContent());
        post.setUser(user);
        Post savedPost= postRepository.save(post);
        List<PostFileDto> files = new ArrayList<>();
        if (postRequestDto.getFiles() != null) {
            // Kiểm tra số lượng ảnh tối đa 5
            if (postRequestDto.getFiles().size() > 5) {
                throw new RuntimeException("Chỉ được upload tối đa 5 ảnh!");
            }
            for (MultipartFile file : postRequestDto.getFiles()) {
                if (!file.isEmpty()) {
                    // Kiểm tra size file tối đa 5MB
                    long maxSize = 5 * 1024 * 1024; // 5MB
                    if (file.getSize() > maxSize) {
                        throw new ApiException(400,"File quá lớn! Dung lượng tối đa là 5MB.");
                    }
                    // Kiểm tra định dạng file
                    String contentType = file.getContentType();
                    if (contentType == null ||
                        !(contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/jpg"))) {
                        throw new ApiException(400,"Chỉ cho phép upload file ảnh JPG, JPEG, PNG!");
                    }
                        String url = Util.uploadImage(file);
                        PostFile postFile = new PostFile();
                        postFile.setUrl(url);
                        postFile.setPost(savedPost);
                        postFileRepository.save(postFile);
                }
            }
        }
        return PostDetailResponseDto.builder()
                .id(savedPost.getId())
                .content(savedPost.getContent())
                .user(UserResponseDTO.builder()
                        .id(savedPost.getUser().getId())
                        .email(savedPost.getUser().getEmail())
                        .name(savedPost.getUser().getName())
                        .avatar(savedPost.getUser().getAvatar())
                        .build())
                .files(files)
                .createdAt(savedPost.getCreatedAt())
                .updatedAt(savedPost.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public PostDetailResponseDto updatePost(Integer postId, UpdatePostRequestDto updatePostRequestDto, String token) {
        String email = securityUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("không tìm thấy người dùng");
        }

        Post post = postRepository.findById(Long.valueOf(postId))
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Không có quyền cập nhật bài viết này");
        }

        // Chỉ cập nhật content nếu content khác null
        if (updatePostRequestDto.getContent() != null) {
            post.setContent(updatePostRequestDto.getContent());
        }
        Post updatedPost = postRepository.save(post);

        // Lấy danh sách id ảnh hiện có
        List<PostFile> currentFiles = post.getPostFiles();
        List<Integer> deletedIds = updatePostRequestDto.getDeletedImageIds();
        if (deletedIds != null && !deletedIds.isEmpty()) {
            for (PostFile pf : new ArrayList<>(currentFiles)) {
                int pfId = pf.getId();
                if (deletedIds.contains(pfId)) {
                    postFileRepository.delete(pf);
                }
            }
        }
        // Nếu không truyền deletedImageIds thì giữ nguyên ảnh cũ

        List<PostFileDto> files = new ArrayList<>();
        // Thêm ảnh mới
        List<MultipartFile> newImages = updatePostRequestDto.getFiles();
        System.out.println("Số lượng file upload mới: " + (newImages == null ? 0 : newImages.size()));
        if (newImages != null && !newImages.isEmpty()) {
            // Đếm số ảnh hiện tại sau khi xóa
            Post postAfterDelete = postRepository.findById(Long.valueOf(postId)).orElseThrow();
            int currentCount = postAfterDelete.getPostFiles() == null ? 0 : postAfterDelete.getPostFiles().size();
            int newCount = newImages.size();
            if (currentCount + newCount > 5) {
                throw new RuntimeException("Tổng số ảnh không được vượt quá 5!");
            }
            for (MultipartFile file : newImages) {
                if (!file.isEmpty()) {
                    String contentType = file.getContentType();
                    if (contentType == null ||
                        !(contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/jpg"))) {
                        throw new RuntimeException("Chỉ cho phép upload file ảnh JPG, JPEG, PNG!");
                    }
                    try {
                        String url = com.swp.company.util.Util.uploadImage(file);
                        System.out.println("Đã upload file, url: " + url);
                        PostFile postFile = new PostFile();
                        postFile.setUrl(url);
                        postFile.setPost(updatedPost);
                        postFileRepository.save(postFile);
                        files.add(new PostFileDto(url, postFile.getId()));
                    } catch (IOException e) {
                        throw new RuntimeException("Lỗi khi lưu file: " + e.getMessage());
                    }
                }
            }
        }
        // Lấy lại danh sách file còn lại sau khi xóa và thêm mới
        Post postFinal = postRepository.findById(Long.valueOf(postId)).orElseThrow();
        if (postFinal.getPostFiles() != null) {
            for (PostFile pf : postFinal.getPostFiles()) {
                files.add(new PostFileDto(pf.getUrl(), pf.getId()));
            }
        }

        UserResponseDTO userDto = UserResponseDTO.builder()
                .id(updatedPost.getUser().getId())
                .email(updatedPost.getUser().getEmail())
                .name(updatedPost.getUser().getName())
                .avatar(updatedPost.getUser().getAvatar())
                .build();

        return PostDetailResponseDto.builder()
                .id(updatedPost.getId())
                .content(updatedPost.getContent())
                .user(userDto)
                .files(files)
                .createdAt(updatedPost.getCreatedAt())
                .updatedAt(updatedPost.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public void deletePost(Integer postId, String token) {
        String email = securityUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        Post post = postRepository.findById(Long.valueOf(postId))
                .orElseThrow(() -> new RuntimeException("không tìm thấy bài viết"));

        if (!post.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Không có quyền xóa bài viết này");
        }

        postRepository.delete(post);
    }

    @Override
    public ListAndPagePostResponseDto getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepository.findAll(pageable);
        
        List<PostDetailResponseDto> posts = postPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        PageResponse pageResponse = new PageResponse(
                postPage.getTotalPages(),
                postPage.getNumber() + 1,
                postPage.getSize()
        );

        return ListAndPagePostResponseDto.builder()
                .data(posts)
                .page(pageResponse)
                .build();
    }

    @Override
    public PostDetailResponseDto getPostById(Integer postId) {
        Post post = postRepository.findById(Long.valueOf(postId))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));

        UserResponseDTO userDto = UserResponseDTO.builder()
                .id(post.getUser().getId())
                .email(post.getUser().getEmail())
                .name(post.getUser().getName())
                .build();

        List<PostFileDto> files = new ArrayList<>();
        if (post.getPostFiles() != null) {
            for (PostFile pf : post.getPostFiles()) {
                files.add(new PostFileDto(pf.getUrl(), pf.getId()));
            }
        }

        return PostDetailResponseDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .user(userDto)
                .files(files)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    @Override
    public ListAndPagePostResponseDto getAllPostsByUserToken(String token, int page, int size) {
        String email = securityUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepository.findAll(pageable);
        List<PostDetailResponseDto> posts = postPage.getContent().stream()
                .filter(p -> p.getUser() != null && p.getUser().getId() == user.getId())
                .map(this::convertToDto)
                .collect(Collectors.toList());
        PageResponse pageResponse = new PageResponse(
                postPage.getTotalPages(),
                postPage.getNumber() + 1,
                postPage.getSize()
        );
        return ListAndPagePostResponseDto.builder()
                .data(posts)
                .page(pageResponse)
                .build();
    }

    private PostDetailResponseDto convertToDto(Post post) {
        UserResponseDTO userDto = UserResponseDTO.builder()
                .id(post.getUser().getId())
                .email(post.getUser().getEmail())
                .name(post.getUser().getName())
                .avatar(post.getUser().getAvatar())
                .build();
        List<PostFileDto> files = null;
        if (post.getPostFiles() != null && !post.getPostFiles().isEmpty()) {
            files = post.getPostFiles().stream()
                    .map(f -> new PostFileDto(f.getUrl(), f.getId()))
                    .collect(java.util.stream.Collectors.toList());
        }
        return PostDetailResponseDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .user(userDto)
                .files(files)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
} 