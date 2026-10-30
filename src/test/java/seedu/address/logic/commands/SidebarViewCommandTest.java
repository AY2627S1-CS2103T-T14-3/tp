package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalApplicants.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

/** Tests that sidebar toggle commands return the requested view without changing model data. */
public class SidebarViewCommandTest {
    @Test
    public void viewApplicantCommand_requestsApplicantsView() throws Exception {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        assertEquals(new CommandResult(ViewApplicantCommand.MESSAGE_SUCCESS,
                ViewRequest.applicants()), new ViewApplicantCommand().execute(model));
    }

    @Test
    public void viewApplicationCommand_requestsApplicationsView() throws Exception {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        assertEquals(new CommandResult(ViewApplicationCommand.MESSAGE_SUCCESS,
                ViewRequest.applications()), new ViewApplicationCommand().execute(model));
    }
}
