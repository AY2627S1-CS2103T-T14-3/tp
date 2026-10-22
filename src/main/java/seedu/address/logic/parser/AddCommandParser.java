package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.regex.Pattern;

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

    private static final Option OPTION_NAME = Option.required(PREFIX_NAME, "-n", "-name");
    private static final Option OPTION_PHONE = Option.required(PREFIX_PHONE, "-p", "-phone");
    private static final Option OPTION_EMAIL = Option.required(PREFIX_EMAIL, "-e", "-email");

    /**
     * Matches an option used only when adding an applicant. {@code -p} is excluded because
     * adding an application uses that option for the applicant index.
     */
    private static final Pattern APPLICANT_ONLY_OPTION =
            Pattern.compile("(?<!\\S)-(?:name|phone|email|n|e)(?=\\s|$)");

    /**
     * Returns true if {@code args} contains an option used only when adding an applicant.
     * @param args arguments string following the add command word
     * @return true if {@code args} includes an applicant-only option
     */
    static boolean hasExclusiveOption(String args) {
        return APPLICANT_ONLY_OPTION.matcher(args).find();
    }

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
                    OPTION_NAME, OPTION_PHONE, OPTION_EMAIL);
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE), pe);
        }

        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());

        Person person = new Person(name, phone, email);

        return new AddCommand(person);
    }
}
