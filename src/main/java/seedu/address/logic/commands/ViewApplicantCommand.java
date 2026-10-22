package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/** Shows the applicants view. */
public class ViewApplicantCommand extends Command {
    public static final String COMMAND_WORD = "viewa";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows applicants in the applicant list.";
    public static final String MESSAGE_SUCCESS = "Showing applicants.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        return new CommandResult(MESSAGE_SUCCESS, ViewRequest.applicants());
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ViewApplicantCommand;
    }

    @Override
    public int hashCode() {
        return ViewApplicantCommand.class.hashCode();
    }
}
