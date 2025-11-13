package com.swp.company.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ListCandidateResponse  {
    private List<CandidateResponse> candidates;
    private PageResponse page;
}
