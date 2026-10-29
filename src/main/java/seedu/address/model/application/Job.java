package seedu.address.model.application;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

/** Represents a job that an applicant is applying for. */
public class Job {

    private static final String DEFAULT_DESC =
            "No information ~";

    public final String role;
    public final String description;

    public Job(String role) {
        this(role, DEFAULT_DESC);
    }

    /** Creates a job with the given description. */
    public Job(String role, String description) {
        requireNonNull(role);
        if (!isValidRole(role) || !isValidDescription(description)) {
            throw new IllegalArgumentException();
        }

        this.role = role.strip();
        this.description = description.strip();
    }

    /** Returns true if the job role is valid. */
    public static boolean isValidRole(String role) {
        return role != null
                && !role.isBlank();
    }

    /** Returns true if the job role is valid. */
    public static boolean isValidDescription(String description) {
        return description != null;
    }

    /**
     * Returns the job role.
     *
     * @return Job Description for the job.
     */
    public String getJobRole() {
        return this.role;
    }

    /**
     * Returns the job description for this job role.
     *
     * @return Job Description for the job.
     */
    public String getJobDescription() {
        return this.description;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof Job otherJob
                && role.equals(otherJob.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role);
    }

    @Override
    public String toString() {
        return role + " : " + description;
    }
}
