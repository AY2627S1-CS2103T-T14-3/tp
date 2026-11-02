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

    /**
     * Restores applicants, jobs, and applications to the state before the last change.
     *
     * @param model Model that stores the remembered states. Cannot be null.
     * @return A result whose message confirms the undo.
     * @throws CommandException If there is no change to undo.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (!model.undoAddressBook()) {
            throw new CommandException(MESSAGE_FAILURE);
        }
        return new CommandResult(MESSAGE_SUCCESS);
    }

    /**
     * Returns true if {@code other} is also an undo command.
     * Every undo command is equal because the command takes no arguments.
     *
     * @param other Object to compare against this command.
     * @return True if {@code other} is an {@code UndoCommand}.
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof UndoCommand;
    }

    /**
     * Returns the hash code shared by every undo command.
     *
     * @return Hash code of the command word.
     */
    @Override
    public int hashCode() {
        return COMMAND_WORD.hashCode();
    }
}
