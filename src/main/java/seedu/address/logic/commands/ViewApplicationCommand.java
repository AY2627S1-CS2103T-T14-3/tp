package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.application.Application;

/** Shows applications, optionally selecting one using its displayed applicant index. */
public class ViewApplicationCommand extends Command {
    public static final String COMMAND_WORD = "viewp";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows applications, optionally selecting one.\n"
            + "Parameters: [INDEX] (displayed applicant index, must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";
    public static final String MESSAGE_SUCCESS = "Showing applications.";
    public static final String MESSAGE_SELECTED = "Viewing application for %1$s.";
    public static final String MESSAGE_MISSING_APPLICATION = "This applicant does not have an application to view.";

    private final Index targetIndex;

    /** Creates a command to show applications without changing the selection. */
    public ViewApplicationCommand() {
        targetIndex = null;
    }

    /** Creates a command to view the application of the applicant at {@code targetIndex}. */
    public ViewApplicationCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (targetIndex == null) {
            return new CommandResult(MESSAGE_SUCCESS, ViewRequest.applications());
        }
        List<Applicant> applicants = model.getFilteredApplicantList();
        if (applicants.isEmpty()) {
            throw new CommandException(ViewCommand.MESSAGE_EMPTY_LIST);
        }
        if (targetIndex.getZeroBased() >= applicants.size()) {
            throw new CommandException(String.format(ViewCommand.MESSAGE_INVALID_INDEX, applicants.size()));
        }
        Applicant applicant = applicants.get(targetIndex.getZeroBased());
        Application application = model.getApplicationList().stream()
                .filter(existing -> existing.isForApplicant(applicant))
                .findFirst().orElseThrow(() -> new CommandException(MESSAGE_MISSING_APPLICATION));
        return new CommandResult(String.format(MESSAGE_SELECTED, applicant.getName()),
                ViewRequest.application(application));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ViewApplicationCommand otherCommand
                && Objects.equals(targetIndex, otherCommand.targetIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetIndex == null ? null : targetIndex.getZeroBased());
    }
}
