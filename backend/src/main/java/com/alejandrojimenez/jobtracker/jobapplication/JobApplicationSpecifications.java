package com.alejandrojimenez.jobtracker.jobapplication;

import org.springframework.data.jpa.domain.Specification;

/**
 * Optional filters for the application list. Each one is skipped when its value is empty,
 * so the list endpoint can combine them freely.
 */
final class JobApplicationSpecifications {

    private JobApplicationSpecifications() {
    }

    static Specification<JobApplication> hasStatus(ApplicationStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    /** Case-insensitive match on company or position. */
    static Specification<JobApplication> matchesText(String text) {
        return (root, query, cb) -> {
            if (text == null || text.isBlank()) {
                return cb.conjunction();
            }
            String pattern = "%" + text.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("company")), pattern),
                    cb.like(cb.lower(root.get("position")), pattern));
        };
    }
}
