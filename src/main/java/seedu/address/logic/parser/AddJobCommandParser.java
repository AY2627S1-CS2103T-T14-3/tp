package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_JOB;

import seedu.address.logic.commands.AddJobCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.application.Job;

/**
 * Parses input arguments and creates a new AddJobCommand object.
 */
public class AddJobCommandParser implements Parser<AddJobCommand> {

    private static final Option JOB_OPTION = Option.required(PREFIX_JOB, "-j", "-job");

    /**
     * Parses the given {@code String} of arguments in the context of the AddJobCommand
     * and returns an AddJobCommand object for execution.
     * @param args arguments following {@code addj}
     * @return an AddJobCommand object for execution
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public AddJobCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap values;
        try {
            values = ArgumentTokenizer.tokenizeOptions(args, JOB_OPTION);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddJobCommand.MESSAGE_USAGE), pe);
        }

        String description = values.getValue(PREFIX_JOB).orElseThrow();
        if (!Job.isValidDescription(description)) {
            throw new ParseException(Job.MESSAGE_CONSTRAINTS);
        }
        return new AddJobCommand(new Job(description));
    }
}
