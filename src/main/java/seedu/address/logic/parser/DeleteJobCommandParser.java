package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteJobCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeleteJobCommand object.
 */
public class DeleteJobCommandParser implements Parser<DeleteJobCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteJobCommand
     * and returns a DeleteJobCommand object for execution.
     * @param args arguments following {@code deletej}
     * @return a DeleteJobCommand object for execution
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public DeleteJobCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new DeleteJobCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteJobCommand.MESSAGE_USAGE), pe);
        }
    }
}
