package seedu.address.logic.commands;

import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Adds a remark to the person identified using its displayed index.
 */
public class RemarkCommand extends Command {

    public static final String COMMAND_WORD = "remark";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds a remark to the person identified by the index number used in the displayed person list.\n"
            + "Parameters: INDEX (must be a positive integer) " + PREFIX_REMARK + "REMARK\n"
            + "Example: " + COMMAND_WORD + " 2 " + PREFIX_REMARK + "Likes baseball";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        throw new UnsupportedOperationException("RemarkCommand is not implemented yet");
    }
}
