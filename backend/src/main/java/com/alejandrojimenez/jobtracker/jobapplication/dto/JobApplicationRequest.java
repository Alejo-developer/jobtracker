package com.alejandrojimenez.jobtracker.jobapplication.dto;

import com.alejandrojimenez.jobtracker.jobapplication.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

/**
 * Body for creating or replacing an application. Messages are in Spanish because the
 * frontend shows them to the user as they are.
 */
public record JobApplicationRequest(
        @NotBlank(message = "La empresa es obligatoria")
        @Size(max = 120, message = "La empresa no puede tener más de 120 caracteres")
        String company,

        @NotBlank(message = "El cargo es obligatorio")
        @Size(max = 120, message = "El cargo no puede tener más de 120 caracteres")
        String position,

        @URL(message = "El enlace de la oferta no es una URL válida")
        @Size(max = 500, message = "El enlace no puede tener más de 500 caracteres")
        String jobUrl,

        @Size(max = 120, message = "La ubicación no puede tener más de 120 caracteres")
        String location,

        @Size(max = 60, message = "La fuente no puede tener más de 60 caracteres")
        String source,

        /** Optional: new applications start as SAVED when it is missing. */
        ApplicationStatus status,

        @PastOrPresent(message = "La fecha de postulación no puede estar en el futuro")
        LocalDate appliedOn,

        @Size(max = 2000, message = "Las notas no pueden tener más de 2000 caracteres")
        String notes) {
}
