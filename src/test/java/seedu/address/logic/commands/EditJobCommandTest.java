package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.BENSON;
import static seedu.address.testutil.TypicalApplicants.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditJobCommand.EditJobDescriptor;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;
import seedu.address.testutil.EditJobDescriptorBuilder;

/**
 * Checks job edits against displayed indices, uniqueness, and application job references.
 */
public class EditJobCommandTest {

    private static final Job ENGINEER = new Job("Software Engineer");
    private static final Job ANALYST = new Job("Data Analyst");
    private static final Job DESIGNER = new Job("Product Designer");

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.addJob(ENGINEER);
        model.addJob(ANALYST);
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        EditJobDescriptor designerDescriptor = new EditJobDescriptorBuilder(DESIGNER).build();

        assertThrows(NullPointerException.class, () -> new EditJobCommand(null, designerDescriptor));
        assertThrows(NullPointerException.class, () -> new EditJobCommand(INDEX_FIRST, null));
    }

    @Test
    public void execute_validIndex_preservesOrderAndApplications() {
        EditJobDescriptor designerDescriptor = new EditJobDescriptorBuilder(DESIGNER).build();

        Application application = new Application(ALICE, ENGINEER, Application.Status.ACCEPT);
        model.addApplication(application);
        Application unrelatedApplication = new Application(BENSON, ANALYST, Application.Status.REJECT);
        model.addApplication(unrelatedApplication);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setJob(ENGINEER, DESIGNER);
        Application editedApplication = new Application(ALICE, DESIGNER, Application.Status.ACCEPT);

        assertCommandSuccess(new EditJobCommand(INDEX_FIRST, designerDescriptor), model,
                new CommandResult(String.format(EditJobCommand.MESSAGE_EDIT_JOB_SUCCESS, Messages.format(DESIGNER)),
                        ViewRequest.job(DESIGNER)),
                expectedModel);
        assertEquals(List.of(DESIGNER, ANALYST), model.getJobList());
        assertEquals(editedApplication, model.getApplication(ALICE).orElseThrow());
        assertEquals(unrelatedApplication, model.getApplication(BENSON).orElseThrow());
    }

    @Test
    public void execute_filteredList_editsDisplayedJobAndShowsAllJobs() {
        EditJobDescriptor designerDescriptor = new EditJobDescriptorBuilder(DESIGNER).build();

        model.updateFilteredJobList(job -> job.equals(ANALYST));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setJob(ANALYST, DESIGNER);

        assertCommandSuccess(new EditJobCommand(INDEX_FIRST, designerDescriptor), model,
                new CommandResult(String.format(EditJobCommand.MESSAGE_EDIT_JOB_SUCCESS, Messages.format(DESIGNER)),
                        ViewRequest.job(DESIGNER)),
                expectedModel);
        assertEquals(List.of(ENGINEER, DESIGNER), model.getFilteredJobList());
    }

    @Test
    public void execute_duplicateOutsideFilter_throwsCommandExceptionAndPreservesFilter() {
        EditJobDescriptor engineerDescriptor = new EditJobDescriptorBuilder(ENGINEER).build();

        model.updateFilteredJobList(job -> job.equals(ANALYST));

        assertCommandFailure(new EditJobCommand(INDEX_FIRST, engineerDescriptor), model,
                EditJobCommand.MESSAGE_DUPLICATE_JOB);
        assertEquals(List.of(ANALYST), model.getFilteredJobList());
    }

    @Test
    public void execute_unchangedDescription_success() {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        EditJobDescriptor engineerDescriptor = new EditJobDescriptorBuilder(ENGINEER).build();

        assertCommandSuccess(new EditJobCommand(INDEX_FIRST, engineerDescriptor), model,
                new CommandResult(String.format(EditJobCommand.MESSAGE_EDIT_JOB_SUCCESS, Messages.format(ENGINEER)),
                        ViewRequest.job(ENGINEER)),
                expectedModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        EditJobDescriptor designerDescriptor = new EditJobDescriptorBuilder(DESIGNER).build();

        assertCommandFailure(new EditJobCommand(Index.fromOneBased(3), designerDescriptor), model,
                String.format(EditJobCommand.MESSAGE_INVALID_INDEX, 2));

        model.updateFilteredJobList(job -> job.equals(ANALYST));
        assertCommandFailure(new EditJobCommand(INDEX_SECOND, designerDescriptor), model,
                String.format(EditJobCommand.MESSAGE_INVALID_INDEX, 1));
        assertEquals(List.of(ANALYST), model.getFilteredJobList());
    }

    @Test
    public void execute_emptyList_throwsCommandException() {
        EditJobDescriptor designerDescriptor = new EditJobDescriptorBuilder(DESIGNER).build();

        assertCommandFailure(new EditJobCommand(INDEX_FIRST, designerDescriptor), new ModelManager(),
                EditJobCommand.MESSAGE_EMPTY_LIST);

        model.updateFilteredJobList(job -> false);
        assertCommandFailure(new EditJobCommand(INDEX_FIRST, designerDescriptor), model,
                EditJobCommand.MESSAGE_EMPTY_LIST);
        assertTrue(model.getFilteredJobList().isEmpty());
    }

    @Test
    public void equals() {
        EditJobDescriptor designerDescriptor = new EditJobDescriptorBuilder(DESIGNER).build();
        EditJobDescriptor designerDescriptor2 = new EditJobDescriptorBuilder(new Job("Product Designer")).build();
        EditJobDescriptor analystDescriptor = new EditJobDescriptorBuilder(ANALYST).build();

        EditJobCommand editFirst = new EditJobCommand(INDEX_FIRST, designerDescriptor);

        assertTrue(editFirst.equals(editFirst));
        assertTrue(editFirst.equals(new EditJobCommand(INDEX_FIRST, designerDescriptor2)));
        assertFalse(editFirst.equals(new EditJobCommand(INDEX_SECOND, designerDescriptor)));
        assertFalse(editFirst.equals(new EditJobCommand(INDEX_FIRST, analystDescriptor)));
        assertFalse(editFirst.equals(null));
        assertFalse(editFirst.equals(1));
    }
}
