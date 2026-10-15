package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.application.Application;
import seedu.address.model.person.Person;

/**
 * Updates the status of an existing application for an applicant shown in the current list.
 */
public class SetApplicationStatusCommand extends Command {

    public static final String COMMAND_WORD = "set";

    public static final String MESSAGE_SUCCESS = "Application status for %1$s (email %2$s) has been updated to %3$s.\n"
            + "Applicant's details have been displayed below.";

    public static final String MESSAGE_MISSING_APPLICATION =
            "Failed to update application status: This applicant does not have an application.";

    public static final String MESSAGE_INVALID_INDEX =
            "Invalid applicant index. Please enter an integer from 1 to %1$d.";

    public static final String MESSAGE_EMPTY_LIST =
            "There are no applicants in the displayed list to update an application for.";

    public static final String MESSAGE_INVALID_STATUS = "Invalid status. Please choose a status from "
            + "\"accept\", \"reject\", or \"pending\".\n"
            + "Please note that the input is case-sensitive.\n"
            + "Example: set -p 1 -s accept";

    public static final String MESSAGE_UNRECOGNIZED = "Unable to recognize the command %1$s.\n"
            + "Please follow the format: set -p <index> -s <status>.\n"
            + "Example: set -p 1 -s accept";

    private final Index targetIndex;
    private final Application.Status status;

    /**
     * Creates a SetApplicationStatusCommand to update the specified applicant's application status.
     * @param targetIndex of the applicant in the filtered person list
     * @param status status to record on the existing application
     */
    public SetApplicationStatusCommand(Index targetIndex, Application.Status status) {
        this.targetIndex = requireNonNull(targetIndex);
        this.status = requireNonNull(status);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (lastShownList.isEmpty()) {
            throw new CommandException(MESSAGE_EMPTY_LIST);
        }
        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(String.format(MESSAGE_INVALID_INDEX, lastShownList.size()));
        }

        Person applicant = lastShownList.get(targetIndex.getZeroBased());
        Application existing = model.getApplication(applicant)
                .orElseThrow(() -> new CommandException(MESSAGE_MISSING_APPLICATION));
        Application updated = new Application(
                existing.getApplicant(), existing.getJobDescription(), status);
        model.setApplication(existing, updated);
        return new CommandResult(String.format(
                MESSAGE_SUCCESS, applicant.getName(), applicant.getEmail(), status), applicant);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof SetApplicationStatusCommand otherCommand)) {
            return false;
        }

        return targetIndex.equals(otherCommand.targetIndex) && status == otherCommand.status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("status", status)
                .toString();
    }
}
