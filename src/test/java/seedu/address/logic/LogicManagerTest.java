package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.logic.Messages.MESSAGE_INVALID_APPLICANT_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.AMY;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddApplicationCommand;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteApplicationCommand;
import seedu.address.logic.commands.DeleteJobCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditJobCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.ViewApplicationCommand;
import seedu.address.logic.commands.ViewJobCommand;
import seedu.address.logic.commands.ViewRequest;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.ApplicantBuilder;
import seedu.address.testutil.ApplicantUtil;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_APPLICANT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_undoWithoutStoredChange_throwsCommandException() throws Exception {
        logic.execute("list");
        assertCommandException("undo", UndoCommand.MESSAGE_FAILURE);
    }

    @Test
    public void execute_undoAfterDelete_restoresApplicantAndJob() throws Exception {
        logic.execute("add -n Amy Bee -p 11111111 -e amy@example.com");
        logic.execute("addj -j Engineer");
        logic.execute("delete 1");
        assertEquals(0, model.getFilteredApplicantList().size());

        CommandResult undoApplicant = logic.execute("undo");
        assertEquals(UndoCommand.MESSAGE_SUCCESS, undoApplicant.getFeedbackToUser());
        assertEquals("Amy Bee", model.getFilteredApplicantList().get(0).getName().fullName);
        assertEquals(1, model.getJobList().size());
        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(model.getAddressBook(), savedStorage.readAddressBook().orElseThrow());

        logic.execute("deletej 1");
        assertEquals(0, model.getJobList().size());
        logic.execute("undo");
        assertEquals("Engineer", model.getJobList().get(0).getJobRole());
    }

    @Test
    public void execute_twoChanges_undoRestoresOneAtATime() throws Exception {
        logic.execute("add -n Amy Bee -p 11111111 -e amy@example.com");
        logic.execute("add -n Bob Choo -p 22222222 -e bob@example.com");

        logic.execute("undo");
        assertEquals(1, model.getFilteredApplicantList().size());
        assertEquals("Amy Bee", model.getFilteredApplicantList().get(0).getName().fullName);

        logic.execute("undo");
        assertEquals(0, model.getFilteredApplicantList().size());
        assertCommandException("undo", UndoCommand.MESSAGE_FAILURE);
    }

    @Test
    public void execute_undoAfterDescriptionEdit_restoresDescription() throws Exception {
        logic.execute("addj -j Engineer -d Original");
        logic.execute("editj 1 -d Revised");
        assertEquals("Revised", model.getJobList().get(0).getJobDescription());

        logic.execute("undo");
        assertEquals(1, model.getJobList().size());
        assertEquals("Engineer", model.getJobList().get(0).getJobRole());
        assertEquals("Original", model.getJobList().get(0).getJobDescription());
    }

    @Test
    public void execute_failedDelete_doesNotReplaceUndoState() throws Exception {
        logic.execute("add -n Amy Bee -p 11111111 -e amy@example.com");
        assertCommandException("delete 5", MESSAGE_INVALID_APPLICANT_DISPLAYED_INDEX);

        logic.execute("undo");
        assertEquals(0, model.getFilteredApplicantList().size());
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_addWithHyphenOptions_savesApplicant() throws Exception {
        String addCommand = "add -n Amy Bee -p 11111111 -e amy@example.com";
        Model expectedModel = new ModelManager();
        expectedModel.addApplicant(AMY);

        assertCommandSuccess(addCommand, String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(AMY)),
                expectedModel);

        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(expectedModel.getAddressBook(), savedStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_addCaseVariantDuplicate_throwsCommandException() throws Exception {
        logic.execute("add -n Amy Bee -p 11111111 -e amy@example.com");
        for (String fields : List.of("-n amy bee -e other@example.com", "-n AMY BEE -e other@example.com",
                "-n Other Name -e AMY@example.com", "-n Other Name -e amy@EXAMPLE.COM")) {
            assertCommandException("add -p 91234567 " + fields, AddCommand.MESSAGE_DUPLICATE_APPLICANT);
        }
    }

    @Test
    public void execute_editCaseVariantDuplicateHiddenByFilter_throwsCommandException() throws Exception {
        logic.execute("add -n Amy Bee -p 11111111 -e amy@example.com");
        logic.execute("add -n Bob Choo -p 91234567 -e bob@example.com");
        logic.execute("find Bob");
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredApplicantList(applicant -> applicant.getName().fullName.equals("Bob Choo"));

        for (String fields : List.of("-n AMY BEE", "-e AMY@example.com", "-e amy@EXAMPLE.COM")) {
            assertCommandFailure("edit 1 " + fields, CommandException.class,
                    EditCommand.MESSAGE_DUPLICATE_APPLICANT, expectedModel);
        }
    }

    @Test
    public void execute_editOwnCapitalization_preservesApplicationAndSavesSpelling() throws Exception {
        logic.execute("add -n Amy Bee -p 11111111 -e amy@example.com");
        logic.execute("addj -j Engineer");
        logic.execute("addp 1 -j Engineer -s accept");
        logic.execute("edit 1 -n AMY BEE -e Amy@Example.com");
        Applicant editedApplicant = new ApplicantBuilder(AMY).withName("AMY BEE")
                .withEmail("Amy@Example.com").build();

        assertEquals(List.of(editedApplicant), model.getAddressBook().getApplicantList());
        Application editedApplication =
                new Application(editedApplicant, new Job("Engineer"), Application.Status.ACCEPT);
        assertEquals(editedApplication, model.getApplication(editedApplicant).orElseThrow());
        assertEquals(new CommandResult(String.format(ViewApplicationCommand.MESSAGE_SELECTED,
                editedApplicant.getName()), ViewRequest.application(editedApplication)),
                logic.execute("viewp 1"));
        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(model.getAddressBook(), savedStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_editWithHyphenOptions_savesApplicantAndPreservesPhone() throws Exception {
        model.addApplicant(AMY);
        Applicant editedApplicant = new ApplicantBuilder(AMY).withName("John Doe")
                .withEmail("john@example.com").build();
        Model expectedModel = new ModelManager();
        expectedModel.addApplicant(editedApplicant);

        assertCommandSuccess("edit 1 -n John Doe -e john@example.com",
                String.format(EditCommand.MESSAGE_EDIT_APPLICANT_SUCCESS, Messages.format(editedApplicant)),
                expectedModel);

        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(expectedModel.getAddressBook(), savedStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_editPhone_savesPhoneAndPreservesOtherFields() throws Exception {
        model.addApplicant(AMY);
        Applicant editedApplicant = new ApplicantBuilder(AMY).withPhone("91234567").build();
        Model expectedModel = new ModelManager();
        expectedModel.addApplicant(editedApplicant);

        assertCommandSuccess("edit 1 -p 91234567",
                String.format(EditCommand.MESSAGE_EDIT_APPLICANT_SUCCESS, Messages.format(editedApplicant)),
                expectedModel);

        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(expectedModel.getAddressBook(), savedStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_nonWhitespaceControls_rejectsWithoutChangingData() throws Exception {
        logic.execute("add -n Amy Bee -p 11111111 -e amy@example.com");
        logic.execute("addj -j Engineer");
        logic.execute("addp 1 -j Engineer -s accept");
        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        ReadOnlyAddressBook original = savedStorage.readAddressBook().orElseThrow();

        for (int codePoint : List.of(0, 1, 8, 14, 27, 127, 159)) {
            String control = Character.toString(codePoint);
            for (String input : List.of("editj 1 -j " + control, "find " + control,
                    "set " + control + "1 -s accept", "addj -j Other -d before" + control + "after")) {
                assertThrows(ParseException.class, () -> logic.execute(input));
                assertEquals(original, model.getAddressBook());
            }
        }
        assertEquals(original, savedStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_unicodePaddedValues_normalizesAcrossWorkflows() throws Exception {
        logic.execute("\u2003add -n \u2003Amy  Bee\u2009 -p \u200311111111\u2009 "
                + "-e \u3000Amy@Example.com\u2003");
        assertEquals("Amy  Bee", model.getFilteredApplicantList().getFirst().getName().fullName);
        logic.execute("addj -j \u2003Engineer\u2009 -d \u3000Research position\u2003");
        logic.execute("addp \u20031\u2009 -j \u2003Engineer\u2009 -s \u3000pending\u2003");
        logic.execute("set \u20031\u2009 -s \u2003accept\u2009");
        logic.execute("edit \u20031\u2009 -n \u3000Amy  Updated\u2003 "
                + "-p \u200391234567\u2009 -e \u2003new@example.com\u2009");
        logic.execute("find Missing \u2003 \u2009 Amy\u2009");
        logic.execute("viewj -f \u2003Engineer\u2009");

        Applicant expected = new ApplicantBuilder(AMY).withName("Amy  Updated")
                .withPhone("91234567").withEmail("new@example.com").build();
        assertEquals(List.of(expected), model.getFilteredApplicantList());
        Application application = model.getApplication(expected).orElseThrow();
        assertEquals(expected, application.getApplicant());
        assertEquals(Application.Status.ACCEPT, application.getStatus());
        assertEquals("Research position", application.getJob().getJobDescription());
        assertEquals(1, model.getFilteredJobList().size());
        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(model.getAddressBook(), savedStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_addJobThenApplicationWithUnicodeWhitespace_savesApplication() throws Exception {
        logic.execute("add -n Amy Bee -p 11111111 -e amy@example.com");
        String role = "\u2003Senior  Engineer\u2003";
        logic.execute("addj -j " + role + " -d Research position");
        assertCommandException("addp 1 -j senior  Engineer", AddApplicationCommand.MESSAGE_INVALID_JOB);
        assertCommandException("addp 1 -j Senior Engineer", AddApplicationCommand.MESSAGE_INVALID_JOB);
        logic.execute("addp 1 -j " + role + " -s accept");

        Application application = model.getApplication(AMY).orElseThrow();
        assertEquals("Senior  Engineer", application.getJob().getJobRole());
        assertEquals("Research position", application.getJob().getJobDescription());
        assertEquals(Application.Status.ACCEPT, application.getStatus());
        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(model.getAddressBook(), savedStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_editJobToUnicodeWhitespace_rejectsWithoutChangingData() throws Exception {
        logic.execute("add -n Amy Bee -p 11111111 -e amy@example.com");
        logic.execute("addj -j Engineer -d Research position");
        logic.execute("addp 1 -j Engineer -s accept");
        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        ReadOnlyAddressBook original = savedStorage.readAddressBook().orElseThrow();

        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditJobCommand.MESSAGE_USAGE);
        assertParseException("editj 1 -j \u2003", expected);
        assertParseException("editj 1 -j \u2003 -d Changed description", expected);
        for (String input : List.of("edit 1 -n \u2003", "edit 1 -p \u2003", "edit 1 -e \u2003",
                "add -n \u2003 -p 91234567 -e other@example.com",
                "addp 1 -j Engineer -s \u2003", "set 1 -s \u2003")) {
            assertThrows(ParseException.class, () -> logic.execute(input));
        }
        assertEquals(original, model.getAddressBook());
        assertEquals(original, savedStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_editJob_savesEditedJobAndUpdatesApplicationReference() throws Exception {
        logic.execute("add -n Amy Bee -p 11111111 -e amy@example.com");
        logic.execute("addj -j Software Engineer");
        logic.execute("addp 1 -j Software Engineer");
        CommandResult result = logic.execute("editj 1 -job Product Designer");

        Job editedJob = new Job("Product Designer");
        Model expectedModel = new ModelManager();
        expectedModel.addApplicant(AMY);
        expectedModel.addJob(new Job("Software Engineer"));
        expectedModel.addApplication(new Application(AMY, new Job("Software Engineer"), Application.Status.PENDING));
        expectedModel.setJob(new Job("Software Engineer"), editedJob);

        assertEquals(
                new CommandResult(String.format(EditJobCommand.MESSAGE_EDIT_JOB_SUCCESS, Messages.format(editedJob)),
                ViewRequest.job(editedJob)), result);
        assertEquals(expectedModel, model);
        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(expectedModel.getAddressBook(), savedStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_editJobWithThreeApplications_savesReloadableReferences() throws Exception {
        logic.execute("add -n Alice -p 91234567 -e alice@example.com");
        logic.execute("add -n Bob -p 91234568 -e bob@example.com");
        logic.execute("add -n Carol -p 91234569 -e carol@example.com");
        logic.execute("add -n Dave -p 91234570 -e dave@example.com");
        logic.execute("add -n Eve -p 91234571 -e eve@example.com");
        logic.execute("addj -j Janitor -d Old description");
        logic.execute("addj -j janitor -d Other description");
        logic.execute("addp 1 -j Janitor");
        logic.execute("addp 2 -j Janitor");
        logic.execute("addp 3 -j Janitor");
        logic.execute("addp 4 -j janitor -s reject");
        List<Applicant> applicants = List.copyOf(model.getAddressBook().getApplicantList());
        Applicant applicantWithoutApplication = applicants.getLast();
        Application unrelatedApplication = model.getApplicationList().getLast();

        logic.execute("editj 1 -j Cleaner");

        Job editedJob = new Job("Cleaner", "Old description");
        assertEquals(List.of(editedJob, unrelatedApplication.getJob()), model.getJobList());
        assertEquals(applicants, model.getAddressBook().getApplicantList());
        assertFalse(model.hasApplication(applicantWithoutApplication));
        assertEquals(4, model.getApplicationList().size());
        for (int i = 0; i < 3; i++) {
            Application application = model.getApplicationList().get(i);
            assertEquals(applicants.get(i), application.getApplicant());
            assertEquals(editedJob, application.getJob());
            assertEquals(editedJob.getJobDescription(), application.getJob().getJobDescription());
            assertEquals(Application.Status.PENDING, application.getStatus());
        }
        assertEquals(unrelatedApplication, model.getApplicationList().getLast());
        assertEquals("Other description", model.getApplicationList().getLast().getJob().getJobDescription());
        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        ReadOnlyAddressBook reloaded = savedStorage.readAddressBook().orElseThrow();
        assertEquals(model.getAddressBook(), reloaded);
        assertEquals(applicants, reloaded.getApplicantList());
        assertEquals(4, reloaded.getApplicationList().size());
        assertFalse(reloaded.getApplicationList().stream()
                .anyMatch(application -> application.isForApplicant(applicantWithoutApplication)));
        for (int i = 0; i < 3; i++) {
            assertEquals(editedJob.getJobDescription(),
                    reloaded.getApplicationList().get(i).getJob().getJobDescription());
        }
        assertEquals(unrelatedApplication, reloaded.getApplicationList().getLast());
        assertEquals("Other description", reloaded.getApplicationList().getLast().getJob().getJobDescription());

        CommandResult deletion = logic.execute("deletej 1");
        assertEquals(String.format(DeleteJobCommand.MESSAGE_DELETE_JOB_SUCCESS, "Cleaner")
                + "\nWarning: Applications deleted with this job: 3.", deletion.getFeedbackToUser());
        assertEquals(List.of(unrelatedApplication), model.getApplicationList());
        assertEquals(applicants, model.getAddressBook().getApplicantList());
        assertEquals(model.getAddressBook(), savedStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_deleteJob_savesCascadeAndKeepsApplicant() throws Exception {
        logic.execute("add -n Amy Bee -p 11111111 -e amy@example.com");
        logic.execute("addj -j Software Engineer");
        logic.execute("addp 1 -j Software Engineer");

        CommandResult result = logic.execute("deletej 1");

        Model expectedModel = new ModelManager();
        expectedModel.addApplicant(AMY);
        assertEquals(new CommandResult(String.format(
                DeleteJobCommand.MESSAGE_DELETE_JOB_SUCCESS, "Software Engineer")
                + "\nWarning: Applications deleted with this job: 1.", ViewRequest.jobs()), result);
        assertEquals(expectedModel, model);
        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        ReadOnlyAddressBook reloaded = savedStorage.readAddressBook().orElseThrow();
        assertEquals(expectedModel.getAddressBook(), reloaded);
    }

    @Test
    public void execute_deleteApplication_savesDeletionAndAllowsNewApplication() throws Exception {
        logic.execute("add -n Amy Bee -p 11111111 -e amy@example.com");
        logic.execute("addj -j Software Engineer");
        logic.execute("addp 1 -j Software Engineer");

        CommandResult result = logic.execute("deletep 1");

        Model expectedModel = new ModelManager();
        expectedModel.addApplicant(AMY);
        expectedModel.addJob(new Job("Software Engineer"));
        assertEquals(new CommandResult(String.format(
                DeleteApplicationCommand.MESSAGE_SUCCESS, AMY.getName(), AMY.getEmail()),
                ViewRequest.applicant(AMY)), result);
        assertEquals(expectedModel, model);
        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(expectedModel.getAddressBook(), savedStorage.readAddressBook().orElseThrow());

        logic.execute("addj -j Product Designer");
        expectedModel.addJob(new Job("Product Designer"));
        logic.execute("addp 1 -j Product Designer -s accept");
        expectedModel.addApplication(new Application(AMY, new Job("Product Designer"), Application.Status.ACCEPT));
        assertEquals(expectedModel.getAddressBook(), savedStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_saveFailure_retryPersistsEditedPhone() throws Exception {
        logic.execute("add -n Amy Bee -p 11111111 -e amy@example.com");
        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        String originalPhone = savedStorage.readAddressBook().orElseThrow()
                .getApplicantList().get(0).getPhone().value;
        boolean[] failNextSave = {true};
        JsonAddressBookStorage flakyStorage = new JsonAddressBookStorage(
                temporaryFolder.resolve("addressBook.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                if (failNextSave[0]) {
                    failNextSave[0] = false;
                    throw DUMMY_AD_EXCEPTION;
                }
                super.saveAddressBook(addressBook);
            }
        };
        logic = new LogicManager(model, new StorageManager(flakyStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"))));

        String editCommand = "edit 1 -p 99999999";
        assertThrows(CommandException.class, String.format(LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT,
                DUMMY_AD_EXCEPTION.getMessage()), () -> logic.execute(editCommand));
        assertEquals("99999999", model.getFilteredApplicantList().get(0).getPhone().value);
        assertEquals(originalPhone, savedStorage.readAddressBook().orElseThrow()
                .getApplicantList().get(0).getPhone().value);

        logic.execute(editCommand);
        assertEquals("99999999", savedStorage.readAddressBook().orElseThrow()
                .getApplicantList().get(0).getPhone().value);
    }

    @Test
    public void execute_saveFailure_byePersistsEditedPhone() throws Exception {
        logic.execute("add -n Amy Bee -p 11111111 -e amy@example.com");
        boolean[] failNextSave = {true};
        JsonAddressBookStorage flakyStorage = new JsonAddressBookStorage(
                temporaryFolder.resolve("addressBook.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                if (failNextSave[0]) {
                    failNextSave[0] = false;
                    throw DUMMY_AD_EXCEPTION;
                }
                super.saveAddressBook(addressBook);
            }
        };
        logic = new LogicManager(model, new StorageManager(flakyStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"))));

        assertThrows(CommandException.class, () -> logic.execute("edit 1 -p 99999999"));
        assertEquals(new CommandResult("", false, true), logic.execute("bye"));
        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals("99999999", savedStorage.readAddressBook().orElseThrow()
                .getApplicantList().get(0).getPhone().value);
    }

    @Test
    public void execute_persistentSaveFailure_helpAndViewStillReturn() throws Exception {
        logic.execute("add -n Amy Bee -p 11111111 -e amy@example.com");
        boolean[] failSaves = {true};
        JsonAddressBookStorage flakyStorage = new JsonAddressBookStorage(
                temporaryFolder.resolve("addressBook.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                if (failSaves[0]) {
                    throw DUMMY_AD_EXCEPTION;
                }
                super.saveAddressBook(addressBook);
            }
        };
        logic = new LogicManager(model, new StorageManager(flakyStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"))));

        assertThrows(CommandException.class, () -> logic.execute("edit 1 -p 99999999"));
        assertEquals(new CommandResult(HelpCommand.SHOWING_HELP_MESSAGE, true, false), logic.execute("help"));
        assertEquals(new CommandResult(ViewJobCommand.MESSAGE_SUCCESS, ViewRequest.jobs()), logic.execute("viewj"));
        Applicant editedApplicant = model.getFilteredApplicantList().get(0);
        assertEquals(new CommandResult("Viewing applicant: Amy Bee", ViewRequest.applicant(editedApplicant)),
                logic.execute("view 1"));

        failSaves[0] = false;
        assertEquals(new CommandResult("", false, true), logic.execute("bye"));
        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals("99999999", savedStorage.readAddressBook().orElseThrow()
                .getApplicantList().get(0).getPhone().value);
    }

    @Test
    public void execute_unchangedCommands_doNotRequireSave() throws Exception {
        logic.execute(ApplicantUtil.getAddCommand(AMY));
        JsonAddressBookStorage savedStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        ReadOnlyAddressBook saved = savedStorage.readAddressBook().orElseThrow();

        JsonAddressBookStorage throwingStorage = new JsonAddressBookStorage(
                temporaryFolder.resolve("unused.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw DUMMY_AD_EXCEPTION;
            }
        };
        logic = new LogicManager(model, new StorageManager(throwingStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"))));

        assertEquals(new CommandResult(HelpCommand.SHOWING_HELP_MESSAGE, true, false), logic.execute("help"));
        assertEquals(new CommandResult(ViewJobCommand.MESSAGE_SUCCESS, ViewRequest.jobs()), logic.execute("viewj"));
        assertEquals(new CommandResult("", false, true), logic.execute("bye"));
        assertEquals(saved, savedStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_view_returnsApplicantWithoutChangingModel() throws Exception {
        model.addApplicant(AMY);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        CommandResult result = logic.execute("view 1");
        assertEquals(new CommandResult("Viewing applicant: Amy Bee", ViewRequest.applicant(AMY)), result);
        assertEquals(expectedModel, model);
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getFilteredApplicantList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredApplicantList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = ApplicantUtil.getAddCommand(AMY);
        Applicant expectedApplicant = new ApplicantBuilder(AMY).build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addApplicant(expectedApplicant);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
