package seedu.address.logic.commands;

/** Switches the sidebar to the Applications tab. */
public class ApplicationsCommand extends Command {
    public static final String COMMAND_WORD = "applications";
    public static final String MESSAGE_SUCCESS = "Showing applications.";

    @Override
    public CommandResult execute(seedu.address.model.Model model) {
        return new CommandResult(MESSAGE_SUCCESS, ViewRequest.applications());
    }
}
