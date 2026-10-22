package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

/** Tests that sidebar toggle commands return the requested view without changing model data. */
public class SidebarViewCommandTest {
    @Test
    public void applicantsCommand_requestsApplicantsView() throws Exception {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        assertEquals(new CommandResult(ApplicantsCommand.MESSAGE_SUCCESS,
                CommandResult.SidebarView.APPLICANTS), new ApplicantsCommand().execute(model));
    }

    @Test
    public void applicationsCommand_requestsApplicationsView() throws Exception {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        assertEquals(new CommandResult(ApplicationsCommand.MESSAGE_SUCCESS,
                CommandResult.SidebarView.APPLICATIONS), new ApplicationsCommand().execute(model));
    }
}
