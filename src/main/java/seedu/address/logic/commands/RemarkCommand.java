package seedu.address.logic.commands;

import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Remark;

/**
 * Adds a remark to the person identified using its displayed index.
 */
public class RemarkCommand extends Command {

    public static final String COMMAND_WORD = "remark";

    public static final String MESSAGE_ARGUMENTS = "Index: %1$d, Remark: %2$s";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds a remark to the person identified by the index number used in the displayed person list."
            + "Existing remark will be overwritten by the input.\n"
            + "Parameters: INDEX (must be a positive integer) " + PREFIX_REMARK + "REMARK\n"
            + "Example: " + COMMAND_WORD + " 2 " + PREFIX_REMARK + "Likes baseball";

    public static final String MESSAGE_NOT_IMPLEMENTED_YET = "Remark command not implemented yet";

    private final Index index;
    private final Remark remark;

    public RemarkCommand(Index index, Remark remark) {
        this.index = index;
        this.remark = remark;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        throw new CommandException(MESSAGE_NOT_IMPLEMENTED_YET);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof RemarkCommand r) {
            return this.equals(r);
        }
        return false;
    }

    private boolean equals(RemarkCommand r) {
        boolean equalIndex = this.index.equals(r.index);
        boolean equalRemark = this.remark.equals(r.remark);

        return (equalIndex && equalRemark);
    }
}
