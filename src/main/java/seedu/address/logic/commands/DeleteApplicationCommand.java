package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes an application using the applicant's displayed index, keeping the applicant and job.
 */
public class DeleteApplicationCommand extends Command {

    public static final String COMMAND_WORD = "deletep";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the application of the applicant identified by the displayed applicant index.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_SUCCESS = "Deleted application for %1$s (email %2$s).\n"
            + "Applicant's details have been displayed below.";
    public static final String MESSAGE_MISSING_APPLICATION =
            "Failed to delete application: This applicant does not have an application.";
    public static final String MESSAGE_EMPTY_LIST =
            "There are no applicants in the displayed list to delete an application from.";
    public static final String MESSAGE_INVALID_INDEX =
            "Invalid applicant index. Please enter an integer from 1 to %1$d.";

    private final Index targetIndex;

    /**
     * Creates a command to delete the application of the applicant at {@code targetIndex}.
     */
    public DeleteApplicationCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
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
        if (!model.hasApplication(applicant)) {
            throw new CommandException(MESSAGE_MISSING_APPLICATION);
        }

        model.deleteApplication(applicant);
        return new CommandResult(String.format(MESSAGE_SUCCESS, applicant.getName(), applicant.getEmail()),
                ViewRequest.applicant(applicant));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteApplicationCommand otherCommand)) {
            return false;
        }
        return targetIndex.equals(otherCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
