package com.swp.company.service;

import com.swp.company.dto.request.HandleFormRequest;
import com.swp.company.dto.request.SubmissionDto;
import com.swp.company.dto.response.FormResponseDto;
import com.swp.company.entity.Form;
import com.swp.company.entity.SubmissionAnswer;
import com.swp.company.exception.ApiException;
import com.swp.company.util.common.FormStatus;

import java.util.List;
import java.util.Map;

public interface FormService {
    public void addForm(Form form) throws ApiException;
    public FormResponseDto getForm(int id) throws ApiException;
    public List<FormResponseDto> getAllForms() throws ApiException;
    public void updateForm(int id, Form form) throws ApiException;
    public void deleteForm(int id) throws ApiException;
    public void submissForm(SubmissionDto data,  String token) throws ApiException;
    public Map getHistorySubmit(String token , int size , int page) throws ApiException;
    public Map getAllSubmit(int size , int page, int formId, String date, FormStatus status, String sort) throws ApiException;
    public void handleFormAction(HandleFormRequest data ) throws ApiException;
    public Map getSubmitAnwer(int submitId) throws ApiException;
}
