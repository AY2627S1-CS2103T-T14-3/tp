package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;

public class ByeCommandTest {
    @Test
    public void execute_bye_exitsWithoutFeedbackOrModelChanges() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();
        assertCommandSuccess(new ByeCommand(), model,
                new CommandResult("", false, true), expectedModel);
    }
}
