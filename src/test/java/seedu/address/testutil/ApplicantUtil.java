package seedu.address.testutil;

import seedu.address.logic.commands.AddCommand;
import seedu.address.model.applicant.Applicant;

/**
 * Provides helper methods for constructing applicant commands.
 */
public class ApplicantUtil {

    /**
     * Returns an add command string for adding the {@code applicant}.
     */
    public static String getAddCommand(Applicant applicant) {
        return AddCommand.COMMAND_WORD + " " + getApplicantDetails(applicant);
    }

    /**
     * Returns add-command options for the given {@code applicant}'s details.
     */
    public static String getApplicantDetails(Applicant applicant) {
        StringBuilder sb = new StringBuilder();
        sb.append("-n ").append(applicant.getName().fullName).append(" ");
        sb.append("-p ").append(applicant.getPhone().value).append(" ");
        sb.append("-e ").append(applicant.getEmail().value).append(" ");
        return sb.toString();
    }

}
