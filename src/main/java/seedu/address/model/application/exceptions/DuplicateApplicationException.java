package seedu.address.model.application.exceptions;

/**
 * Signals that the operation will result in duplicate applications.
 * Applications are duplicates when they belong to the same applicant.
 */
public class DuplicateApplicationException extends RuntimeException {
    public DuplicateApplicationException() {
        super("Operation would result in duplicate applications");
    }
}
