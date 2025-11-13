package com.swp.company.service.imp;

import com.swp.company.dto.request.HomeResponse;
import com.swp.company.dto.response.RoleUserCountDto;
import com.swp.company.dto.response.SubmissionCountByMonthDto;
import com.swp.company.dto.response.UserCountByMonthDto;
import com.swp.company.repository.IdeaRepository;
import com.swp.company.repository.SubmissionRepository;
import com.swp.company.repository.UserRepository;
import com.swp.company.service.HomeService;
import com.swp.company.service.UserService;
import com.swp.company.util.common.FormStatus;
import com.swp.company.util.common.IdeaStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {
    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final IdeaRepository ideaRepository;
    @Override
    public HomeResponse getDashBoard() {
        List<Object[]> resultsCandidate = userRepository.countUsersByMonthWithRole5Subquery();
        List<Object[]> resultsEmployee = userRepository.countUsersByRole();
        List<Object[]> resultsForm = submissionRepository.countSubmissionsByMonth(2025);
        return HomeResponse.builder()
                .form(submissionRepository.countForm(FormStatus.PENDING))
                .idea(ideaRepository.countIdeas(IdeaStatus.CHUA_PHAN_HOI))
                .employee(userRepository.countEmployee())
                .candidatePercent(resultsCandidate.stream()
                        .map(row -> new UserCountByMonthDto(
                                (String) row[0],                  // date
                                ((Number) row[1]).intValue()      // count (Object to int)
                        ))
                        .toList())
                .employeePercent(resultsEmployee.stream()
                        .map(row -> new RoleUserCountDto(
                                (String) row[0],
                                ((Number) row[1]).intValue()
                        ))
                        .toList())
                .submissionPercent(resultsForm.stream()
                        .map(obj -> new SubmissionCountByMonthDto(
                                (String) obj[0],     // month name, e.g., "Tháng 1"
                                ((Number) obj[1]).intValue() // count
                        ))
                        .toList())
                .build();
    }
}
