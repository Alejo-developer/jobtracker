package com.alejandrojimenez.jobtracker.jobapplication.dto;

import com.alejandrojimenez.jobtracker.jobapplication.ApplicationStatus;
import com.alejandrojimenez.jobtracker.jobapplication.JobApplication;

import java.time.Instant;
import java.time.LocalDate;

public record JobApplicationResponse(
        Long id,
        String company,
        String position,
        String jobUrl,
        String location,
        String source,
        ApplicationStatus status,
        LocalDate appliedOn,
        String notes,
        Instant createdAt,
        Instant updatedAt) {

    public static JobApplicationResponse from(JobApplication application) {
        return new JobApplicationResponse(
                application.getId(),
                application.getCompany(),
                application.getPosition(),
                application.getJobUrl(),
                application.getLocation(),
                application.getSource(),
                application.getStatus(),
                application.getAppliedOn(),
                application.getNotes(),
                application.getCreatedAt(),
                application.getUpdatedAt());
    }
}
