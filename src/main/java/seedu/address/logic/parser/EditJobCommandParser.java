package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.OPTION_JOB_DESC_OPT;
import static seedu.address.logic.parser.CliSyntax.OPTION_JOB_ROLE_OPT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditJobCommand;
import seedu.address.logic.commands.EditJobCommand.EditJobDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses a displayed job index and role or description edits into an EditJobCommand.
 */
public class EditJobCommandParser implements Parser<EditJobCommand> {

    /**
     * Parses a leading job index followed by at least one role or description edit.
     *
     * @throws ParseException if the arguments do not conform to the expected format.
     */
    @Override
    public EditJobCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap values;
        Index index;
        try {
            values = ArgumentTokenizer.tokenizeOptions(args, ArgumentTokenizer.HAS_PREAMBLE, OPTION_JOB_ROLE_OPT,
                    OPTION_JOB_DESC_OPT);
            index = ParserUtil.parseIndex(values.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditJobCommand.MESSAGE_USAGE), pe);
        }

        EditJobDescriptor editJobDescriptor = new EditJobDescriptor();

        if (values.getValue(OPTION_JOB_ROLE_OPT).isPresent()) {
            editJobDescriptor.setRole(values.getValue(OPTION_JOB_ROLE_OPT).get());
        }
        if (values.getValue(OPTION_JOB_DESC_OPT).isPresent()) {
            editJobDescriptor.setDescription(values.getValue(OPTION_JOB_DESC_OPT).get());
        }
        if (!editJobDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditJobCommand.MESSAGE_NOT_EDITED);
        }

        return new EditJobCommand(index, editJobDescriptor);
    }
}
