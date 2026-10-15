package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.testutil.PersonBuilder;

/**
 * Verifies add-command option parsing and person-field validation.
 */
public class AddCommandParserTest {
    private static final String NAME_OPTION = " -n " + VALID_NAME_BOB;
    private static final String PHONE_OPTION = " -p " + VALID_PHONE_BOB;
    private static final String EMAIL_OPTION = " -e " + VALID_EMAIL_BOB;
    private static final String ADDRESS_OPTION = " -a " + VALID_ADDRESS_BOB;
    private static final String VALID_ARGUMENTS = NAME_OPTION + PHONE_OPTION + EMAIL_OPTION + ADDRESS_OPTION;
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

    private final AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + VALID_ARGUMENTS, new AddCommand(BOB));
    }

    @Test
    public void parse_longAliases_success() {
        String arguments = " -name " + VALID_NAME_BOB + " -phone " + VALID_PHONE_BOB
                + " -email " + VALID_EMAIL_BOB + " -address " + VALID_ADDRESS_BOB;
        assertParseSuccess(parser, arguments, new AddCommand(BOB));
    }

    @Test
    public void parse_mixedAliasesInAnyOrder_success() {
        String arguments = "-email " + VALID_EMAIL_BOB + ADDRESS_OPTION
                + " -name " + VALID_NAME_BOB + PHONE_OPTION;
        assertParseSuccess(parser, arguments, new AddCommand(BOB));
    }

    @Test
    public void parse_optionsSeparatedByTabs_success() {
        String arguments = "\t-n\t" + VALID_NAME_BOB + "\t-p\t" + VALID_PHONE_BOB
                + "\t-e\t" + VALID_EMAIL_BOB + "\t-a\t" + VALID_ADDRESS_BOB + "\t";
        assertParseSuccess(parser, arguments, new AddCommand(BOB));
    }

    @Test
    public void parse_hyphensInsideValues_success() {
        Person expectedPerson = new PersonBuilder(BOB).withEmail("bob-choo@example.com")
                .withAddress("123 Bobby Street, #01-01").build();
        String arguments = NAME_OPTION + PHONE_OPTION
                + " -e bob-choo@example.com -a 123 Bobby Street, #01-01";
        assertParseSuccess(parser, arguments, new AddCommand(expectedPerson));
    }

    @Test
    public void parse_repeatedOptions_failure() {
        for (String duplicate : List.of(" -n Amy Bee", " -name Amy Bee", " -p 1234567", " -phone 1234567",
                " -e amy@example.com", " -email amy@example.com", " -a Amy Street", " -address Amy Street")) {
            assertParseFailure(parser, VALID_ARGUMENTS + duplicate, MESSAGE_INVALID_FORMAT);
        }
    }

    @Test
    public void parse_repeatedInvalidValues_failure() {
        for (String duplicate : List.of(" -n James&", " -p 911a", " -e bob!yahoo", " -a ")) {
            assertParseFailure(parser, duplicate + VALID_ARGUMENTS, MESSAGE_INVALID_FORMAT);
            assertParseFailure(parser, VALID_ARGUMENTS + duplicate, MESSAGE_INVALID_FORMAT);
        }
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        for (String arguments : List.of("", PHONE_OPTION + EMAIL_OPTION + ADDRESS_OPTION,
                NAME_OPTION + EMAIL_OPTION + ADDRESS_OPTION, NAME_OPTION + PHONE_OPTION + ADDRESS_OPTION,
                NAME_OPTION + PHONE_OPTION + EMAIL_OPTION)) {
            assertParseFailure(parser, arguments, MESSAGE_INVALID_FORMAT);
        }
    }

    @Test
    public void parse_optionValueMissing_failure() {
        for (String arguments : List.of(" -n " + PHONE_OPTION + EMAIL_OPTION + ADDRESS_OPTION,
                NAME_OPTION + " -p " + EMAIL_OPTION + ADDRESS_OPTION,
                NAME_OPTION + PHONE_OPTION + " -e " + ADDRESS_OPTION,
                NAME_OPTION + PHONE_OPTION + EMAIL_OPTION + " -a ")) {
            assertParseFailure(parser, arguments, MESSAGE_INVALID_FORMAT);
        }
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, " -n James&" + PHONE_OPTION + EMAIL_OPTION + ADDRESS_OPTION,
                Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_OPTION + " -p 911a" + EMAIL_OPTION + ADDRESS_OPTION,
                Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_OPTION + PHONE_OPTION + " -e bob!yahoo" + ADDRESS_OPTION,
                Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_OPTION + PHONE_OPTION + EMAIL_OPTION + " -a Block\n123",
                Address.MESSAGE_CONSTRAINTS);

        // Report the first invalid field when multiple person details are invalid.
        assertParseFailure(parser, " -n James& -p 911a" + EMAIL_OPTION + ADDRESS_OPTION,
                Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_unknownOptions_failure() {
        for (String option : List.of(" -unknown value", " -applicant", " -t friend")) {
            assertParseFailure(parser, VALID_ARGUMENTS + option, MESSAGE_INVALID_FORMAT);
        }
    }

    @Test
    public void parse_nonEmptyPreamble_failure() {
        assertParseFailure(parser, "unexpected text" + VALID_ARGUMENTS, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_legacySyntax_failure() {
        String arguments = " n/" + VALID_NAME_BOB + " p/" + VALID_PHONE_BOB
                + " e/" + VALID_EMAIL_BOB + " a/" + VALID_ADDRESS_BOB;
        assertParseFailure(parser, arguments, MESSAGE_INVALID_FORMAT);
    }
}
