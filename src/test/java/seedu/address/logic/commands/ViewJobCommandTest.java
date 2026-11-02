package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalApplicants.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.application.Job;
import seedu.address.model.application.JobDescriptionContainsKeywordPredicate;

/** Tests listing, filtering, and selecting jobs. */
public class ViewJobCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_showsJobsWithoutChangingThem() {
        model.addJob(new Job("Software Engineer"));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new ViewJobCommand(), model,
                new CommandResult(ViewJobCommand.MESSAGE_SUCCESS, ViewRequest.jobs()), expectedModel);
    }

    @Test
    public void execute_keyword_listsMatchingJobs() {
        model.addJob(new Job("Janitor"));
        model.addJob(new Job("Data Analyst"));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredJobList(new JobDescriptionContainsKeywordPredicate("jan"));

        String expectedMessage = String.format(ViewJobCommand.MESSAGE_JOBS_LISTED, 1);
        assertCommandSuccess(new ViewJobCommand("jan"), model,
                new CommandResult(expectedMessage, ViewRequest.jobs()), expectedModel);
        assertEquals(List.of(new Job("Janitor")), model.getFilteredJobList());
    }

    @Test
    public void execute_index_selectsJobWithoutChangingModel() {
        model.addJob(new Job("Janitor"));
        Job job = new Job("Data Analyst");
        model.addJob(job);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new ViewJobCommand(INDEX_SECOND), model,
                new CommandResult("Viewing job: Data Analyst", ViewRequest.job(job)), expectedModel);
    }

    @Test
    public void execute_filteredIndex_selectsDisplayedJobAndKeepsFilter() {
        model.addJob(new Job("Janitor"));
        Job job = new Job("Data Analyst");
        model.addJob(job);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        model.updateFilteredJobList(new JobDescriptionContainsKeywordPredicate("data"));
        expectedModel.updateFilteredJobList(new JobDescriptionContainsKeywordPredicate("data"));

        assertCommandSuccess(new ViewJobCommand(INDEX_FIRST), model,
                new CommandResult("Viewing job: Data Analyst", ViewRequest.job(job)), expectedModel);
        assertEquals(List.of(job), model.getFilteredJobList());
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        model.addJob(new Job("Janitor"));
        assertCommandFailure(new ViewJobCommand(INDEX_SECOND), model,
                String.format(ViewJobCommand.MESSAGE_INVALID_INDEX, 1));
    }

    @Test
    public void execute_invalidFilteredIndex_keepsFilter() {
        Job job = new Job("Janitor");
        model.addJob(job);
        model.addJob(new Job("Data Analyst"));
        model.updateFilteredJobList(new JobDescriptionContainsKeywordPredicate("jan"));

        assertCommandFailure(new ViewJobCommand(INDEX_SECOND), model,
                String.format(ViewJobCommand.MESSAGE_INVALID_INDEX, 1));
        assertEquals(List.of(job), model.getFilteredJobList());
    }

    @Test
    public void execute_emptyList_throwsCommandException() {
        assertCommandFailure(new ViewJobCommand(INDEX_FIRST), model, ViewJobCommand.MESSAGE_EMPTY_LIST);
        model.addJob(new Job("Janitor"));
        model.updateFilteredJobList(new JobDescriptionContainsKeywordPredicate("data"));
        assertCommandFailure(new ViewJobCommand(INDEX_FIRST), model, ViewJobCommand.MESSAGE_EMPTY_LIST);
        assertEquals(List.of(), model.getFilteredJobList());
    }

    @Test
    public void execute_noIndex_resetsJobFilter() {
        model.addJob(new Job("Janitor"));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        model.updateFilteredJobList(new JobDescriptionContainsKeywordPredicate("data"));

        assertCommandSuccess(new ViewJobCommand(), model,
                new CommandResult(ViewJobCommand.MESSAGE_SUCCESS, ViewRequest.jobs()), expectedModel);
    }

    @Test
    public void equalsAndHashCode() {
        ViewJobCommand command = new ViewJobCommand(INDEX_FIRST);
        ViewJobCommand sameCommand = new ViewJobCommand(Index.fromOneBased(1));
        assertEquals(command, command);
        assertEquals(command, sameCommand);
        assertEquals(command.hashCode(), sameCommand.hashCode());
        assertEquals(new ViewJobCommand(), new ViewJobCommand());
        assertEquals(new ViewJobCommand("jan"), new ViewJobCommand("jan"));
        assertNotEquals(command, new ViewJobCommand(INDEX_SECOND));
        assertNotEquals(command, new ViewJobCommand());
        assertNotEquals(command, new ViewJobCommand("jan"));
        assertNotEquals(new ViewJobCommand("jan"), new ViewJobCommand("data"));
        assertNotEquals(command, null);
        assertNotEquals(command, new ListCommand());
    }
}
