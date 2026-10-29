package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showApplicantAtIndex;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.BENSON;
import static seedu.address.testutil.TypicalApplicants.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_APPLICANT;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_APPLICANT;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class ViewCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_returnsApplicantWithoutChangingModel() {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        CommandResult expectedResult = new CommandResult("Viewing applicant: Alice Pauline", ALICE);
        assertCommandSuccess(new ViewCommand(INDEX_FIRST_APPLICANT), model,
                expectedResult, expectedModel);
    }

    @Test
    public void execute_validIndexFilteredList_usesDisplayedIndexWithoutChangingFilter() {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showApplicantAtIndex(model, INDEX_SECOND_APPLICANT);
        showApplicantAtIndex(expectedModel, INDEX_SECOND_APPLICANT);

        CommandResult expectedResult = new CommandResult("Viewing applicant: Benson Meier", BENSON);
        assertCommandSuccess(new ViewCommand(INDEX_FIRST_APPLICANT), model,
                expectedResult, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        int listSize = model.getFilteredApplicantList().size();
        ViewCommand command = new ViewCommand(Index.fromOneBased(listSize + 1));

        assertCommandFailure(command, model, String.format(ViewCommand.MESSAGE_INVALID_INDEX, listSize));
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showApplicantAtIndex(model, INDEX_FIRST_APPLICANT);

        assertCommandFailure(new ViewCommand(INDEX_SECOND_APPLICANT), model,
                "Invalid applicant index. Please enter an integer from 1 to 1.");
    }

    @Test
    public void execute_emptyList_throwsCommandException() {
        Model emptyModel = new ModelManager();
        assertCommandFailure(new ViewCommand(INDEX_FIRST_APPLICANT), emptyModel, ViewCommand.MESSAGE_EMPTY_LIST);
    }

    @Test
    public void equals() {
        ViewCommand command = new ViewCommand(INDEX_FIRST_APPLICANT);
        assertTrue(command.equals(command));
        assertTrue(command.equals(new ViewCommand(INDEX_FIRST_APPLICANT)));
        assertFalse(command.equals(new ViewCommand(INDEX_SECOND_APPLICANT)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ListCommand()));
    }

    @Test
    public void toStringMethod() {
        ViewCommand command = new ViewCommand(INDEX_FIRST_APPLICANT);
        String expected = ViewCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_APPLICANT + "}";
        assertEquals(expected, command.toString());
    }
}
