package seedu.address.model.application;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;

/**
 * Represents an application submitted by an applicant for a job.
 *
 * <p>An application owns its relationship with a {@link Person}; a {@code Person} does not contain application
 * data. Application values are immutable after construction.</p>
 */
public class Application {

    public static final String MESSAGE_JOB_DESCRIPTION_CONSTRAINTS =
            "Job descriptions should not be blank or contain option-like parameters.";

    private static final String OPTION_LIKE_TOKEN_REGEX = ".* -[a-z] .*";

    private final Person applicant;
    private final String jobDescription;
    private final Status status;

    /**
     * Creates an application.
     *
     * @param applicant the person submitting the application
     * @param jobDescription the job the applicant is applying for
     * @param status the application's status
     */
    public Application(Person applicant, String jobDescription, Status status) {
        requireNonNull(applicant);
        requireNonNull(jobDescription);
        requireNonNull(status);
        checkArgument(isValidJobDescription(jobDescription), MESSAGE_JOB_DESCRIPTION_CONSTRAINTS);

        this.applicant = applicant;
        this.jobDescription = jobDescription.trim();
        this.status = status;
    }

    /** Returns true if the job description is valid. */
    public static boolean isValidJobDescription(String jobDescription) {
        return jobDescription != null
                && !jobDescription.trim().isEmpty()
                && !jobDescription.matches(OPTION_LIKE_TOKEN_REGEX);
    }

    public Person getApplicant() {
        return applicant;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public Status getStatus() {
        return status;
    }

    /**
     * Returns true if both applications belong to the same applicant.
     * This defines application identity because an applicant may have at most one application.
     */
    public boolean isForSameApplicant(Application otherApplication) {
        if (otherApplication == this) {
            return true;
        }

        return otherApplication != null
                && otherApplication.getApplicant().isSamePerson(getApplicant());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Application otherApplication)) {
            return false;
        }
        return applicant.equals(otherApplication.applicant)
                && jobDescription.equals(otherApplication.jobDescription)
                && status == otherApplication.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(applicant, jobDescription, status);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("applicant", applicant)
                .add("jobDescription", jobDescription)
                .add("status", status)
                .toString();
    }

    /** The allowed application states. Enum names intentionally match the case-sensitive command values. */
    public enum Status {
        ACCEPT("accept"),
        REJECT("reject"),
        PENDING("pending");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        /**
         * Converts a command or storage value to a status without accepting different casing.
         *
         * @throws IllegalArgumentException if {@code value} is not an allowed status
         */
        public static Status fromValue(String value) {
            requireNonNull(value);
            for (Status status : values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid application status: " + value);
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
