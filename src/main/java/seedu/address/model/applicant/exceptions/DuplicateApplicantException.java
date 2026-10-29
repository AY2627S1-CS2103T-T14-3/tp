package seedu.address.model.applicant.exceptions;

/**
 * Signals that the operation will result in applicants sharing a name or email.
 */
public class DuplicateApplicantException extends RuntimeException {
    public DuplicateApplicantException() {
        super("Operation would result in duplicate applicants");
    }
}
