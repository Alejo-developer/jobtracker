package com.alejandrojimenez.jobtracker.jobapplication;

import com.alejandrojimenez.jobtracker.jobapplication.dto.ApplicationStats;
import com.alejandrojimenez.jobtracker.jobapplication.dto.JobApplicationRequest;
import com.alejandrojimenez.jobtracker.jobapplication.dto.JobApplicationResponse;
import com.alejandrojimenez.jobtracker.jobapplication.dto.StatusUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
@Tag(name = "Job applications", description = "Track the jobs you applied to")
public class JobApplicationController {

    private final JobApplicationService service;

    public JobApplicationController(JobApplicationService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List applications, newest first, optionally filtered by status or text")
    public List<JobApplicationResponse> findAll(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(name = "q", required = false) String search) {
        return service.findAll(status, search);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one application")
    public JobApplicationResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/stats")
    @Operation(summary = "Counts per status and response rate for the dashboard")
    public ApplicationStats stats() {
        return service.stats();
    }

    @PostMapping
    @Operation(summary = "Create an application")
    public ResponseEntity<JobApplicationResponse> create(@Valid @RequestBody JobApplicationRequest request) {
        JobApplicationResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace an application")
    public JobApplicationResponse update(@PathVariable Long id, @Valid @RequestBody JobApplicationRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Move an application to another status")
    public JobApplicationResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return service.updateStatus(id, request.status());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an application")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
