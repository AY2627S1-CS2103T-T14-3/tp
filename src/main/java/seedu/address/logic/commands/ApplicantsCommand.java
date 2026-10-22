package seedu.address.logic.commands;

/** Switches the sidebar to the Applicants tab. */
public class ApplicantsCommand extends Command {
    public static final String COMMAND_WORD = "applicants";
    public static final String MESSAGE_SUCCESS = "Showing applicants.";

    @Override
    public CommandResult execute(seedu.address.model.Model model) {
        return new CommandResult(MESSAGE_SUCCESS, ViewRequest.applicants());
    }
}
