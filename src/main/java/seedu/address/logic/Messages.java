package seedu.address.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Option;
import seedu.address.model.applicant.Applicant;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.\n"
            + "Available commands: add, addj, addp, bye, clear, delete, deletej, deletep, edit, exit, find, help, "
            + "list, set, view, viewa, viewj, viewp.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_APPLICANT_DISPLAYED_INDEX = "The applicant index provided is invalid.";
    public static final String MESSAGE_APPLICANTS_LISTED_OVERVIEW = "%1$d applicant(s) listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";

    public static final String MESSAGE_INVALID_JOB_ROLE =
            "Job roles should not be blank or contain option-like parameters.";
    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicateOptions(Option... duplicateOptions) {
        assert duplicateOptions.length > 0 : "There should be at least one duplicate options";

        Set<String> duplicateFields =
                Stream.of(duplicateOptions).map(Option::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code applicant} for display to the user.
     */
    public static String format(Applicant applicant) {
        final StringBuilder builder = new StringBuilder();
        builder.append(applicant.getName())
                .append("; Phone: ")
                .append(applicant.getPhone())
                .append("; Email: ")
                .append(applicant.getEmail());
        return builder.toString();
    }

}
