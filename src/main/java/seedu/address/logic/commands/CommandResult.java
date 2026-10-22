package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.application.Job;
import seedu.address.model.person.Person;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    /** The applicant to select in the UI, or null if the selection should remain unchanged. */
    private final Person personToView;

    /** The job list should be shown instead of the applicant list. */
    private final boolean showJobs;

    /** The job to select in the UI, or null if the selection should remain unchanged. */
    private final Job jobToView;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit) {
        this(feedbackToUser, showHelp, exit, null, false, null);
    }

    /**
     * Constructs a {@code CommandResult} with an optional applicant selection.
     */
    private CommandResult(String feedbackToUser, boolean showHelp, boolean exit, Person personToView,
            boolean showJobs, Job jobToView) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.personToView = personToView;
        this.showJobs = showJobs;
        this.jobToView = jobToView;
    }

    /**
     * Constructs a {@code CommandResult} that selects the specified applicant in the UI.
     */
    public CommandResult(String feedbackToUser, Person personToView) {
        this(feedbackToUser, false, false, requireNonNull(personToView), false, null);
    }

    /**
     * Constructs a {@code CommandResult} that shows the job list and selects {@code jobToView}.
     * @param feedbackToUser message to show the user
     * @param jobToView job to select in the job list
     */
    public CommandResult(String feedbackToUser, Job jobToView) {
        this(feedbackToUser, false, false, null, true, requireNonNull(jobToView));
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false);
    }

    /**
     * Returns a result that shows the job list without changing the selected job.
     * @param feedbackToUser message to show the user
     * @return a command result that shows the job list
     */
    public static CommandResult showJobs(String feedbackToUser) {
        return new CommandResult(feedbackToUser, false, false, null, true, null);
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public boolean isExit() {
        return exit;
    }

    public Optional<Person> getPersonToView() {
        return Optional.ofNullable(personToView);
    }

    /** Returns true if the job list should replace the applicant list. */
    public boolean isShowJobs() {
        return showJobs;
    }

    public Optional<Job> getJobToView() {
        return Optional.ofNullable(jobToView);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult otherCommandResult)) {
            return false;
        }

        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && showHelp == otherCommandResult.showHelp
                && exit == otherCommandResult.exit
                && Objects.equals(personToView, otherCommandResult.personToView)
                && showJobs == otherCommandResult.showJobs
                && Objects.equals(jobToView, otherCommandResult.jobToView);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, showHelp, exit, personToView, showJobs, jobToView);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("showHelp", showHelp)
                .add("exit", exit)
                .add("personToView", personToView)
                .add("showJobs", showJobs)
                .add("jobToView", jobToView)
                .toString();
    }

}
