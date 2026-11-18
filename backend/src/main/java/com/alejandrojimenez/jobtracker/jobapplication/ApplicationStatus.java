package com.alejandrojimenez.jobtracker.jobapplication;

/**
 * Stages a job application goes through, in the order they usually happen.
 */
public enum ApplicationStatus {
    /** Saved to apply later. */
    SAVED,
    /** CV sent, waiting for an answer. */
    APPLIED,
    /** At least one interview scheduled or done. */
    INTERVIEW,
    /** Offer received. */
    OFFER,
    /** The company said no. */
    REJECTED;

    /** True when the company has answered in any way (interview, offer or rejection). */
    public boolean isResponse() {
        return this == INTERVIEW || this == OFFER || this == REJECTED;
    }
}
