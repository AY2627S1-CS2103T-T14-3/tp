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
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;

public class SetApplicationStatusCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_existingApplication_updatesStatusAndKeepsJob() {
        model.addApplication(new Application(ALICE, new Job("Janitor"), Application.Status.PENDING));
        SetApplicationStatusCommand command = new SetApplicationStatusCommand(
                INDEX_FIRST, Application.Status.ACCEPT);

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.addApplication(new Application(ALICE, new Job("Janitor"), Application.Status.ACCEPT));
        CommandResult expectedResult = new CommandResult(String.format(
                SetApplicationStatusCommand.MESSAGE_SUCCESS, ALICE.getName(), ALICE.getEmail(),
                Application.Status.ACCEPT), ViewRequest.applicant(ALICE));

        assertCommandSuccess(command, model, expectedResult, expectedModel);
        Application updated = model.getApplication(ALICE).orElseThrow();
        assertEquals(new Job("Janitor"), updated.getJob());
        assertEquals(Application.Status.ACCEPT, updated.getStatus());
    }

    @Test
    public void execute_sameStatus_succeeds() {
        Application application = new Application(ALICE, new Job("Janitor"), Application.Status.PENDING);
        model.addApplication(application);
        SetApplicationStatusCommand command = new SetApplicationStatusCommand(
                INDEX_FIRST, Application.Status.PENDING);

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.addApplication(application);
        CommandResult expectedResult = new CommandResult(String.format(
                SetApplicationStatusCommand.MESSAGE_SUCCESS, ALICE.getName(), ALICE.getEmail(),
                Application.Status.PENDING), ViewRequest.applicant(ALICE));

        assertCommandSuccess(command, model, expectedResult, expectedModel);
    }

    @Test
    public void execute_filteredList_usesDisplayedIndexAndKeepsFilter() {
        model.addApplication(new Application(BENSON, new Job("Janitor"), Application.Status.PENDING));
        showApplicantAtIndex(model, INDEX_SECOND);
        SetApplicationStatusCommand command = new SetApplicationStatusCommand(
                INDEX_FIRST, Application.Status.REJECT);

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        showApplicantAtIndex(expectedModel, INDEX_SECOND);
        expectedModel.addApplication(new Application(BENSON, new Job("Janitor"), Application.Status.REJECT));
        CommandResult expectedResult = new CommandResult(String.format(
                SetApplicationStatusCommand.MESSAGE_SUCCESS, BENSON.getName(), BENSON.getEmail(),
                Application.Status.REJECT), ViewRequest.applicant(BENSON));

        assertCommandSuccess(command, model, expectedResult, expectedModel);
        assertEquals(1, model.getFilteredApplicantList().size());
    }

    @Test
    public void execute_missingApplication_throwsCommandException() {
        SetApplicationStatusCommand command = new SetApplicationStatusCommand(
                INDEX_FIRST, Application.Status.ACCEPT);

        assertCommandFailure(command, model, SetApplicationStatusCommand.MESSAGE_MISSING_APPLICATION);
        assertTrue(model.getApplication(ALICE).isEmpty());
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        int listSize = model.getFilteredApplicantList().size();
        SetApplicationStatusCommand command = new SetApplicationStatusCommand(
                Index.fromOneBased(listSize + 1), Application.Status.ACCEPT);

        assertCommandFailure(command, model,
                String.format(SetApplicationStatusCommand.MESSAGE_INVALID_INDEX, listSize));
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showApplicantAtIndex(model, INDEX_FIRST);
        SetApplicationStatusCommand command = new SetApplicationStatusCommand(
                INDEX_SECOND, Application.Status.ACCEPT);

        assertCommandFailure(command, model, "Invalid applicant index. Please enter an integer from 1 to 1.");
    }

    @Test
    public void execute_emptyList_throwsCommandException() {
        Model emptyModel = new ModelManager();
        SetApplicationStatusCommand command = new SetApplicationStatusCommand(
                INDEX_FIRST, Application.Status.ACCEPT);

        assertCommandFailure(command, emptyModel, SetApplicationStatusCommand.MESSAGE_EMPTY_LIST);
    }

    @Test
    public void equals() {
        SetApplicationStatusCommand command = new SetApplicationStatusCommand(
                INDEX_FIRST, Application.Status.ACCEPT);
        assertTrue(command.equals(command));
        assertTrue(command.equals(new SetApplicationStatusCommand(
                INDEX_FIRST, Application.Status.ACCEPT)));
        assertFalse(command.equals(new SetApplicationStatusCommand(
                INDEX_SECOND, Application.Status.ACCEPT)));
        assertFalse(command.equals(new SetApplicationStatusCommand(
                INDEX_FIRST, Application.Status.REJECT)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ListCommand()));
    }

    @Test
    public void toStringMethod() {
        SetApplicationStatusCommand command = new SetApplicationStatusCommand(
                INDEX_FIRST, Application.Status.ACCEPT);
        String expected = SetApplicationStatusCommand.class.getCanonicalName()
                + "{targetIndex=" + INDEX_FIRST + ", status=accept}";
        assertEquals(expected, command.toString());
    }
}
