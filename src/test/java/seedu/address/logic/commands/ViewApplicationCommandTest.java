package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

/** Tests application selection using the applicant indexes displayed on application rows. */
public class ViewApplicationCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_index_selectsApplicationByApplicantIndex() {
        Job job = new Job("Janitor");
        model.addJob(job);
        Application application = new Application(BENSON, job, Application.Status.ACCEPT);
        model.addApplication(application);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        // The only application row displays index 2 because Alice has no application.
        assertCommandSuccess(new ViewApplicationCommand(INDEX_SECOND), model,
                new CommandResult("Viewing application for Benson Meier.", ViewRequest.application(application)),
                expectedModel);
    }

    @Test
    public void execute_applicationStorageOrderDiffers_usesApplicantIndex() {
        Job job = new Job("Janitor");
        model.addJob(job);
        model.addApplication(new Application(BENSON, job, Application.Status.ACCEPT));
        Application application = new Application(ALICE, job, Application.Status.PENDING);
        model.addApplication(application);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new ViewApplicationCommand(INDEX_FIRST), model,
                new CommandResult("Viewing application for Alice Pauline.", ViewRequest.application(application)),
                expectedModel);
    }

    @Test
    public void execute_filteredIndex_selectsDisplayedApplicationAndKeepsFilter() {
        Job job = new Job("Janitor");
        model.addJob(job);
        model.addApplication(new Application(ALICE, job, Application.Status.PENDING));
        Application application = new Application(BENSON, job, Application.Status.REJECT);
        model.addApplication(application);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showApplicantAtIndex(model, INDEX_SECOND);
        showApplicantAtIndex(expectedModel, INDEX_SECOND);

        assertCommandSuccess(new ViewApplicationCommand(INDEX_FIRST), model,
                new CommandResult("Viewing application for Benson Meier.", ViewRequest.application(application)),
                expectedModel);
    }

    @Test
    public void execute_noIndex_keepsApplicantFilter() {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showApplicantAtIndex(model, INDEX_SECOND);
        showApplicantAtIndex(expectedModel, INDEX_SECOND);

        assertCommandSuccess(new ViewApplicationCommand(), model,
                new CommandResult(ViewApplicationCommand.MESSAGE_SUCCESS, ViewRequest.applications()), expectedModel);
    }

    @Test
    public void execute_missingApplication_throwsCommandException() {
        assertCommandFailure(new ViewApplicationCommand(INDEX_FIRST), model,
                ViewApplicationCommand.MESSAGE_MISSING_APPLICATION);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        int size = model.getFilteredApplicantList().size();
        assertCommandFailure(new ViewApplicationCommand(Index.fromOneBased(size + 1)), model,
                String.format(ViewCommand.MESSAGE_INVALID_INDEX, size));
    }

    @Test
    public void execute_invalidFilteredIndex_throwsCommandException() {
        showApplicantAtIndex(model, INDEX_SECOND);
        assertCommandFailure(new ViewApplicationCommand(INDEX_SECOND), model,
                String.format(ViewCommand.MESSAGE_INVALID_INDEX, 1));
    }

    @Test
    public void execute_emptyApplicantList_throwsCommandException() {
        assertCommandFailure(new ViewApplicationCommand(INDEX_FIRST), new ModelManager(),
                ViewCommand.MESSAGE_EMPTY_LIST);
        model.updateFilteredApplicantList(applicant -> false);
        assertCommandFailure(new ViewApplicationCommand(INDEX_FIRST), model, ViewCommand.MESSAGE_EMPTY_LIST);
    }

    @Test
    public void equalsAndHashCode() {
        ViewApplicationCommand command = new ViewApplicationCommand(INDEX_FIRST);
        ViewApplicationCommand sameCommand = new ViewApplicationCommand(Index.fromOneBased(1));
        assertEquals(command, command);
        assertEquals(command, sameCommand);
        assertEquals(command.hashCode(), sameCommand.hashCode());
        assertEquals(new ViewApplicationCommand(), new ViewApplicationCommand());
        assertNotEquals(command, new ViewApplicationCommand(INDEX_SECOND));
        assertNotEquals(command, new ViewApplicationCommand());
        assertNotEquals(command, null);
        assertNotEquals(command, new ListCommand());
    }
}
