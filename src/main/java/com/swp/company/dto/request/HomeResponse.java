package com.swp.company.dto.request;

import com.swp.company.dto.response.RoleUserCountDto;
import com.swp.company.dto.response.SubmissionCountByMonthDto;
import com.swp.company.dto.response.UserCountByMonthDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HomeResponse {
    private int employee;
    private int form;
    private int idea;

    private List<UserCountByMonthDto> candidatePercent;
    private List<RoleUserCountDto> employeePercent;
    List<SubmissionCountByMonthDto> submissionPercent;

}
