package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ViewApplicationCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ViewApplicationCommand object.
 */
public class ViewApplicationCommandParser implements Parser<ViewApplicationCommand> {

    /**
     * Parses the given arguments into a command to show applications or select one by applicant index.
     *
     * @throws ParseException if nonblank arguments are not a single positive integer.
     */
    @Override
    public ViewApplicationCommand parse(String args) throws ParseException {
        if (args.isBlank()) {
            return new ViewApplicationCommand();
        }
        try {
            Index index = ParserUtil.parseIndex(args);
            return new ViewApplicationCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewApplicationCommand.MESSAGE_USAGE), pe);
        }
    }
}
