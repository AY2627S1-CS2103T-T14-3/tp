package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_JOB_ROLE;
import static seedu.address.logic.parser.CliSyntax.OPTION_JOB_DESC_OPT;
import static seedu.address.logic.parser.CliSyntax.OPTION_JOB_ROLE_OPT;
import static seedu.address.logic.parser.CliSyntax.OPTION_NAME_OPT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddJobCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditJobCommand;
import seedu.address.logic.commands.EditJobCommand.EditJobDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.application.Job;

/**
 * Parses a displayed job index and a replacement description into an EditJobCommand.
 */
public class EditJobCommandParser implements Parser<EditJobCommand> {

    /**
     * Parses a leading job index followed by a required job description.
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
            editJobDescriptor.setRole(values.getValue(OPTION_JOB_ROLE_OPT).get().trim());
        }
        if (values.getValue(OPTION_JOB_DESC_OPT).isPresent()) {
            editJobDescriptor.setDescription(values.getValue(OPTION_JOB_DESC_OPT).get().trim());
        }
        if (!editJobDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditJobCommand.MESSAGE_NOT_EDITED);
        }

        return new EditJobCommand(index, editJobDescriptor);
    }
}
