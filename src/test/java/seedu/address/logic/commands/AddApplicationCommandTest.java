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
import static seedu.address.testutil.TypicalJobs.JANITOR;
import static seedu.address.testutil.TypicalJobs.addJobsToAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;

public class AddApplicationCommandTest {

    private final Model model = new ModelManager(addJobsToAddressBook(getTypicalAddressBook()), new UserPrefs());

    @Test
    public void execute_newApplicationWithoutStatus_addsPendingApplication() {
        Application application = new Application(ALICE, JANITOR, Application.Status.PENDING);
        AddApplicationCommand command = new AddApplicationCommand(
                INDEX_FIRST, "Janitor", Application.Status.PENDING);

        Model expectedModel = new ModelManager(addJobsToAddressBook(getTypicalAddressBook()), new UserPrefs());
        expectedModel.addApplication(application);
        CommandResult expectedResult = new CommandResult(String.format(
                AddApplicationCommand.MESSAGE_SUCCESS, ALICE.getName(), ALICE.getEmail()),
                ViewRequest.applicant(ALICE));

        assertCommandSuccess(command, model, expectedResult, expectedModel);
        assertEquals(application, model.getApplication(ALICE).orElseThrow());
    }

    @Test
    public void execute_newApplicationWithStatus_addsRequestedStatus() {

        Application application = new Application(BENSON, JANITOR, Application.Status.ACCEPT);
        AddApplicationCommand command = new AddApplicationCommand(
                INDEX_SECOND, "Janitor", Application.Status.ACCEPT);

        Model expectedModel = new ModelManager(addJobsToAddressBook(getTypicalAddressBook()), new UserPrefs());
        expectedModel.addApplication(application);
        CommandResult expectedResult = new CommandResult(String.format(
                AddApplicationCommand.MESSAGE_SUCCESS, BENSON.getName(), BENSON.getEmail()),
                ViewRequest.applicant(BENSON));

        assertCommandSuccess(command, model, expectedResult, expectedModel);
    }

    @Test
    public void execute_filteredList_usesDisplayedIndexAndKeepsFilter() {
        showApplicantAtIndex(model, INDEX_SECOND);
        AddApplicationCommand command = new AddApplicationCommand(
                INDEX_FIRST, "Janitor", Application.Status.REJECT);

        Model expectedModel = new ModelManager(addJobsToAddressBook(getTypicalAddressBook()), new UserPrefs());
        showApplicantAtIndex(expectedModel, INDEX_SECOND);
        expectedModel.addApplication(new Application(BENSON, new Job("Janitor"), Application.Status.REJECT));
        CommandResult expectedResult = new CommandResult(String.format(
                AddApplicationCommand.MESSAGE_SUCCESS, BENSON.getName(), BENSON.getEmail()),
                ViewRequest.applicant(BENSON));

        assertCommandSuccess(command, model, expectedResult, expectedModel);
        assertEquals(1, model.getFilteredApplicantList().size());
    }

    @Test
    public void execute_duplicateApplication_throwsCommandException() {
        model.addApplication(new Application(ALICE, new Job("Janitor"), Application.Status.PENDING));
        AddApplicationCommand command = new AddApplicationCommand(
                INDEX_FIRST, "Engineer", Application.Status.ACCEPT);

        assertCommandFailure(command, model, AddApplicationCommand.MESSAGE_DUPLICATE_APPLICATION);
        assertEquals(new Job("Janitor"), model.getApplication(ALICE).orElseThrow().getJob());
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        int listSize = model.getFilteredApplicantList().size();
        AddApplicationCommand command = new AddApplicationCommand(
                Index.fromOneBased(listSize + 1), "Janitor", Application.Status.PENDING);

        assertCommandFailure(command, model, String.format(AddApplicationCommand.MESSAGE_INVALID_INDEX, listSize));
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showApplicantAtIndex(model, INDEX_FIRST);
        AddApplicationCommand command = new AddApplicationCommand(
                INDEX_SECOND, "Janitor", Application.Status.PENDING);

        assertCommandFailure(command, model, "Invalid applicant index. Please enter an integer from 1 to 1.");
    }

    @Test
    public void execute_emptyList_throwsCommandException() {
        Model emptyModel = new ModelManager();
        AddApplicationCommand command = new AddApplicationCommand(
                INDEX_FIRST, "Janitor", Application.Status.PENDING);

        assertCommandFailure(command, emptyModel, AddApplicationCommand.MESSAGE_EMPTY_LIST);
    }

    @Test
    public void equals() {
        AddApplicationCommand command = new AddApplicationCommand(
                INDEX_FIRST, "Janitor", Application.Status.PENDING);
        assertTrue(command.equals(command));
        assertTrue(command.equals(new AddApplicationCommand(
                INDEX_FIRST, "Janitor", Application.Status.PENDING)));
        assertFalse(command.equals(new AddApplicationCommand(
                INDEX_SECOND, "Janitor", Application.Status.PENDING)));
        assertFalse(command.equals(new AddApplicationCommand(
                INDEX_FIRST, "Engineer", Application.Status.PENDING)));
        assertFalse(command.equals(new AddApplicationCommand(
                INDEX_FIRST, "Janitor", Application.Status.ACCEPT)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ListCommand()));
    }

    @Test
    public void toStringMethod() {
        AddApplicationCommand command = new AddApplicationCommand(
                INDEX_FIRST, "Janitor", Application.Status.PENDING);
        String expected = AddApplicationCommand.class.getCanonicalName()
                + "{targetIndex=" + INDEX_FIRST
                + ", jobRole=Janitor, status=pending}";
        assertEquals(expected, command.toString());
    }
}
