package seedu.address.logic.commands;

import seedu.address.model.Model;

/**
 * Terminates LinkedOut without displaying a message.
 */
public class ByeCommand extends Command {

    public static final String COMMAND_WORD = "bye";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Exits LinkedOut.\n"
            + "Usage: " + COMMAND_WORD;

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult("", false, true);
    }
}
