package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.GEORGE;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.AddApplicationCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.SetApplicationStatusCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.TypicalPersons;

/** Integration checks for applicant selection and the existing command workflow. */
public class LinkedOutUiTest {
    @TempDir
    public Path testFolder;

    @BeforeAll
    public static void startToolkit() throws Exception {
        assumeFalse(java.awt.GraphicsEnvironment.isHeadless(), "JavaFX integration checks require a display");
        CompletableFuture<Void> started = new CompletableFuture<>();
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            started.complete(null);
        });
        started.get(15, TimeUnit.SECONDS);
    }

    @Test
    public void selectionAndCommands_keepApplicantDetailsCurrent() throws Exception {
        withWindow((window, model) -> {
            assertEquals("LinkedOut", window.getRoot().getTitle());
            ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#personListView");

            assertEquals("Alice Pauline", label(window, "applicantName").getText());
            applicants.getSelectionModel().select(1);
            assertEquals("Benson Meier", label(window, "applicantName").getText());
            model.setPerson(BENSON, new PersonBuilder(BENSON).withName("Benson Updated").build());
            assertEquals("Benson Updated", label(window, "applicantName").getText());

            command(window, "find Alice");
            assertEquals("Alice Pauline", label(window, "applicantName").getText());
            assertEquals("1 applicant", label(window, "applicantCount").getText());
            command(window, "delete 1");
            assertEquals("Alice Pauline", label(window, "applicantName").getText());
            assertEquals("0 applicants", label(window, "applicantCount").getText());
            command(window, "list");
            assertNotNull(applicants.getSelectionModel().getSelectedItem());
            Person selected = (Person) applicants.getSelectionModel().getSelectedItem();
            assertEquals(selected.getName().fullName, label(window, "applicantName").getText());
            assertApplication(window, "No application recorded.", null);
        });
    }

    @Test
    public void updateApplicant_selectedFirstApplicant_applicantNameUpdated() throws Exception {
        withWindow((window, model) -> {
            model.setPerson(ALICE, new PersonBuilder(ALICE).withName("Alice Updated").build());
            assertApplicantName(window, "Alice Updated");
        });
    }

    @Test
    public void updateApplicant_selectedSecondApplicant_applicantNameUpdated() throws Exception {
        withWindow((window, model) -> {
            selectApplicant(window, 1);
            model.setPerson(BENSON, new PersonBuilder(BENSON).withName("Benson Updated").build());
            assertApplicantName(window, "Benson Updated");
        });
    }

    @Test
    public void updateApplicant_unselectedFirstApplicant_applicantNameUnchanged() throws Exception {
        withWindow((window, model) -> {
            selectApplicant(window, 1);
            model.setPerson(ALICE, new PersonBuilder(ALICE).withName("Alice Updated").build());
            assertApplicantName(window, "Benson Meier");
        });
    }

    @Test
    public void updateApplicant_unselectedSecondApplicant_applicantNameUnchanged() throws Exception {
        withWindow((window, model) -> {
            model.setPerson(BENSON, new PersonBuilder(BENSON).withName("Benson Updated").build());
            assertApplicantName(window, "Alice Pauline");
        });
    }

    @Test
    public void editCommand_invalidSyntax_preservesApplicantsAndShowsError() throws Exception {
        withWindow((window, model) -> {
            List<Person> expectedPersons = List.copyOf(model.getAddressBook().getPersonList());
            command(window, "find Meier");
            command(window, "view 2");
            TextField input = (TextField) window.getRoot().getScene().lookup("#commandTextField");

            for (String editCommand : List.of("edit", "edit 2 n/Updated Name",
                    "edit -applicant 2 -n Updated Name")) {
                input.setText(editCommand);
                input.fireEvent(new ActionEvent());

                assertEquals(editCommand, input.getText());
                assertTrue(input.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
                assertEquals(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE),
                        feedback(window));
                assertEquals(expectedPersons, model.getAddressBook().getPersonList());
                assertEquals(List.of(BENSON, DANIEL), model.getFilteredPersonList());
                assertApplicantDetails(window, DANIEL);
                assertEquals("2 applicants", label(window, "applicantCount").getText());
            }
        });
    }

    @Test
    public void deleteApplicant_selectedApplicant_showsDeletedDetails() throws Exception {
        withWindow(window -> {
            command(window, "delete 1");
            assertApplicantName(window, "Alice Pauline");
            assertEquals("Deleted applicant. These details are no longer stored.",
                    label(window, "selectionHint").getText());
        });
    }

    @Test
    public void deleteApplicant_unselectedApplicant_showsDeletedDetails() throws Exception {
        withWindow(window -> {
            command(window, "delete 2");
            assertApplicantName(window, "Benson Meier");
            command(window, "view 1");
            assertApplicantDetails(window, ALICE);
            assertEquals("Applicant contact information", label(window, "selectionHint").getText());
        });
    }

    @Test
    public void deleteApplicant_lastApplicant_showsApplicationSnapshotUntilNextCommand() throws Exception {
        withWindow((window, model) -> {
            command(window, "find Alice");
            command(window, "addp 1 -j Engineer -s accept");
            command(window, "delete 1");

            assertApplicantName(window, "Alice Pauline");
            assertEquals(ALICE.getEmail().value, label(window, "email").getText());
            assertEquals(ALICE.getPhone().value, label(window, "phone").getText());
            assertApplication(window, "Engineer", "Accepted");
            assertEquals("0 applicants", label(window, "applicantCount").getText());
            assertTrue(model.getApplicationList().isEmpty());
            ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#personListView");
            assertNull(applicants.getSelectionModel().getSelectedItem());

            command(window, "list");
            assertApplicantDetails(window, BENSON);
            assertApplication(window, "No application recorded.", null);
        });
    }

    @Test
    public void helpCommand_afterDeletion_preservesDeletedSnapshot() throws Exception {
        withWindow(window -> {
            command(window, "delete 1");
            assertApplicantName(window, "Alice Pauline");
            assertNull(window.getPersonListPanel().selectedPersonProperty().get());

            command(window, "help");

            assertApplicantName(window, "Alice Pauline");
            assertEquals("Deleted applicant. These details are no longer stored.",
                    label(window, "selectionHint").getText());
            assertNull(window.getPersonListPanel().selectedPersonProperty().get());

            command(window, "view 1");
            assertApplicantDetails(window, BENSON);
            assertEquals("Applicant contact information", label(window, "selectionHint").getText());
        });
    }

    @Test
    public void helpMenu_afterDeletion_preservesDeletedSnapshot() throws Exception {
        withWindow(window -> {
            command(window, "delete 1");
            assertApplicantName(window, "Alice Pauline");
            assertNull(window.getPersonListPanel().selectedPersonProperty().get());

            window.handleHelp();

            assertApplicantName(window, "Alice Pauline");
            assertEquals("Deleted applicant. These details are no longer stored.",
                    label(window, "selectionHint").getText());
            assertNull(window.getPersonListPanel().selectedPersonProperty().get());
        });
    }

    @Test
    public void helpCommand_afterDeletingLastDisplayedApplicant_preservesApplicationSnapshot() throws Exception {
        withWindow(window -> {
            command(window, "find Alice");
            command(window, "addp 1 -j Engineer -s accept");
            command(window, "delete 1");
            assertApplicantName(window, "Alice Pauline");
            assertApplication(window, "Engineer", "Accepted");

            command(window, "help");

            assertApplicantName(window, "Alice Pauline");
            assertApplication(window, "Engineer", "Accepted");
            assertEquals("Deleted applicant. These details are no longer stored.",
                    label(window, "selectionHint").getText());
            assertNull(window.getPersonListPanel().selectedPersonProperty().get());
            assertEquals("0 applicants", label(window, "applicantCount").getText());

            command(window, "list");
            assertApplicantDetails(window, BENSON);
            assertApplication(window, "No application recorded.", null);
        });
    }

    @Test
    public void findApplicant_currentApplicant_applicantNameUnchanged() throws Exception {
        withWindow(window -> {
            command(window, "find Alice");
            assertApplicantName(window, "Alice Pauline");
        });
    }

    @Test
    public void findApplicant_anotherApplicant_applicantNameUpdated() throws Exception {
        withWindow(window -> {
            command(window, "find Benson");
            assertApplicantName(window, "Benson Meier");
        });
    }

    @Test
    public void selectApplicant_anotherApplicant_applicantNameUpdated() throws Exception {
        withWindow(window -> {
            selectApplicant(window, 1);
            assertApplicantName(window, "Benson Meier");
        });
    }

    @Test
    public void findApplicant_noMatchingApplicant_noApplicantSelected() throws Exception {
        withWindow(window -> {
            command(window, "find completely-random-string");
            assertApplicantName(window, "No applicant selected");
            assertEquals("0 applicants", label(window, "applicantCount").getText());
        });
    }

    @Test
    public void viewCommand_validIndex_selectsApplicantAndShowsDetails() throws Exception {
        onFxThread(() -> {
            MainWindow window = createWindow();
            try {
                ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#personListView");
                command(window, "view 2");
                assertApplicantDetails(window, BENSON);
                assertEquals(1, applicants.getSelectionModel().getSelectedIndex());
                assertEquals("Viewing applicant: Benson Meier", feedback(window));

                command(window, "view 2");
                assertApplicantDetails(window, BENSON);
                applicants.getSelectionModel().selectFirst();
                assertApplicantDetails(window, ALICE);
                command(window, "view 7");
                assertApplicantDetails(window, GEORGE);
                assertEquals(6, applicants.getSelectionModel().getSelectedIndex());
                assertEquals("7 applicants", label(window, "applicantCount").getText());
            } finally {
                window.getRoot().close();
            }
        });
    }

    @Test
    public void viewCommand_filteredList_usesDisplayedIndexAndPreservesFilter() throws Exception {
        onFxThread(() -> {
            MainWindow window = createWindow();
            try {
                command(window, "find Meier");
                command(window, "view 2");
                ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#personListView");
                assertEquals(List.of(BENSON, DANIEL), applicants.getItems());
                assertEquals(1, applicants.getSelectionModel().getSelectedIndex());
                assertApplicantDetails(window, DANIEL);
                assertEquals("2 applicants", label(window, "applicantCount").getText());
                assertEquals("Viewing applicant: Daniel Meier", feedback(window));
            } finally {
                window.getRoot().close();
            }
        });
    }

    @Test
    public void viewCommand_invalidInput_preservesSelectionAndShowsError() throws Exception {
        onFxThread(() -> {
            MainWindow window = createWindow();
            try {
                command(window, "find Meier");
                command(window, "view 2");
                ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#personListView");
                TextField input = (TextField) window.getRoot().getScene().lookup("#commandTextField");
                for (String invalidCommand : List.of("view 3", "view 0", "view", "view 1 extra")) {
                    input.setText(invalidCommand);
                    input.fireEvent(new ActionEvent());
                    assertEquals(invalidCommand, input.getText());
                    assertTrue(input.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
                    assertEquals(DANIEL, applicants.getSelectionModel().getSelectedItem());
                    assertApplicantDetails(window, DANIEL);
                    assertEquals(List.of(BENSON, DANIEL), applicants.getItems());
                    if (invalidCommand.equals("view 3")) {
                        assertEquals(String.format(ViewCommand.MESSAGE_INVALID_INDEX, 2), feedback(window));
                    } else {
                        assertTrue(feedback(window).contains(ViewCommand.MESSAGE_USAGE));
                    }
                }

                command(window, "clear");
                input.setText("view 1");
                input.fireEvent(new ActionEvent());
                assertEquals("view 1", input.getText());
                assertEquals(ViewCommand.MESSAGE_EMPTY_LIST, feedback(window));
                assertEquals("No applicant selected", label(window, "applicantName").getText());
                assertEquals("0 applicants", label(window, "applicantCount").getText());
            } finally {
                window.getRoot().close();
            }
        });
    }

    @Test
    public void addApplication_validCommand_selectsApplicantAndShowsApplication() throws Exception {
        withWindow(window -> {
            command(window, "addp 2 -job Janitor -s accept");
            ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#personListView");
            assertEquals(1, applicants.getSelectionModel().getSelectedIndex());
            assertApplicantDetails(window, BENSON);
            assertApplication(window, "Janitor", "Accepted");
            assertEquals(String.format(AddApplicationCommand.MESSAGE_SUCCESS, BENSON.getName(), BENSON.getEmail()),
                    feedback(window));
            assertEquals("7 applicants", label(window, "applicantCount").getText());
        });
    }

    @Test
    public void addApplication_duplicateAndInvalidStatus_preservesExistingApplication() throws Exception {
        withWindow(window -> {
            command(window, "find Meier");
            command(window, "addp 1 -j Janitor");
            ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#personListView");
            TextField input = (TextField) window.getRoot().getScene().lookup("#commandTextField");

            input.setText("addp 1 -j Engineer -s ACCEPT");
            input.fireEvent(new ActionEvent());
            assertEquals("addp 1 -j Engineer -s ACCEPT", input.getText());
            assertTrue(input.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
            assertEquals(AddApplicationCommand.MESSAGE_INVALID_STATUS, feedback(window));
            assertEquals(List.of(BENSON, DANIEL), applicants.getItems());
            assertApplicantDetails(window, BENSON);
            assertApplication(window, "Janitor", "Pending");

            input.setText("addp 1 -j Engineer");
            input.fireEvent(new ActionEvent());
            assertEquals(AddApplicationCommand.MESSAGE_DUPLICATE_APPLICATION, feedback(window));
            assertApplication(window, "Janitor", "Pending");
            assertEquals(List.of(BENSON, DANIEL), applicants.getItems());
        });
    }

    @Test
    public void setApplicationStatus_existingApplication_updatesDetailPanel() throws Exception {
        withWindow(window -> {
            command(window, "addp 2 -j Janitor");
            command(window, "set 2 -status accept");
            ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#personListView");
            assertEquals(1, applicants.getSelectionModel().getSelectedIndex());
            assertApplicantDetails(window, BENSON);
            assertApplication(window, "Janitor", "Accepted");
            assertEquals(String.format(SetApplicationStatusCommand.MESSAGE_SUCCESS,
                    BENSON.getName(), BENSON.getEmail(), "accept"), feedback(window));
        });
    }

    @Test
    public void setApplicationStatus_invalidStatusAndMissingApplication_preservesApplication() throws Exception {
        withWindow(window -> {
            command(window, "addp 1 -j Janitor");
            ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#personListView");
            TextField input = (TextField) window.getRoot().getScene().lookup("#commandTextField");

            input.setText("set 1 -s ACCEPT");
            input.fireEvent(new ActionEvent());
            assertEquals("set 1 -s ACCEPT", input.getText());
            assertTrue(input.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
            assertEquals(SetApplicationStatusCommand.MESSAGE_INVALID_STATUS, feedback(window));
            assertApplication(window, "Janitor", "Pending");

            input.setText("set 2 -s reject");
            input.fireEvent(new ActionEvent());
            assertEquals(SetApplicationStatusCommand.MESSAGE_MISSING_APPLICATION, feedback(window));
            assertEquals(0, applicants.getSelectionModel().getSelectedIndex());
            assertApplication(window, "Janitor", "Pending");
        });
    }

    @Test
    public void editJob_filteredList_updatesDetailsAndSelectsRenamedJob() throws Exception {
        withWindow(window -> {
            command(window, "addj -j Software Engineer");
            command(window, "addj -j Data Analyst");
            command(window, "viewj -f data");
            command(window, "editj 1 -job Product Designer");
            pulse(window);

            assertEquals("Jobs", label(window, "listTitle").getText());
            assertEquals("2 jobs", label(window, "applicantCount").getText());
            assertEquals("Product Designer", label(window, "jobName").getText());
            assertEquals("Edited job: Product Designer", feedback(window));
            ListView<?> jobs = (ListView<?>) window.getRoot().getScene().lookup("#jobListView");
            assertEquals("Product Designer", jobs.getSelectionModel().getSelectedItem().toString());
            assertEquals(1, jobs.getSelectionModel().getSelectedIndex());

            TextField input = (TextField) window.getRoot().getScene().lookup("#commandTextField");
            input.setText("editj 2 -j Software Engineer");
            input.fireEvent(new ActionEvent());
            pulse(window);
            assertEquals("This job already exists in the job list.", feedback(window));
            assertEquals("Product Designer", label(window, "jobName").getText());
            assertEquals(1, jobs.getSelectionModel().getSelectedIndex());

            command(window, "list");
            command(window, "editj 1 -j Senior Software Engineer");
            pulse(window);
            assertEquals("Jobs", label(window, "listTitle").getText());
            assertEquals("Senior Software Engineer", label(window, "jobName").getText());
            assertEquals(0, jobs.getSelectionModel().getSelectedIndex());
        });
    }

    @Test
    public void jobCommands_switchToJobListAndBackToApplicants() throws Exception {
        withWindow(window -> {
            assertEquals("Applicants", label(window, "listTitle").getText());

            command(window, "addj -j Software Engineer");
            pulse(window);
            assertEquals("Jobs", label(window, "listTitle").getText());
            assertEquals("1 job", label(window, "applicantCount").getText());
            assertEquals("Software Engineer", label(window, "jobName").getText());
            assertEquals("Open", label(window, "openBadge").getText());
            assertNull(window.getRoot().getScene().lookup("#applicantSummary"));
            assertEquals("New job added: Software Engineer", feedback(window));

            command(window, "addj -job Data Analyst");
            pulse(window);
            assertEquals("2 jobs", label(window, "applicantCount").getText());
            assertEquals("Data Analyst", label(window, "jobName").getText());

            command(window, "viewj -f data");
            pulse(window);
            assertEquals("1 job", label(window, "applicantCount").getText());
            assertEquals("Data Analyst", label(window, "jobName").getText());
            assertEquals("1 job(s) listed!", feedback(window));

            command(window, "viewj");
            pulse(window);
            assertEquals("2 jobs", label(window, "applicantCount").getText());

            command(window, "list");
            pulse(window);
            assertEquals("Applicants", label(window, "listTitle").getText());
            assertEquals("Alice Pauline", label(window, "applicantName").getText());

            command(window, "addp 1 -j Janitor");
            command(window, "addj -j Janitor");
            pulse(window);
            assertEquals("Jobs", label(window, "listTitle").getText());
            assertEquals("3 jobs", label(window, "applicantCount").getText());
            assertEquals("Janitor", label(window, "jobName").getText());
            assertNull(window.getRoot().getScene().lookup("#applicantSummary"));

            command(window, "viewj");
            assertEquals("Listed all jobs.", feedback(window));
            assertEquals("Jobs", label(window, "listTitle").getText());

            command(window, "deletej 1");
            assertEquals("Deleted job: Software Engineer", feedback(window));
            assertEquals("2 jobs", label(window, "applicantCount").getText());
            ListView<?> jobs = (ListView<?>) window.getRoot().getScene().lookup("#jobListView");
            assertEquals("Data Analyst", jobs.getItems().get(0).toString());
            assertNotNull(jobs.getSelectionModel().getSelectedItem());
        });
    }

    private MainWindow createWindow() {
        return createWindow(new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs()));
    }

    private MainWindow createWindow(Model model) {
        Path dataPath = testFolder.resolve("applicants.json");
        StorageManager storage = new StorageManager(new JsonAddressBookStorage(dataPath),
                new JsonUserPrefsStorage(testFolder.resolve("preferences.json")));
        MainWindow window = new MainWindow(new Stage(), new LogicManager(model, storage), dataPath);
        window.fillInnerParts();
        window.show();
        window.getRoot().getScene().getRoot().applyCss();
        window.getRoot().getScene().getRoot().layout();
        return window;
    }

    private void withWindow(Consumer<MainWindow> action) throws Exception {
        withWindow((window, model) -> action.accept(window));
    }

    private void withWindow(BiConsumer<MainWindow, Model> action) throws Exception {
        onFxThread(() -> {
            Model model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());
            MainWindow window = createWindow(model);
            try {
                assertEquals("LinkedOut", window.getRoot().getTitle());
                action.accept(window, model);
            } finally {
                window.getRoot().close();
            }
        });
    }

    private void selectApplicant(MainWindow window, int zeroBasedIndex) {
        ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#personListView");
        applicants.getSelectionModel().select(zeroBasedIndex);
    }

    private void assertApplicantName(MainWindow window, String expectedName) {
        assertEquals(expectedName, label(window, "applicantName").getText());
    }

    private void assertApplication(MainWindow window, String job, String status) {
        assertEquals(job, label(window, "applicationStatus").getText());
        Label badge = label(window, "applicationStatusBadge");
        if (status == null) {
            assertFalse(badge.isVisible());
            return;
        }
        assertTrue(badge.isVisible());
        assertEquals(status, badge.getText());
    }

    private Label label(MainWindow window, String id) {
        Label label = (Label) window.getRoot().getScene().lookup("#" + id);
        assertNotNull(label, "Missing applicant detail: " + id);
        return label;
    }

    private void assertApplicantDetails(MainWindow window, Person person) {
        assertEquals(person, window.getPersonListPanel().selectedPersonProperty().get());
        assertEquals(person.getName().fullName, label(window, "applicantName").getText());
        Node contactDetails = window.getRoot().getScene().lookup("#contactDetails");
        assertEquals(person.getEmail().value, ((Label) contactDetails.lookup("#email")).getText());
        assertEquals(person.getPhone().value, ((Label) contactDetails.lookup("#phone")).getText());
    }

    private String feedback(MainWindow window) {
        return ((TextArea) window.getRoot().getScene().lookup("#resultDisplay")).getText();
    }

    private void pulse(MainWindow window) {
        window.getRoot().getScene().getRoot().applyCss();
        window.getRoot().getScene().getRoot().layout();
    }

    private void command(MainWindow window, String text) {
        TextField input = (TextField) window.getRoot().getScene().lookup("#commandTextField");
        input.setText(text);
        input.fireEvent(new ActionEvent());
        assertEquals("", input.getText(), "Command should succeed: " + text);
    }

    private static void onFxThread(Runnable action) throws Exception {
        CompletableFuture<Void> completed = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                action.run();
                completed.complete(null);
            } catch (Throwable error) {
                completed.completeExceptionally(error);
            }
        });
        completed.get(15, TimeUnit.SECONDS);
    }
}
