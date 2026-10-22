package seedu.address.model.person.exceptions;

/**
 * Signals that the operation will result in persons sharing a name or email.
 */
public class DuplicatePersonException extends RuntimeException {
    public DuplicatePersonException() {
        super("Operation would result in duplicate persons");
    }
}
