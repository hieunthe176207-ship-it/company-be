package com.swp.company.service;

import com.swp.company.dto.request.NoteRequestDto;
import com.swp.company.dto.response.NoteResponseDto;
import com.swp.company.exception.ApiException;

public interface NoteService {
    void addNote(NoteRequestDto noteRequestDto) throws ApiException;
    void updateNote(NoteRequestDto noteRequestDto, int id) throws ApiException;
    void deleteNote(int id) throws ApiException;

}
