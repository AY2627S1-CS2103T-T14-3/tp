package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalApplicants.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.application.Job;
import seedu.address.model.application.JobDescriptionContainsKeywordPredicate;

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
}
