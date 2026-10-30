package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.application.Job;

/**
 * Adds a job to the job list.
 */
public class AddJobCommand extends Command {

    public static final String COMMAND_WORD = "addj";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a job to the job list.\n"
            + "Parameters: -j JOB_DESCRIPTION\n"
            + "Example: " + COMMAND_WORD + " -j Software Engineer Intern";

    public static final String MESSAGE_SUCCESS = "New job added: %1$s";
    public static final String MESSAGE_DUPLICATE_JOB = "This job already exists in the job list.";

    private final Job job;

    /**
     * Creates an AddJobCommand to add the specified {@code Job}.
     * @param job job to add
     */
    public AddJobCommand(Job job) {
        requireNonNull(job);
        this.job = job;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (model.hasJob(job)) {
            throw new CommandException(MESSAGE_DUPLICATE_JOB);
        }

        model.addJob(job);
        return new CommandResult(String.format(MESSAGE_SUCCESS, job.getJobRole()), ViewRequest.job(job));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AddJobCommand otherCommand)) {
            return false;
        }
        return job.equals(otherCommand.job);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("job", job)
                .toString();
    }
}
