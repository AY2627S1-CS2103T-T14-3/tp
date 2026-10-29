package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.OPTION_EMAIL_REQ;
import static seedu.address.logic.parser.CliSyntax.OPTION_NAME_REQ;
import static seedu.address.logic.parser.CliSyntax.OPTION_PHONE_REQ;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.Email;
import seedu.address.model.applicant.Name;
import seedu.address.model.applicant.Phone;

/**
 * Parses hyphen-prefixed options and creates a command to add a applicant.
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the required applicant details from short or long option aliases.
     *
     * @param args The options following the {@code add} command.
     * @return A command that adds the parsed applicant.
     * @throws ParseException If the options or applicant details are invalid.
     */
    @Override
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap;
        try {
            argMultimap = ArgumentTokenizer.tokenizeOptions(args,
                    ArgumentTokenizer.NO_PREAMBLE, OPTION_NAME_REQ, OPTION_PHONE_REQ, OPTION_EMAIL_REQ);
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE), pe);
        }

        Name name = ParserUtil.parseName(argMultimap.getValue(OPTION_NAME_REQ).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(OPTION_PHONE_REQ).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(OPTION_EMAIL_REQ).get());

        Applicant applicant = new Applicant(name, phone, email);

        return new AddCommand(applicant);
    }
}
