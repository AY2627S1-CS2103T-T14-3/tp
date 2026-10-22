package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_APPLICANT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_JOB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;

import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddApplicationCommand;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;

/**
 * Parses input arguments and creates a new AddApplicationCommand object
 */
public class AddApplicationCommandParser implements Parser<AddApplicationCommand> {

    /**
     * Matches an add-application option. {@code -p} is shared with add-person, so routing
     * uses options that only an application command accepts.
     */
    private static final Pattern APPLICATION_OPTION =
            Pattern.compile("(?<!\\S)-(?:application|job|status|j|s)(?=\\s|$)");

    private static final Option APPLICANT_OPTION = Option.required(PREFIX_APPLICANT, "-p", "-application");
    private static final Option JOB_OPTION = Option.required(PREFIX_JOB, "-j", "-job");
    private static final Option STATUS_OPTION = Option.optional(PREFIX_STATUS, "-s", "-status");

    /**
     * Returns true if {@code args} contains an option used only by an add-application command.
     * Add-person commands also use hyphen options, including {@code -p} for phone, so a bare
     * hyphen is not enough to choose this parser.
     * @param args arguments string following the add command word
     * @return true if {@code args} should be parsed as an add-application command
     */
    static boolean isApplicationCommand(String args) {
        return APPLICATION_OPTION.matcher(args).find();
    }

    /**
     * Parses the given {@code String} of arguments in the context of the AddApplicationCommand
     * and returns an AddApplicationCommand object for execution.
     * @param args arguments string of the form {@code -p INDEX -j JOB [-s STATUS]}
     * @return an AddApplicationCommand object for execution
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddApplicationCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap values = parseOptions(args);

        Index index = parseIndex(args, values);
        String jobDescription = values.getValue(PREFIX_JOB).orElseThrow();
        if (!Job.isValidDescription(jobDescription)) {
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
            return ArgumentTokenizer.tokenizeOptions(args, APPLICANT_OPTION, JOB_OPTION, STATUS_OPTION);
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
     * Parses the application status, or {@code pending} when status is omitted.
     * @param values tokenized option values
     * @return application status
     * @throws ParseException if the status is not accept, reject, or pending
     */
    private Application.Status parseStatus(ArgumentMultimap values) throws ParseException {
        if (values.getValue(PREFIX_STATUS).isEmpty()) {
            return Application.Status.PENDING;
        }
        try {
            return Application.Status.fromValue(values.getValue(PREFIX_STATUS).get());
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
