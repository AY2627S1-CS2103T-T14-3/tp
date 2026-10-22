package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.OPTION_STATUS;
import static seedu.address.logic.parser.CliSyntax.OPTION_JOB;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddApplicationCommand;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.application.Application;

/**
 * Parses input arguments and creates a new AddApplicationCommand object
 */
public class AddApplicationCommandParser implements Parser<AddApplicationCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddApplicationCommand
     * and returns an AddApplicationCommand object for execution.
     * @param args arguments string of the form {@code -p INDEX -j JOB [-s STATUS]}
     * @return an AddApplicationCommand object for execution
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public AddApplicationCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap values = parseOptions(args);

        Index index = parseIndex(args, values);
        String jobDescription = values.getValue(OPTION_JOB).orElseThrow();
        if (!Application.isValidJobDescription(jobDescription)) {
            throw unrecognized(args);
        }

        Application.Status status = parseStatus(values);
        return new AddApplicationCommand(index, jobDescription, status);
    }

    /**
     * Tokenizes {@code args} as add-application options.
     * @param args arguments string for the add-application command
     * @return tokenized option values
     * @throws ParseException if the user input does not conform to the expected format
     */
    private ArgumentMultimap parseOptions(String args) throws ParseException {
        try {
            return ArgumentTokenizer.tokenizeOptions(args, OPTION_JOB, OPTION_STATUS);
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
            return ParserUtil.parseIndex(values.getPreamble());
        } catch (ParseException pe) {
            throw unrecognized(args);
        }
    }

    /**
     * Parses the application status, or {@code pending} when status is omitted.
     * @param values tokenized option values
     * @return application status
     * @throws ParseException if the status is not accept, reject, or pending
     */
    private Application.Status parseStatus(ArgumentMultimap values) throws ParseException {
        if (values.getValue(OPTION_STATUS).isEmpty()) {
            return Application.Status.PENDING;
        }
        try {
            return Application.Status.fromValue(values.getValue(OPTION_STATUS).get());
        } catch (IllegalArgumentException exception) {
            throw new ParseException(AddApplicationCommand.MESSAGE_INVALID_STATUS);
        }
    }

    /**
     * Returns a parse exception for an unrecognized add-application command.
     * @param args original arguments string
     * @return parse exception describing the expected command format
     */
    private static ParseException unrecognized(String args) {
        String input = (AddCommand.COMMAND_WORD + args).trim().replaceAll("\\s+", " ");
        return new ParseException(String.format(AddApplicationCommand.MESSAGE_UNRECOGNIZED, input));
    }
}
