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
                DeleteApplicationCommand.MESSAGE_SUCCESS, ALICE.getName(), ALICE.getEmail()),
                ViewRequest.applicant(ALICE));

        assertCommandSuccess(new DeleteApplicationCommand(INDEX_FIRST_APPLICANT), model,
                expectedResult, expectedModel);
        assertFalse(model.hasApplication(ALICE));
        assertTrue(model.hasApplicant(ALICE));
        assertTrue(model.hasJob(job));
    }

    @Test
    public void execute_filteredList_usesDisplayedIndexAndKeepsFilter() {
        Job job = new Job("Janitor");
        model.addJob(job);
        Application otherApplication = new Application(ALICE, job, Application.Status.PENDING);
        model.addApplication(otherApplication);
        model.addApplication(new Application(BENSON, job, Application.Status.REJECT));
        showApplicantAtIndex(model, INDEX_SECOND_APPLICANT);

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.addJob(job);
        expectedModel.addApplication(otherApplication);
        showApplicantAtIndex(expectedModel, INDEX_SECOND_APPLICANT);
        CommandResult expectedResult = new CommandResult(String.format(
                DeleteApplicationCommand.MESSAGE_SUCCESS, BENSON.getName(), BENSON.getEmail()),
                ViewRequest.applicant(BENSON));

        assertCommandSuccess(new DeleteApplicationCommand(INDEX_FIRST_APPLICANT), model,
                expectedResult, expectedModel);
        assertEquals(1, model.getFilteredApplicantList().size());
    }

    @Test
    public void execute_missingApplication_throwsCommandException() {
        assertCommandFailure(new DeleteApplicationCommand(INDEX_FIRST_APPLICANT), model,
                DeleteApplicationCommand.MESSAGE_MISSING_APPLICATION);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        int listSize = model.getFilteredApplicantList().size();
        DeleteApplicationCommand command = new DeleteApplicationCommand(Index.fromOneBased(listSize + 1));

        assertCommandFailure(command, model, String.format(DeleteApplicationCommand.MESSAGE_INVALID_INDEX, listSize));
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showApplicantAtIndex(model, INDEX_FIRST_APPLICANT);

        assertCommandFailure(new DeleteApplicationCommand(INDEX_SECOND_APPLICANT), model,
                String.format(DeleteApplicationCommand.MESSAGE_INVALID_INDEX, 1));
    }

    @Test
    public void execute_emptyList_throwsCommandException() {
        assertCommandFailure(new DeleteApplicationCommand(INDEX_FIRST_APPLICANT), new ModelManager(),
                DeleteApplicationCommand.MESSAGE_EMPTY_LIST);
    }

    @Test
    public void equals() {
        DeleteApplicationCommand command = new DeleteApplicationCommand(INDEX_FIRST_APPLICANT);
        assertTrue(command.equals(command));
        assertTrue(command.equals(new DeleteApplicationCommand(INDEX_FIRST_APPLICANT)));
        assertFalse(command.equals(new DeleteApplicationCommand(INDEX_SECOND_APPLICANT)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ListCommand()));
    }
}
