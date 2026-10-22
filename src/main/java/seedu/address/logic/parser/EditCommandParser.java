package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.OPTION_EMAIL_OPT;
import static seedu.address.logic.parser.CliSyntax.OPTION_NAME_OPT;
import static seedu.address.logic.parser.CliSyntax.OPTION_PHONE_OPT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenizeOptions(args,
                        ArgumentTokenizer.HAS_PREAMBLE, OPTION_NAME_OPT, OPTION_PHONE_OPT, OPTION_EMAIL_OPT);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicateOptionsFor(OPTION_NAME_OPT, OPTION_PHONE_OPT, OPTION_EMAIL_OPT);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(OPTION_NAME_OPT).isPresent()) {
            editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(OPTION_NAME_OPT).get()));
        }
        if (argMultimap.getValue(OPTION_PHONE_OPT).isPresent()) {
            editPersonDescriptor.setPhone(ParserUtil.parsePhone(argMultimap.getValue(OPTION_PHONE_OPT).get()));
        }
        if (argMultimap.getValue(OPTION_EMAIL_OPT).isPresent()) {
            editPersonDescriptor.setEmail(ParserUtil.parseEmail(argMultimap.getValue(OPTION_EMAIL_OPT).get()));
        }
        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor);
    }

}
