package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.OPTION_STATUS_REQ;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.SetApplicationStatusCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.application.Application;

/**
 * Parses input arguments and creates a new SetApplicationStatusCommand object
 */
public class SetApplicationStatusCommandParser implements Parser<SetApplicationStatusCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the SetApplicationStatusCommand
     * and returns a SetApplicationStatusCommand object for execution.
     * @param args arguments string of the form {@code INDEX -s STATUS}
     * @return a SetApplicationStatusCommand object for execution
     * @throws ParseException if the user input does not conform to the expected format
     */
    public SetApplicationStatusCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String[] tokens = args.trim().split("\\s+", 2);
        Index index = parseIndex(args, tokens[0]);
        String optionArgs = tokens.length == 2 ? tokens[1] : "";
        Application.Status status = parseStatus(args, optionArgs);
        return new SetApplicationStatusCommand(index, status);
    }

    /**
     * Parses the applicant index from the leading positional token.
     * The index is not prefixed, matching {@code delete} and {@code view}.
     * @param args original arguments string, used in the error message
     * @param indexToken first token of the arguments
     * @return index of the applicant in the displayed list
     * @throws ParseException if the user input does not conform to the expected format
     */
    private Index parseIndex(String args, String indexToken) throws ParseException {
        try {
            return ParserUtil.parseIndex(indexToken);
        } catch (ParseException pe) {
            throw unrecognized(args);
        }
    }

    /**
     * Parses the required application status from the option portion of the command.
     * @param args original arguments string, used in the error message
     * @param optionArgs arguments after the index
     * @return application status
     * @throws ParseException if the status is missing, malformed, or not accept, reject, or pending
     */
    private Application.Status parseStatus(String args, String optionArgs) throws ParseException {
        ArgumentMultimap values = parseOptions(args, optionArgs);
        try {
            return Application.Status.fromValue(values.getValue(OPTION_STATUS_REQ).orElseThrow());
        } catch (IllegalArgumentException exception) {
            throw new ParseException(SetApplicationStatusCommand.MESSAGE_INVALID_STATUS);
        }
    }

    /**
     * Tokenizes {@code optionArgs} as the status option.
     * @param args original arguments string, used in the error message
     * @param optionArgs arguments after the index
     * @return tokenized option values
     * @throws ParseException if the user input does not conform to the expected format
     */
    private ArgumentMultimap parseOptions(String args, String optionArgs) throws ParseException {
        try {
            return ArgumentTokenizer.tokenizeOptions(optionArgs, ArgumentTokenizer.NO_PREAMBLE, OPTION_STATUS_REQ);
        } catch (ParseException pe) {
            throw unrecognized(args);
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
