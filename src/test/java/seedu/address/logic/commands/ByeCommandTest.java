package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.model.ModelManager;

public class ByeCommandTest {
    @Test
    public void execute_bye_exitsWithoutFeedbackOrModelChanges() {
        assertCommandSuccess(new ByeCommand(), new ModelManager(),
                new CommandResult("", false, true), new ModelManager());
    }
}
