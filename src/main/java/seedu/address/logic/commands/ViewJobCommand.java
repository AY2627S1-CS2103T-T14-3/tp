package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.application.Job;
import seedu.address.model.application.JobDescriptionContainsKeywordPredicate;

/**
 * Shows jobs, optionally filtering by keyword or selecting a job by its displayed index.
 */
public class ViewJobCommand extends Command {

    public static final String COMMAND_WORD = "viewj";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows jobs in the job list.\n"
            + "Parameters: [INDEX] OR [-f KEYWORD] (INDEX must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " -f jan";

    public static final String MESSAGE_SUCCESS = "Listed all jobs.";
    public static final String MESSAGE_JOBS_LISTED = "%1$d job(s) listed!";
    public static final String MESSAGE_SELECTED = "Viewing job: %1$s";
    public static final String MESSAGE_EMPTY_LIST = "There are no jobs in the displayed list to view.";
    public static final String MESSAGE_INVALID_INDEX = "Invalid job index. Please enter an integer from 1 to %1$d.";

    private final String keyword;
    private final Index targetIndex;

    /**
     * Creates a ViewJobCommand that shows every job.
     */
    public ViewJobCommand() {
        this.keyword = null;
        this.targetIndex = null;
    }

    /**
     * Creates a ViewJobCommand that shows jobs whose description contains {@code keyword}.
     *
     * @param keyword text to match, ignoring case.
     */
    public ViewJobCommand(String keyword) {
        this.keyword = requireNonNull(keyword);
        this.targetIndex = null;
    }

    /** Creates a command to view the job at {@code targetIndex} in the current filtered list. */
    public ViewJobCommand(Index targetIndex) {
        this.keyword = null;
        this.targetIndex = requireNonNull(targetIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (targetIndex != null) {
            List<Job> jobs = model.getFilteredJobList();
            if (jobs.isEmpty()) {
                throw new CommandException(MESSAGE_EMPTY_LIST);
            }
            if (targetIndex.getZeroBased() >= jobs.size()) {
                throw new CommandException(String.format(MESSAGE_INVALID_INDEX, jobs.size()));
            }
            Job job = jobs.get(targetIndex.getZeroBased());
            return new CommandResult(String.format(MESSAGE_SELECTED, job.getJobRole()), ViewRequest.job(job));
        }
        if (keyword == null) {
            model.updateFilteredJobList(Model.PREDICATE_SHOW_ALL_JOBS);
            return new CommandResult(MESSAGE_SUCCESS, ViewRequest.jobs());
        }

        model.updateFilteredJobList(new JobDescriptionContainsKeywordPredicate(keyword));
        return new CommandResult(
                String.format(MESSAGE_JOBS_LISTED, model.getFilteredJobList().size()), ViewRequest.jobs());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ViewJobCommand otherCommand)) {
            return false;
        }
        return Objects.equals(keyword, otherCommand.keyword) && Objects.equals(targetIndex, otherCommand.targetIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyword, targetIndex == null ? null : targetIndex.getZeroBased());
    }
}
