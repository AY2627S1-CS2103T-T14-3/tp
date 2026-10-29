package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.applicant.Email;
import seedu.address.model.applicant.Name;
import seedu.address.model.applicant.Phone;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);
    private static final String NAME_OPTION = " -n " + VALID_NAME_AMY;
    private static final String PHONE_OPTION = " -p " + VALID_PHONE_AMY;
    private static final String EMAIL_OPTION = " -e " + VALID_EMAIL_AMY;

    private final EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_AMY).withEmail(VALID_EMAIL_AMY).build();
        assertParseSuccess(parser, " 2" + NAME_OPTION + PHONE_OPTION + EMAIL_OPTION,
                new EditCommand(INDEX_SECOND_PERSON, descriptor));
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withEmail(VALID_EMAIL_AMY).build();
        assertParseSuccess(parser, " 1" + NAME_OPTION + EMAIL_OPTION,
                new EditCommand(INDEX_FIRST_PERSON, descriptor));
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        EditPersonDescriptor nameOnly = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY).build();
        assertParseSuccess(parser, " 1" + NAME_OPTION, new EditCommand(INDEX_FIRST_PERSON, nameOnly));

        EditPersonDescriptor phoneOnly = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_AMY).build();
        assertParseSuccess(parser, " 1" + PHONE_OPTION, new EditCommand(INDEX_FIRST_PERSON, phoneOnly));

        EditPersonDescriptor emailOnly = new EditPersonDescriptorBuilder().withEmail(VALID_EMAIL_AMY).build();
        assertParseSuccess(parser, " 1" + EMAIL_OPTION, new EditCommand(INDEX_FIRST_PERSON, emailOnly));
    }

    @Test
    public void parse_longAndMixedAliasesInAnyOrder_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_AMY).withEmail(VALID_EMAIL_AMY).build();
        EditCommand expected = new EditCommand(INDEX_FIRST_PERSON, descriptor);
        assertParseSuccess(parser, " 1 -email " + VALID_EMAIL_AMY + " -phone " + VALID_PHONE_AMY
                + " -name " + VALID_NAME_AMY, expected);
        assertParseSuccess(parser, " 1" + EMAIL_OPTION + " -name " + VALID_NAME_AMY + PHONE_OPTION, expected);
        assertParseSuccess(parser, " 1 -phone " + VALID_PHONE_AMY + NAME_OPTION + " -email " + VALID_EMAIL_AMY,
                expected);
    }

    @Test
    public void parse_extraWhitespace_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_AMY).build();
        assertParseSuccess(parser, " \t1 \t-n\t" + VALID_NAME_AMY + " \t-p\t" + VALID_PHONE_AMY + " \t",
                new EditCommand(INDEX_FIRST_PERSON, descriptor));
    }

    @Test
    public void parse_hyphenInsideValue_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withEmail("amy-bee@example.com").build();
        assertParseSuccess(parser, " 1 -e amy-bee@example.com", new EditCommand(INDEX_FIRST_PERSON, descriptor));
    }

    @Test
    public void parse_missingIndex_failure() {
        for (String args : List.of("", " ", NAME_OPTION, PHONE_OPTION, EMAIL_OPTION,
                NAME_OPTION + PHONE_OPTION + EMAIL_OPTION)) {
            assertParseFailure(parser, args, MESSAGE_INVALID_FORMAT);
        }
    }

    @Test
    public void parse_invalidIndex_failure() {
        for (String index : List.of("0", "-1", "one", "1.5", "2147483648", "1 extra", "1 2")) {
            assertParseFailure(parser, index + NAME_OPTION, MESSAGE_INVALID_FORMAT);
        }
    }

    @Test
    public void parse_noFieldSpecified_failure() {
        assertParseFailure(parser, "1", EditCommand.MESSAGE_NOT_EDITED);
        assertParseFailure(parser, " \t1 \t", EditCommand.MESSAGE_NOT_EDITED);
    }

    @Test
    public void parse_missingValue_failure() {
        for (String args : List.of(" 1 -p", " 1 -phone", " 1 -p " + NAME_OPTION, " 1 -n", " 1 -e",
                " 1 -n " + EMAIL_OPTION, " 1" + NAME_OPTION + " -email")) {
            assertParseFailure(parser, args, MESSAGE_INVALID_FORMAT);
        }
    }

    @Test
    public void parse_invalidFieldValue_failure() {
        assertParseFailure(parser, " 1 -n James&", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 1 -p 911a", Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 1 -phone 12", Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 1 -e bob!yahoo", Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 1 -n James& -p 911a -e bob!yahoo", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 1 -p 911a" + EMAIL_OPTION, Phone.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_repeatedOptions_failure() {
        String fields = NAME_OPTION + PHONE_OPTION + EMAIL_OPTION;
        for (String duplicate : List.of(" -p 22222222", " -phone 22222222", " -n Bob", " -name Bob",
                " -e bob@example.com", " -email bob@example.com", " -n James&", " -email bob!yahoo", " -p 911a")) {
            assertParseFailure(parser, " 1" + fields + duplicate, MESSAGE_INVALID_FORMAT);
            assertParseFailure(parser, " 1" + duplicate + fields, MESSAGE_INVALID_FORMAT);
        }
    }

    @Test
    public void parse_unknownOptions_failure() {
        for (String option : List.of(" -unknown value", " -applicant 1", " -a Address", " -job Janitor",
                " -application 1", " -N Amy", " --name Amy")) {
            assertParseFailure(parser, " 1" + option + EMAIL_OPTION, MESSAGE_INVALID_FORMAT);
        }
    }

    @Test
    public void parse_legacySyntaxAndInvalidPreamble_failure() {
        for (String args : List.of("1 n/John Doe e/john@example.com", "1 p/91234567", "1 e/john@example.com",
                "unexpected 1" + NAME_OPTION, " -p 1" + NAME_OPTION, " -applicant 1" + NAME_OPTION,
                NAME_OPTION + " 1", " 1 extra" + NAME_OPTION)) {
            assertParseFailure(parser, args, MESSAGE_INVALID_FORMAT);
        }
    }
}
