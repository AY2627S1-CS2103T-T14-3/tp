package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;

/** Represents the result of a command execution. */
public class CommandResult {
    private final String feedbackToUser;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    /** The view that should be displayed, together with an optional selection. */
    private final Optional<ViewRequest> viewRequest;

    /**
     * Constructs a {@code CommandResult} with the specified feedback, and other fields set to
     * their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false);
    }

    /** Constructs a {@code CommandResult} with the specified fields. */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit) {
        this(feedbackToUser, showHelp, exit, null);
    }

    /** Constructs a {@code CommandResult} that requests a view change. */
    public CommandResult(String feedbackToUser, ViewRequest viewRequest) {
        this(feedbackToUser, false, false, viewRequest);
    }

    private CommandResult(String feedbackToUser, boolean showHelp, boolean exit,
                          ViewRequest viewRequest) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.viewRequest = Optional.ofNullable(viewRequest);
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

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof CommandResult otherCommandResult)) {
            return false;
        }

        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && showHelp == otherCommandResult.showHelp
                && exit == otherCommandResult.exit
                && viewRequest.equals(otherCommandResult.viewRequest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, showHelp, exit, viewRequest);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("showHelp", showHelp)
                .add("exit", exit)
                .add("viewRequest", viewRequest)
                .toString();
    }
}
