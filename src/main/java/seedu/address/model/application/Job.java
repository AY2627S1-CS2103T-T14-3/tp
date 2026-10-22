package seedu.address.model.application;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Objects;
import java.util.regex.Pattern;

/** Represents a job that an applicant is applying for. */
public class Job {

    public static final String MESSAGE_CONSTRAINTS =
            "Job descriptions should not be blank or contain option-like parameters.";

    /** Matches an option-like token using the same whitespace boundaries as {@code ArgumentTokenizer}. */
    private static final Pattern OPTION_LIKE_TOKEN_PATTERN =
            Pattern.compile("(?<!\\S)-[a-z]+(?:-[a-z]+)*(?=\\s|$)");

    public final String value;

    /** Creates a job with the given description. */
    public Job(String description) {
        requireNonNull(description);
        checkArgument(isValidDescription(description), MESSAGE_CONSTRAINTS);
        this.value = description.strip();
    }

    /** Returns true if the job description is valid. */
    public static boolean isValidDescription(String description) {
        return description != null
                && !description.isBlank()
                && !OPTION_LIKE_TOKEN_PATTERN.matcher(description).find();
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof Job otherJob
                && value.equals(otherJob.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
