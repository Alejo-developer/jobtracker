package com.alejandrojimenez.jobtracker.jobapplication.dto;

import com.alejandrojimenez.jobtracker.jobapplication.ApplicationStatus;

import java.util.EnumMap;
import java.util.Map;

/**
 * Summary for the dashboard.
 *
 * @param total        all applications, including the ones only saved
 * @param byStatus     count for every status, with zero for the empty ones
 * @param sent         applications actually sent (everything except SAVED)
 * @param responseRate share of sent applications that got any answer, from 0 to 1
 */
public record ApplicationStats(
        long total,
        Map<ApplicationStatus, Long> byStatus,
        long sent,
        double responseRate) {

    public static ApplicationStats fromCounts(Map<ApplicationStatus, Long> counts) {
        Map<ApplicationStatus, Long> byStatus = new EnumMap<>(ApplicationStatus.class);
        for (ApplicationStatus status : ApplicationStatus.values()) {
            byStatus.put(status, counts.getOrDefault(status, 0L));
        }

        long total = byStatus.values().stream().mapToLong(Long::longValue).sum();
        long sent = total - byStatus.get(ApplicationStatus.SAVED);
        long responses = byStatus.entrySet().stream()
                .filter(entry -> entry.getKey().isResponse())
                .mapToLong(Map.Entry::getValue)
                .sum();
        double responseRate = sent == 0 ? 0 : (double) responses / sent;

        return new ApplicationStats(total, byStatus, sent, responseRate);
    }
}
