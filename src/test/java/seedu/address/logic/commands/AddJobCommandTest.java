package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.application.Job;

public class AddJobCommandTest {

    private static final Job ENGINEER = new Job("Software Engineer");

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullJob_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddJobCommand(null));
    }

    @Test
    public void execute_newJob_success() {
        AddJobCommand command = new AddJobCommand(ENGINEER);
        String expectedMessage = String.format(AddJobCommand.MESSAGE_SUCCESS, ENGINEER);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addJob(ENGINEER);

        assertCommandSuccess(command, model, new CommandResult(expectedMessage, ENGINEER), expectedModel);
        assertTrue(model.hasJob(ENGINEER));
    }

    @Test
    public void execute_duplicateJob_throwsCommandException() {
        model.addJob(ENGINEER);
        AddJobCommand command = new AddJobCommand(new Job("Software Engineer"));

        assertCommandFailure(command, model, AddJobCommand.MESSAGE_DUPLICATE_JOB);
    }

    @Test
    public void equals() {
        AddJobCommand addEngineer = new AddJobCommand(ENGINEER);
        AddJobCommand addAnalyst = new AddJobCommand(new Job("Data Analyst"));

        assertTrue(addEngineer.equals(addEngineer));
        assertTrue(addEngineer.equals(new AddJobCommand(new Job("Software Engineer"))));
        assertFalse(addEngineer.equals(addAnalyst));
        assertFalse(addEngineer.equals(null));
        assertFalse(addEngineer.equals(1));
    }

    @Test
    public void toStringMethod() {
        AddJobCommand command = new AddJobCommand(ENGINEER);
        String expected = AddJobCommand.class.getCanonicalName() + "{job=" + ENGINEER + "}";
        assertEquals(expected, command.toString());
    }
}
