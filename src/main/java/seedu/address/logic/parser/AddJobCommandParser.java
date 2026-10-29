package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.OPTION_JOB_OPT;

import seedu.address.logic.commands.AddJobCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.application.Job;

/**
 * Parses input arguments and creates a new AddJobCommand object.
 */
public class AddJobCommandParser implements Parser<AddJobCommand> {

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
            values = ArgumentTokenizer.tokenizeOptions(args,
                    ArgumentTokenizer.HAS_PREAMBLE, OPTION_JOB_OPT);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddJobCommand.MESSAGE_USAGE), pe);
        }

        String role = values.getPreamble().trim();
        if (!Job.isValidRole(role)) {
            throw new ParseException(Job.MESSAGE_CONSTRAINTS);
        }
        // Job description is given
        if (values.getValue(OPTION_JOB_OPT).isPresent()) {
            return new AddJobCommand(new Job(role, values.getValue(OPTION_JOB_OPT).get().trim()));
        }

        return new AddJobCommand(new Job(role));
    }
}
