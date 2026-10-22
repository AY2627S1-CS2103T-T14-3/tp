package seedu.address.model.application.exceptions;

/**
 * Signals that the operation will result in duplicate jobs.
 * Jobs are duplicates when their descriptions are the same.
 */
public class DuplicateJobException extends RuntimeException {
    public DuplicateJobException() {
        super("Operation would result in duplicate jobs");
    }
}
