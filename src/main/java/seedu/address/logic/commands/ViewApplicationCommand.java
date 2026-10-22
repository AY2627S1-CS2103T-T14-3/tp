package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/** Shows the applications view. */
public class ViewApplicationCommand extends Command {
    public static final String COMMAND_WORD = "viewp";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows applications in the applications list.";
    public static final String MESSAGE_SUCCESS = "Showing applications.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        return new CommandResult(MESSAGE_SUCCESS, ViewRequest.applications());
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ViewApplicationCommand;
    }

    @Override
    public int hashCode() {
        return ViewApplicationCommand.class.hashCode();
    }
}
