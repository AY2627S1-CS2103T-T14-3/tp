package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.ByeCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ByeCommand object.
 */
public class ByeCommandParser implements Parser<ByeCommand> {

    /**
     * Parses the given arguments into a command to exit LinkedOut.
     *
     * @throws ParseException if any arguments are provided.
     */
    @Override
    public ByeCommand parse(String args) throws ParseException {
        if (!args.isBlank()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ByeCommand.MESSAGE_USAGE));
        }
        return new ByeCommand();
    }
}
