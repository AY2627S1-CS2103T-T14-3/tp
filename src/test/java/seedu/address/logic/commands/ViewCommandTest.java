package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class ViewCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_showsDetailsWithoutChangingModel() {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandSuccess(new ViewCommand(INDEX_FIRST_PERSON), model,
                "Name: Alice Pauline\nEmail: alice@example.com", expectedModel);
    }

    @Test
    public void execute_validIndexFilteredList_usesDisplayedIndexWithoutChangingFilter() {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);

        assertCommandSuccess(new ViewCommand(INDEX_FIRST_PERSON), model,
                "Name: Benson Meier\nEmail: johnd@example.com", expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        int listSize = model.getFilteredPersonList().size();
        ViewCommand command = new ViewCommand(Index.fromOneBased(listSize + 1));

        assertCommandFailure(command, model, String.format(ViewCommand.MESSAGE_INVALID_INDEX, listSize));
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        assertCommandFailure(new ViewCommand(INDEX_SECOND_PERSON), model,
                "Invalid applicant index. Please enter an integer from 1 to 1.");
    }

    @Test
    public void execute_emptyList_throwsCommandException() {
        Model emptyModel = new ModelManager();
        assertCommandFailure(new ViewCommand(INDEX_FIRST_PERSON), emptyModel, ViewCommand.MESSAGE_EMPTY_LIST);
    }

    @Test
    public void equals() {
        ViewCommand command = new ViewCommand(INDEX_FIRST_PERSON);
        assertTrue(command.equals(command));
        assertTrue(command.equals(new ViewCommand(INDEX_FIRST_PERSON)));
        assertFalse(command.equals(new ViewCommand(INDEX_SECOND_PERSON)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ListCommand()));
    }

    @Test
    public void toStringMethod() {
        ViewCommand command = new ViewCommand(INDEX_FIRST_PERSON);
        String expected = ViewCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON + "}";
        assertEquals(expected, command.toString());
    }
}
