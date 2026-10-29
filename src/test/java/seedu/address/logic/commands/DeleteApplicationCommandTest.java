package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;

public class DeleteApplicationCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_existingApplication_keepsApplicantJobAndOtherApplications() {
        Job job = new Job("Janitor");
        model.addJob(job);
        model.addApplication(new Application(ALICE, job, Application.Status.PENDING));
        Application otherApplication = new Application(BENSON, job, Application.Status.ACCEPT);
        model.addApplication(otherApplication);

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.addJob(job);
        expectedModel.addApplication(otherApplication);
        CommandResult expectedResult = new CommandResult(String.format(
                DeleteApplicationCommand.MESSAGE_SUCCESS, ALICE.getName(), ALICE.getEmail()), ALICE);

        assertCommandSuccess(new DeleteApplicationCommand(INDEX_FIRST_PERSON), model, expectedResult, expectedModel);
        assertFalse(model.hasApplication(ALICE));
        assertTrue(model.hasPerson(ALICE));
        assertTrue(model.hasJob(job));
    }

    @Test
    public void execute_filteredList_usesDisplayedIndexAndKeepsFilter() {
        Job job = new Job("Janitor");
        model.addJob(job);
        Application otherApplication = new Application(ALICE, job, Application.Status.PENDING);
        model.addApplication(otherApplication);
        model.addApplication(new Application(BENSON, job, Application.Status.REJECT));
        showPersonAtIndex(model, INDEX_SECOND_PERSON);

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.addJob(job);
        expectedModel.addApplication(otherApplication);
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);
        CommandResult expectedResult = new CommandResult(String.format(
                DeleteApplicationCommand.MESSAGE_SUCCESS, BENSON.getName(), BENSON.getEmail()), BENSON);

        assertCommandSuccess(new DeleteApplicationCommand(INDEX_FIRST_PERSON), model, expectedResult, expectedModel);
        assertEquals(1, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_missingApplication_throwsCommandException() {
        assertCommandFailure(new DeleteApplicationCommand(INDEX_FIRST_PERSON), model,
                DeleteApplicationCommand.MESSAGE_MISSING_APPLICATION);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        int listSize = model.getFilteredPersonList().size();
        DeleteApplicationCommand command = new DeleteApplicationCommand(Index.fromOneBased(listSize + 1));

        assertCommandFailure(command, model, String.format(DeleteApplicationCommand.MESSAGE_INVALID_INDEX, listSize));
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        assertCommandFailure(new DeleteApplicationCommand(INDEX_SECOND_PERSON), model,
                String.format(DeleteApplicationCommand.MESSAGE_INVALID_INDEX, 1));
    }

    @Test
    public void execute_emptyList_throwsCommandException() {
        assertCommandFailure(new DeleteApplicationCommand(INDEX_FIRST_PERSON), new ModelManager(),
                DeleteApplicationCommand.MESSAGE_EMPTY_LIST);
    }

    @Test
    public void equals() {
        DeleteApplicationCommand command = new DeleteApplicationCommand(INDEX_FIRST_PERSON);
        assertTrue(command.equals(command));
        assertTrue(command.equals(new DeleteApplicationCommand(INDEX_FIRST_PERSON)));
        assertFalse(command.equals(new DeleteApplicationCommand(INDEX_SECOND_PERSON)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ListCommand()));
    }
}
