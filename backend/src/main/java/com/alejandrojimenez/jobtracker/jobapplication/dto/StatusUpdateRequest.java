package com.alejandrojimenez.jobtracker.jobapplication.dto;

import com.alejandrojimenez.jobtracker.jobapplication.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

/** Body for moving an application to another column of the board. */
public record StatusUpdateRequest(
        @NotNull(message = "El estado es obligatorio")
        ApplicationStatus status) {
}
