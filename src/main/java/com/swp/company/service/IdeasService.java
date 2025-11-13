package com.swp.company.service;

import com.swp.company.dto.request.IdeaRequest;
import com.swp.company.dto.request.ReplyIdeaRequest;
import com.swp.company.entity.Ideas;
import com.swp.company.exception.ApiException;

import java.util.HashMap;

public interface IdeasService {
   public void addIdeas(IdeaRequest data) throws ApiException;
   public HashMap<String, Object> getAllIdeas(int page, int size) throws ApiException;
   public HashMap<String, Object> getMyIdeas(int page, int size) throws ApiException;
   public Ideas getIdea(int id) throws ApiException;
   public void updateIdea(ReplyIdeaRequest data) throws ApiException;


}
