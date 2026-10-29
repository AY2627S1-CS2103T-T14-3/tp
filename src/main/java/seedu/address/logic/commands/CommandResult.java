package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.application.Application;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    /** The view that should be displayed, together with an optional selection. */
    private final Optional<ViewRequest> viewRequest;

    /** A snapshot to display after an applicant has been deleted. */
    private final DeletedApplicant deletedApplicant;

    /** Constructs a {@code CommandResult} with the specified feedback. */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false, null, null);
    }

    /** Constructs a {@code CommandResult} with the specified fields. */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit) {
        this(feedbackToUser, showHelp, exit, null, null);
    }

    /** Constructs a {@code CommandResult} that requests a view change. */
    public CommandResult(String feedbackToUser, ViewRequest viewRequest) {
        this(feedbackToUser, false, false, viewRequest, null);
    }

    private CommandResult(String feedbackToUser, boolean showHelp, boolean exit,
            ViewRequest viewRequest, DeletedApplicant deletedApplicant) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.viewRequest = Optional.ofNullable(viewRequest);
        this.deletedApplicant = deletedApplicant;
    }

    /** Returns a result displaying the deleted applicant and their former application, if any. */
    public static CommandResult deletedApplicant(String feedback, Applicant applicant, Application application) {
        return new CommandResult(feedback, false, false, null,
                new DeletedApplicant(applicant, application));
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

    public Optional<ViewRequest> getViewRequest() {
        return viewRequest;
    }

    public Optional<DeletedApplicant> getDeletedApplicant() {
        return Optional.ofNullable(deletedApplicant);
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
                && viewRequest.equals(otherCommandResult.viewRequest)
                && Objects.equals(deletedApplicant, otherCommandResult.deletedApplicant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, showHelp, exit, viewRequest, deletedApplicant);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("showHelp", showHelp)
                .add("exit", exit)
                .add("viewRequest", viewRequest)
                .add("deletedApplicant", deletedApplicant)
                .toString();
    }

    /** Stores immutable details captured before deletion; the application may be null. */
    public record DeletedApplicant(Applicant applicant, Application application) {
        /** Creates a snapshot with a non-null applicant. */
        public DeletedApplicant {
            requireNonNull(applicant);
        }
    }
}
