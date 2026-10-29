package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;

/**
 * Adds one job application to an applicant shown in the current list.
 */
public class AddApplicationCommand extends Command {

    public static final String COMMAND_WORD = "addp";

    public static final String MESSAGE_SUCCESS = "New application for %1$s (email %2$s) has been created!\n"
            + "Applicant's details have been displayed below.";

    public static final String MESSAGE_DUPLICATE_APPLICATION =
            "Failed to add new application: This applicant already has an application.";

    public static final String MESSAGE_INVALID_INDEX =
            "Invalid applicant index. Please enter an integer from 1 to %1$d.";

    public static final String MESSAGE_INVALID_JOB =
            "Could not find specified job in the program.";

    public static final String MESSAGE_EMPTY_LIST =
            "There are no applicants in the displayed list to add an application to.";

    public static final String MESSAGE_INVALID_STATUS = "Invalid status. Please choose a status from "
            + "\"accept\", \"reject\", or \"pending\".\n"
            + "Please note that the input is case-sensitive.\n"
            + "Example: addp 1 -j Janitor -s accept";

    public static final String MESSAGE_UNRECOGNIZED = "Unable to recognize the command %1$s.\n"
            + "Please follow the format: addp <index> -j <job_desc> [-s <status>].\n"
            + "Example: addp 2 -j ClaudeGPT Expert -s accept";

    private final Index targetIndex;
    private final String jobRole;
    private final Application.Status status;

    /**
     * Creates an AddApplicationCommand to add an application to the specified applicant.
     * @param targetIndex of the applicant in the filtered person list to add an application to
     * @param jobRole job the applicant is applying for
     * @param status status of the application
     */
    public AddApplicationCommand(Index targetIndex, String jobRole, Application.Status status) {
        this.targetIndex = requireNonNull(targetIndex);
        this.jobRole = requireNonNull(jobRole);
        this.status = requireNonNull(status);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Applicant> lastShownList = model.getFilteredApplicantList();

        if (lastShownList.isEmpty()) {
            throw new CommandException(MESSAGE_EMPTY_LIST);
        }
        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(String.format(MESSAGE_INVALID_INDEX, lastShownList.size()));
        }

        Applicant applicant = lastShownList.get(targetIndex.getZeroBased());
        if (model.hasApplication(applicant)) {
            throw new CommandException(MESSAGE_DUPLICATE_APPLICATION);
        }

        Job job = model.getJobList().stream().filter(j -> j.getJobRole().equals(jobRole))
                .findFirst().orElseThrow(() -> new CommandException(MESSAGE_INVALID_JOB));

        Application application = new Application(applicant, job, status);
        model.addApplication(application);
        return new CommandResult(String.format(MESSAGE_SUCCESS, applicant.getName(), applicant.getEmail()),
                ViewRequest.applicant(applicant));
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
                && jobRole.equals(otherCommand.jobRole)
                && status == otherCommand.status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("jobRole", jobRole)
                .add("status", status)
                .toString();
    }
}
