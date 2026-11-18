package com.alejandrojimenez.jobtracker.jobapplication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long>, JpaSpecificationExecutor<JobApplication> {

    /** One row per status that has at least one application. */
    @Query("select j.status as status, count(j) as total from JobApplication j group by j.status")
    List<StatusCount> countByStatus();

    interface StatusCount {
        ApplicationStatus getStatus();

        long getTotal();
    }
}
