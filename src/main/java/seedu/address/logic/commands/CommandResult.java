package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {
    /**
     * Represents the tabs of the sidebar.
     */
    public enum SidebarView { APPLICANTS, APPLICATIONS }

    private final String feedbackToUser;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    /** The applicant to select in the UI, or null if the selection should remain unchanged. */
    private final Person personToView;
    private final SidebarView sidebarView;

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false);
    }

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit) {
        this(feedbackToUser, showHelp, exit, null);
    }

    /**
     * Constructs a {@code CommandResult} with an optional applicant selection.
     */
    private CommandResult(String feedbackToUser, boolean showHelp, boolean exit, Person personToView) {
        this(feedbackToUser, showHelp, exit, personToView, null);
    }

    private CommandResult(String feedbackToUser, boolean showHelp, boolean exit,
                          Person personToView, SidebarView sidebarView) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.personToView = personToView;
        this.sidebarView = sidebarView;
    }

    /**
     * Constructs a {@code CommandResult} that selects the specified applicant in the UI.
     */
    public CommandResult(String feedbackToUser, Person personToView) {
        this(feedbackToUser, false, false, requireNonNull(personToView));
    }

    public CommandResult(String feedbackToUser, SidebarView sidebarView) {
        this(feedbackToUser, false, false, null, requireNonNull(sidebarView));
    }

    public Optional<SidebarView> getSidebarView() {
        return Optional.ofNullable(sidebarView);
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
                && sidebarView == otherCommandResult.sidebarView;
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, showHelp, exit, personToView, sidebarView);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("showHelp", showHelp)
                .add("exit", exit)
                .add("personToView", personToView)
                .toString();
    }

}
