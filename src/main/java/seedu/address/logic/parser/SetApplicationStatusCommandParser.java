package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_APPLICANT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.SetApplicationStatusCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.application.Application;

/**
 * Parses input arguments and creates a new SetApplicationStatusCommand object
 */
public class SetApplicationStatusCommandParser implements Parser<SetApplicationStatusCommand> {

    private static final Option APPLICANT_OPTION = Option.required(PREFIX_APPLICANT, "-p", "-application");
    private static final Option STATUS_OPTION = Option.required(PREFIX_STATUS, "-s", "-status");

    /**
     * Parses the given {@code String} of arguments in the context of the SetApplicationStatusCommand
     * and returns a SetApplicationStatusCommand object for execution.
     * @param args arguments string of the form {@code -p INDEX -s STATUS}
     * @return a SetApplicationStatusCommand object for execution
     * @throws ParseException if the user input does not conform to the expected format
     */
    public SetApplicationStatusCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap values = parseOptions(args);
        Index index = parseIndex(args, values);
        Application.Status status = parseStatus(values);
        return new SetApplicationStatusCommand(index, status);
    }

    /**
     * Tokenizes {@code args} as set-status options.
     * @param args arguments string for the set-status command
     * @return tokenized option values
     * @throws ParseException if the user input does not conform to the expected format
     */
    private ArgumentMultimap parseOptions(String args) throws ParseException {
        try {
            return ArgumentTokenizer.tokenizeOptions(args, APPLICANT_OPTION, STATUS_OPTION);
        } catch (ParseException pe) {
            throw unrecognized(args);
        }
    }

    /**
     * Parses the applicant index from the tokenized options.
     * @param args original arguments string, used in the error message
     * @param values tokenized option values
     * @return index of the applicant in the displayed list
     * @throws ParseException if the user input does not conform to the expected format
     */
    private Index parseIndex(String args, ArgumentMultimap values) throws ParseException {
        try {
            return ParserUtil.parseIndex(values.getValue(PREFIX_APPLICANT).orElseThrow());
        } catch (ParseException pe) {
            throw unrecognized(args);
        }
    }

    /**
     * Parses the required application status.
     * @param values tokenized option values
     * @return application status
     * @throws ParseException if the status is not accept, reject, or pending
     */
    private Application.Status parseStatus(ArgumentMultimap values) throws ParseException {
        try {
            return Application.Status.fromValue(values.getValue(PREFIX_STATUS).orElseThrow());
        } catch (IllegalArgumentException exception) {
            throw new ParseException(SetApplicationStatusCommand.MESSAGE_INVALID_STATUS);
        }
    }

    /**
     * Returns a parse exception for an unrecognized set-status command.
     * @param args original arguments string
     * @return parse exception describing the expected command format
     */
    private static ParseException unrecognized(String args) {
        String input = (SetApplicationStatusCommand.COMMAND_WORD + args).trim().replaceAll("\\s+", " ");
        return new ParseException(String.format(SetApplicationStatusCommand.MESSAGE_UNRECOGNIZED, input));
    }
}
