package com.alejandrojimenez.jobtracker.jobapplication;

import com.alejandrojimenez.jobtracker.common.ResourceNotFoundException;
import com.alejandrojimenez.jobtracker.jobapplication.dto.ApplicationStats;
import com.alejandrojimenez.jobtracker.jobapplication.dto.JobApplicationRequest;
import com.alejandrojimenez.jobtracker.jobapplication.dto.JobApplicationResponse;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.alejandrojimenez.jobtracker.jobapplication.JobApplicationSpecifications.hasStatus;
import static com.alejandrojimenez.jobtracker.jobapplication.JobApplicationSpecifications.matchesText;

@Service
@Transactional(readOnly = true)
public class JobApplicationService {

    private static final Sort NEWEST_FIRST = Sort.by(Sort.Direction.DESC, "createdAt");

    private final JobApplicationRepository repository;

    public JobApplicationService(JobApplicationRepository repository) {
        this.repository = repository;
    }

    public List<JobApplicationResponse> findAll(ApplicationStatus status, String search) {
        return repository.findAll(hasStatus(status).and(matchesText(search)), NEWEST_FIRST).stream()
                .map(JobApplicationResponse::from)
                .toList();
    }

    public JobApplicationResponse findById(Long id) {
        return JobApplicationResponse.from(getOrThrow(id));
    }

    @Transactional
    public JobApplicationResponse create(JobApplicationRequest request) {
        ApplicationStatus status = request.status() != null ? request.status() : ApplicationStatus.SAVED;
        JobApplication application = new JobApplication(request.company().trim(), request.position().trim(), status);
        copyOptionalFields(request, application);
        return JobApplicationResponse.from(repository.save(application));
    }

    @Transactional
    public JobApplicationResponse update(Long id, JobApplicationRequest request) {
        JobApplication application = getOrThrow(id);
        application.setCompany(request.company().trim());
        application.setPosition(request.position().trim());
        if (request.status() != null) {
            application.setStatus(request.status());
        }
        copyOptionalFields(request, application);
        // saveAndFlush so the response carries the new updatedAt
        return JobApplicationResponse.from(repository.saveAndFlush(application));
    }

    @Transactional
    public JobApplicationResponse updateStatus(Long id, ApplicationStatus status) {
        JobApplication application = getOrThrow(id);
        application.setStatus(status);
        return JobApplicationResponse.from(repository.saveAndFlush(application));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    public ApplicationStats stats() {
        Map<ApplicationStatus, Long> counts = repository.countByStatus().stream()
                .collect(Collectors.toMap(
                        JobApplicationRepository.StatusCount::getStatus,
                        JobApplicationRepository.StatusCount::getTotal));
        return ApplicationStats.fromCounts(counts);
    }

    private JobApplication getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una postulación con id " + id));
    }

    private static void copyOptionalFields(JobApplicationRequest request, JobApplication application) {
        application.setJobUrl(blankToNull(request.jobUrl()));
        application.setLocation(blankToNull(request.location()));
        application.setSource(blankToNull(request.source()));
        application.setAppliedOn(request.appliedOn());
        application.setNotes(blankToNull(request.notes()));
    }

    /** Empty form fields arrive as "" — store them as null so the data stays consistent. */
    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
