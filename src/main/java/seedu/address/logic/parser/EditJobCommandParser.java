package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_JOB_ROLE;
import static seedu.address.logic.parser.CliSyntax.OPTION_JOB_ROLE_REQ;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditJobCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.application.Job;

/**
 * Parses a displayed job index and a replacement role into an EditJobCommand.
 */
public class EditJobCommandParser implements Parser<EditJobCommand> {

    /**
     * Parses a leading job index followed by a required job role.
     *
     * @throws ParseException if the arguments do not conform to the expected format.
     */
    @Override
    public EditJobCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap values;
        Index index;
        try {
            values = ArgumentTokenizer.tokenizeOptions(args, ArgumentTokenizer.HAS_PREAMBLE, OPTION_JOB_ROLE_REQ);
            index = ParserUtil.parseIndex(values.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditJobCommand.MESSAGE_USAGE), pe);
        }

        String role = values.getValue(OPTION_JOB_ROLE_REQ).orElseThrow();
        if (!Job.isValidRole(role)) {
            throw new ParseException(MESSAGE_INVALID_JOB_ROLE);
        }
        return new EditJobCommand(index, new Job(role));
    }
}
