package com.alejandrojimenez.jobtracker.jobapplication.dto;

import com.alejandrojimenez.jobtracker.jobapplication.ApplicationStatus;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class ApplicationStatsTests {

    @Test
    void fillsMissingStatusesWithZero() {
        ApplicationStats stats = ApplicationStats.fromCounts(Map.of(ApplicationStatus.APPLIED, 3L));

        assertThat(stats.byStatus()).hasSize(ApplicationStatus.values().length);
        assertThat(stats.byStatus().get(ApplicationStatus.OFFER)).isZero();
        assertThat(stats.total()).isEqualTo(3);
    }

    @Test
    void responseRateIgnoresSavedApplications() {
        ApplicationStats stats = ApplicationStats.fromCounts(Map.of(
                ApplicationStatus.SAVED, 5L,
                ApplicationStatus.APPLIED, 2L,
                ApplicationStatus.INTERVIEW, 1L,
                ApplicationStatus.REJECTED, 1L));

        assertThat(stats.total()).isEqualTo(9);
        assertThat(stats.sent()).isEqualTo(4);
        // 2 answers (interview + rejection) out of 4 sent
        assertThat(stats.responseRate()).isCloseTo(0.5, within(1e-9));
    }

    @Test
    void responseRateIsZeroWhenNothingWasSent() {
        ApplicationStats stats = ApplicationStats.fromCounts(Map.of(ApplicationStatus.SAVED, 2L));

        assertThat(stats.sent()).isZero();
        assertThat(stats.responseRate()).isZero();
    }
}
