package com.swp.company.service.imp;

import com.swp.company.dto.request.NoteRequestDto;
import com.swp.company.dto.response.NoteResponseDto;
import com.swp.company.dto.response.UserResponseDTO;
import com.swp.company.entity.Note;
import com.swp.company.entity.User;
import com.swp.company.exception.ApiException;
import com.swp.company.repository.NoteRepository;
import com.swp.company.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
    public final NoteRepository noteRepository;
    public final UserServiceImpl userService;
    @Override
    public void addNote(NoteRequestDto noteRequestDto) throws ApiException {
        User createdBy = userService.getUser();
        User employee = userService.getUserById(noteRequestDto.getEmployeeId());
        noteRepository.save(Note.builder()
                        .note(noteRequestDto.getContent())
                        .title(noteRequestDto.getTitle())
                        .createdBy(createdBy)
                        .employee(employee)
                .build());
    }

    @Override
    public void updateNote(NoteRequestDto noteRequestDto, int id) throws ApiException {
        Note note = findNoteById(id);
        User createdBy = userService.getUser();
        note.setTitle(noteRequestDto.getTitle());
        note.setNote(noteRequestDto.getContent());
        note.setCreatedBy(createdBy);
        noteRepository.save(note);

    }
    private Note findNoteById(int id ) throws ApiException {
        return noteRepository.findById(id).orElseThrow(() -> new ApiException(404, "Không tìn thấy ghi chú"));
    }

    @Override
    public void deleteNote(int id) throws ApiException {
        Note note = findNoteById(id);
        // Gỡ liên kết để tránh lỗi foreign key
        if (note.getCreatedBy() != null) {
            note.setCreatedBy(null);
        }
        if (note.getEmployee() != null) {
            note.setEmployee(null);
        }
        noteRepository.save(note);
    }

}
