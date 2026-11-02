package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Restores the address book to the state before the most recent change.
 */
public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Restores applicants, jobs, and applications to the state before the last change.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Undone the last change.";
    public static final String MESSAGE_FAILURE = "There is no change to undo.";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (!model.undoAddressBook()) {
            throw new CommandException(MESSAGE_FAILURE);
        }
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof UndoCommand;
    }

    @Override
    public int hashCode() {
        return COMMAND_WORD.hashCode();
    }
}
