package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.OPTION_EMAIL_OPT;
import static seedu.address.logic.parser.CliSyntax.OPTION_NAME_OPT;
import static seedu.address.logic.parser.CliSyntax.OPTION_PHONE_OPT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditApplicantDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses an applicant index followed by name, phone, and email options into an EditCommand.
 */
public class EditCommandParser implements Parser<EditCommand> {

    /**
     * Parses a leading applicant index and at least one of name, phone, or email.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap;
        Index index;

        try {
            argMultimap = ArgumentTokenizer.tokenizeOptions(args, ArgumentTokenizer.HAS_PREAMBLE,
                    OPTION_NAME_OPT, OPTION_PHONE_OPT, OPTION_EMAIL_OPT);
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), pe);
        }

        EditApplicantDescriptor editApplicantDescriptor = new EditApplicantDescriptor();

        if (argMultimap.getValue(OPTION_NAME_OPT).isPresent()) {
            editApplicantDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(OPTION_NAME_OPT).get()));
        }
        if (argMultimap.getValue(OPTION_PHONE_OPT).isPresent()) {
            editApplicantDescriptor.setPhone(ParserUtil.parsePhone(argMultimap.getValue(OPTION_PHONE_OPT).get()));
        }
        if (argMultimap.getValue(OPTION_EMAIL_OPT).isPresent()) {
            editApplicantDescriptor.setEmail(ParserUtil.parseEmail(argMultimap.getValue(OPTION_EMAIL_OPT).get()));
        }
        if (!editApplicantDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editApplicantDescriptor);
    }

}
