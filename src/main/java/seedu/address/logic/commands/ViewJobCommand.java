package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.model.Model;
import seedu.address.model.application.JobDescriptionContainsKeywordPredicate;

/**
 * Shows jobs in the job list, optionally limited to roles whose description contains a keyword.
 */
public class ViewJobCommand extends Command {

    public static final String COMMAND_WORD = "viewj";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows jobs in the job list.\n"
            + "Parameters: [-f KEYWORD]\n"
            + "Example: " + COMMAND_WORD + " -f jan";

    public static final String MESSAGE_SUCCESS = "Listed all jobs.";
    public static final String MESSAGE_JOBS_LISTED = "%1$d job(s) listed!";

    private final String keyword;

    /**
     * Creates a ViewJobCommand that shows every job.
     */
    public ViewJobCommand() {
        this.keyword = null;
    }

    /**
     * Creates a ViewJobCommand that shows jobs whose description contains {@code keyword}.
     * @param keyword text to match, ignoring case
     */
    public ViewJobCommand(String keyword) {
        this.keyword = requireNonNull(keyword);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        if (keyword == null) {
            model.updateFilteredJobList(Model.PREDICATE_SHOW_ALL_JOBS);
            return CommandResult.showJobs(MESSAGE_SUCCESS);
        }

        model.updateFilteredJobList(new JobDescriptionContainsKeywordPredicate(keyword));
        return CommandResult.showJobs(String.format(MESSAGE_JOBS_LISTED, model.getFilteredJobList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ViewJobCommand otherCommand)) {
            return false;
        }
        return Objects.equals(keyword, otherCommand.keyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyword);
    }
}
