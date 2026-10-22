package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;
import seedu.address.model.person.Person;

/**
 * Adds one job application to an applicant shown in the current list.
 */
public class AddApplicationCommand extends Command {

    public static final String MESSAGE_SUCCESS = "New application for %1$s (email %2$s) has been created!\n"
            + "Applicant's details have been displayed below.";

    public static final String MESSAGE_DUPLICATE_APPLICATION =
            "Failed to add new application: This applicant already has an application.";

    public static final String MESSAGE_INVALID_INDEX =
            "Invalid applicant index. Please enter an integer from 1 to %1$d.";

    public static final String MESSAGE_EMPTY_LIST =
            "There are no applicants in the displayed list to add an application to.";

    public static final String MESSAGE_INVALID_STATUS = "Invalid status. Please choose a status from "
            + "\"accept\", \"reject\", or \"pending\".\n"
            + "Please note that the input is case-sensitive.\n"
            + "Example: add -p 1 -j Janitor -s accept";

    public static final String MESSAGE_UNRECOGNIZED = "Unable to recognize the command %1$s.\n"
            + "Please follow the format: add -p <index> -j <job_desc> [-s <status>].\n"
            + "Example: add -p 2 -j ClaudeGPT Expert -s accept";

    private final Index targetIndex;
    private final String jobDescription;
    private final Application.Status status;

    /**
     * Creates an AddApplicationCommand to add an application to the specified applicant.
     * @param targetIndex of the applicant in the filtered person list to add an application to
     * @param jobDescription job the applicant is applying for
     * @param status status of the application
     */
    public AddApplicationCommand(Index targetIndex, String jobDescription, Application.Status status) {
        this.targetIndex = requireNonNull(targetIndex);
        this.jobDescription = requireNonNull(jobDescription);
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
        if (model.hasApplication(applicant)) {
            throw new CommandException(MESSAGE_DUPLICATE_APPLICATION);
        }

        Application application = new Application(applicant, new Job(jobDescription), status);
        model.addApplication(application);
        return new CommandResult(String.format(MESSAGE_SUCCESS, applicant.getName(), applicant.getEmail()), applicant);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddApplicationCommand otherCommand)) {
            return false;
        }

        return targetIndex.equals(otherCommand.targetIndex)
                && jobDescription.equals(otherCommand.jobDescription)
                && status == otherCommand.status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("jobDescription", jobDescription)
                .add("status", status)
                .toString();
    }
}
