package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.OPTION_EMAIL_REQ;
import static seedu.address.logic.parser.CliSyntax.OPTION_NAME_REQ;
import static seedu.address.logic.parser.CliSyntax.OPTION_PHONE_REQ;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.NameContainsKeywordsPredicate;
import seedu.address.testutil.EditApplicantDescriptorBuilder;

/**
 * Contains helper methods for testing commands.
 */
public class CommandTestUtil {

    public static final String VALID_NAME_AMY = "Amy Bee";
    public static final String VALID_NAME_BOB = "Bob Choo";
    public static final String VALID_PHONE_AMY = "11111111";
    public static final String VALID_PHONE_BOB = "22222222";
    public static final String VALID_EMAIL_AMY = "amy@example.com";
    public static final String VALID_EMAIL_BOB = "bob@example.com";
    public static final String VALID_JOB_AMY = "Janitor";
    public static final String VALID_JOB_BOB = "Engineer";

    public static final String NAME_DESC_AMY = " " + OPTION_NAME_REQ + " " + VALID_NAME_AMY;
    public static final String NAME_DESC_BOB = " " + OPTION_NAME_REQ + " " + VALID_NAME_BOB;
    public static final String PHONE_DESC_AMY = " " + OPTION_PHONE_REQ + " " + VALID_PHONE_AMY;
    public static final String PHONE_DESC_BOB = " " + OPTION_PHONE_REQ + " " + VALID_PHONE_BOB;
    public static final String EMAIL_DESC_AMY = " " + OPTION_EMAIL_REQ + " " + VALID_EMAIL_AMY;
    public static final String EMAIL_DESC_BOB = " " + OPTION_EMAIL_REQ + " " + VALID_EMAIL_BOB;

    public static final String INVALID_NAME_DESC = " " + OPTION_NAME_REQ + " James&"; // '&' not allowed in names
    public static final String INVALID_PHONE_DESC = " " + OPTION_PHONE_REQ + " 911a"; // 'a' not allowed in phones
    public static final String INVALID_EMAIL_DESC = " " + OPTION_EMAIL_REQ + " bob!yahoo"; // missing '@' symbol

    public static final String PREAMBLE_WHITESPACE = "\t  \r  \n";
    public static final String PREAMBLE_NON_EMPTY = "NonEmptyPreamble";

    public static final EditCommand.EditApplicantDescriptor DESC_AMY;
    public static final EditCommand.EditApplicantDescriptor DESC_BOB;

    static {
        DESC_AMY = new EditApplicantDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_AMY).withEmail(VALID_EMAIL_AMY)
                .build();
        DESC_BOB = new EditApplicantDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB)
                .build();
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - the returned {@link CommandResult} matches {@code expectedCommandResult} <br>
     * - the {@code actualModel} matches {@code expectedModel}
     */
    public static void assertCommandSuccess(Command command, Model actualModel, CommandResult expectedCommandResult,
            Model expectedModel) {
        try {
            CommandResult result = command.execute(actualModel);
            assertEquals(expectedCommandResult, result);
            assertEquals(expectedModel, actualModel);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    /**
     * Convenience wrapper to {@link #assertCommandSuccess(Command, Model, CommandResult, Model)}
     * that takes a string {@code expectedMessage}.
     */
    public static void assertCommandSuccess(Command command, Model actualModel, String expectedMessage,
            Model expectedModel) {
        CommandResult expectedCommandResult = new CommandResult(expectedMessage);
        assertCommandSuccess(command, actualModel, expectedCommandResult, expectedModel);
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - a {@code CommandException} is thrown <br>
     * - the CommandException message matches {@code expectedMessage} <br>
     * - the address book, filtered applicant list and selected applicant in {@code actualModel} remain unchanged
     */
    public static void assertCommandFailure(Command command, Model actualModel, String expectedMessage) {
        // we are unable to defensively copy the model for comparison later, so we can
        // only do so by copying its components.
        AddressBook expectedAddressBook = new AddressBook(actualModel.getAddressBook());
        List<Applicant> expectedFilteredList = new ArrayList<>(actualModel.getFilteredApplicantList());

        assertThrows(CommandException.class, expectedMessage, () -> command.execute(actualModel));
        assertEquals(expectedAddressBook, actualModel.getAddressBook());
        assertEquals(expectedFilteredList, actualModel.getFilteredApplicantList());
    }
    /**
     * Updates {@code model}'s filtered list to show only the applicant at the given {@code targetIndex} in the
     * {@code model}'s address book.
     */
    public static void showApplicantAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredApplicantList().size());

        Applicant applicant = model.getFilteredApplicantList().get(targetIndex.getZeroBased());
        final String[] splitName = applicant.getName().fullName.split("\\s+");
        model.updateFilteredApplicantList(new NameContainsKeywordsPredicate(List.of(splitName[0])));

        assertEquals(1, model.getFilteredApplicantList().size());
    }

}
