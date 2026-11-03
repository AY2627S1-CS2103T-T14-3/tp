package seedu.address.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.ViewApplicationCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.commands.ViewJobCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;

    /** Records are in memory but have not been written. */
    private boolean hasUnsavedChanges;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser();
    }

    /**
     * {@inheritDoc}
     * Remembers the records from before a command that changes them.
     * An undo command is not remembered, so the next undo steps further back.
     * Saves after stored records change. A failed save is written on a later command.
     */
    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        Command command = addressBookParser.parseCommand(commandText);
        ReadOnlyAddressBook stateBeforeCommand = new AddressBook(model.getAddressBook());
        CommandResult commandResult = command.execute(model);
        boolean recordsChanged = storedRecordsChanged(stateBeforeCommand, model.getAddressBook());
        if (!(command instanceof UndoCommand) && recordsChanged) {
            model.rememberStateForUndo(stateBeforeCommand);
        }
        if (!recordsChanged && !hasUnsavedChanges) {
            return commandResult;
        }
        hasUnsavedChanges = true;

        try {
            storage.saveAddressBook(model.getAddressBook());
            hasUnsavedChanges = false;
        } catch (IOException exception) {
            if (keepsResultAfterSaveFailure(command)) {
                return commandResult;
            }
            String format = exception instanceof AccessDeniedException
                    ? FILE_OPS_PERMISSION_ERROR_FORMAT
                    : FILE_OPS_ERROR_FORMAT;
            throw new CommandException(String.format(format, exception.getMessage()), exception);
        }

        return commandResult;
    }

    /**
     * Returns true if {@code command} should still report its result when saving fails.
     */
    private static boolean keepsResultAfterSaveFailure(Command command) {
        return command instanceof HelpCommand
                || command instanceof ViewCommand
                || command instanceof ViewJobCommand
                || command instanceof ViewApplicationCommand;
    }

    /**
     * Returns true if the stored applicants, jobs, or applications differ.
     * Job equality compares the role only, so a description edit is checked separately.
     */
    private static boolean storedRecordsChanged(ReadOnlyAddressBook before, ReadOnlyAddressBook after) {
        if (!before.equals(after)) {
            return true;
        }
        for (int i = 0; i < before.getJobList().size(); i++) {
            String beforeDescription = before.getJobList().get(i).getJobDescription();
            String afterDescription = after.getJobList().get(i).getJobDescription();
            if (!beforeDescription.equals(afterDescription)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public FilteredList<Applicant> getFilteredApplicantList() {
        return model.getFilteredApplicantList();
    }

    @Override
    public ObservableList<Applicant> getApplicantList() {
        return model.getApplicantList();
    }

    @Override
    public ObservableList<Application> getApplicationList() {
        return model.getApplicationList();
    }

    @Override
    public ObservableList<Job> getJobList() {
        return model.getJobList();
    }

    @Override
    public FilteredList<Job> getFilteredJobList() {
        return model.getFilteredJobList();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
