package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_JOB_ROLE;
import static seedu.address.logic.parser.CliSyntax.OPTION_JOB_DESC_OPT;
import static seedu.address.logic.parser.CliSyntax.OPTION_JOB_ROLE_REQ;

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
                    ArgumentTokenizer.NO_PREAMBLE, OPTION_JOB_ROLE_REQ, OPTION_JOB_DESC_OPT);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddJobCommand.MESSAGE_USAGE), pe);
        }

        String role = values.getValue(OPTION_JOB_ROLE_REQ).get().trim();
        if (!Job.isValidRole(role)) {
            throw new ParseException(MESSAGE_INVALID_JOB_ROLE);
        }

        // Job description is given
        if (values.getValue(OPTION_JOB_DESC_OPT).isPresent()) {
            return new AddJobCommand(new Job(role, values.getValue(OPTION_JOB_DESC_OPT).get().trim()));
        }

        return new AddJobCommand(new Job(role));
    }
}
