package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * Parses hyphen-prefixed options and creates a command to add a person.
 */
public class AddCommandParser implements Parser<AddCommand> {

    private static final Option OPTION_NAME = Option.required(PREFIX_NAME, "-n", "-name");
    private static final Option OPTION_PHONE = Option.required(PREFIX_PHONE, "-p", "-phone");
    private static final Option OPTION_EMAIL = Option.required(PREFIX_EMAIL, "-e", "-email");
    private static final Option OPTION_ADDRESS = Option.required(PREFIX_ADDRESS, "-a", "-address");

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
                    OPTION_NAME, OPTION_PHONE, OPTION_EMAIL, OPTION_ADDRESS);
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE), pe);
        }

        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get());

        Person person = new Person(name, phone, email, address);

        return new AddCommand(person);
    }
}
