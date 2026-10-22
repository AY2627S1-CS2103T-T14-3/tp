package seedu.address.model.application;

import static java.util.Objects.requireNonNull;

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

    private final Person applicant;
    private final Job job;
    private final Status status;

    /**
     * Creates an application.
     *
     * @param applicant the person submitting the application.
     * @param job the job the applicant is applying for.
     * @param status the application's status.
     */
    public Application(Person applicant, Job job, Status status) {
        requireNonNull(applicant);
        requireNonNull(job);
        requireNonNull(status);

        this.applicant = applicant;
        this.job = job;
        this.status = status;
    }

    public Person getApplicant() {
        return applicant;
    }

    /** Returns the applicant's display name. */
    public String getApplicantName() {
        return applicant.getDisplayName();
    }

    /** Returns whether this application belongs to the supplied applicant. */
    public boolean isForApplicant(Person person) {
        return applicant.isSamePerson(person);
    }

    /** Returns the display value of this application's status. */
    public String getStatusValue() {
        return status.getValue();
    }

    /**
     * Returns the status of the application.
     */
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
                && job.equals(otherApplication.job)
                && status == otherApplication.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(applicant, job, status);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("applicant", applicant)
                .add("job", job)
                .add("status", status)
                .toString();
    }

    /**
     * The allowed application states.
     * Enum constants use uppercase Java naming, while their backing command values are lowercase and case-sensitive.
     */
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
         * For example, {@code fromValue("accept")} succeeds, while {@code fromValue("ACCEPT")} throws.
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
