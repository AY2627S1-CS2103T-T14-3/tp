package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.OPTION_EMAIL_REQ;
import static seedu.address.logic.parser.CliSyntax.OPTION_NAME_REQ;
import static seedu.address.logic.parser.CliSyntax.OPTION_PHONE_REQ;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * Parses hyphen-prefixed options and creates a command to add a person.
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the required person details from short or long option aliases.
     *
     * @param args The options following the {@code add} command.
     * @return A command that adds the parsed person.
     * @throws ParseException If the options or person details are invalid.
     */
    @Override
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap;
        try {
            argMultimap = ArgumentTokenizer.tokenizeOptions(args,
                    false, OPTION_NAME_REQ, OPTION_PHONE_REQ, OPTION_EMAIL_REQ);
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE), pe);
        }

        Name name = ParserUtil.parseName(argMultimap.getValue(OPTION_NAME_REQ).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(OPTION_PHONE_REQ).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(OPTION_EMAIL_REQ).get());

        Person person = new Person(name, phone, email);

        return new AddCommand(person);
    }
}
