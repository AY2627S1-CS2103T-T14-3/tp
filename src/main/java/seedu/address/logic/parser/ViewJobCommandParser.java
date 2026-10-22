package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.OPTION_FILTER;

import seedu.address.logic.commands.ViewJobCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ViewJobCommand object.
 */
public class ViewJobCommandParser implements Parser<ViewJobCommand> {

    /**
     * Parses the given arguments into a command that shows every job, or the jobs matching {@code -f}.
     * @param args arguments following {@code viewj}
     * @return a command that shows all jobs, or only jobs matching the filter
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public ViewJobCommand parse(String args) throws ParseException {
        requireNonNull(args);
        if (args.isBlank()) {
            return new ViewJobCommand();
        }

        ArgumentMultimap values;
        try {
            values = ArgumentTokenizer.tokenizeOptions(args, ArgumentTokenizer.NO_PREAMBLE, OPTION_FILTER);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewJobCommand.MESSAGE_USAGE), pe);
        }

        String keyword = values.getValue(OPTION_FILTER).orElse("");
        if (keyword.isBlank()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewJobCommand.MESSAGE_USAGE));
        }
        return new ViewJobCommand(keyword);
    }
}
