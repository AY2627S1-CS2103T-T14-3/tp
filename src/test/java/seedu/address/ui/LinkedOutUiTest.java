package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
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
        withWindow(window -> {
            assertEquals("LinkedOut", window.getRoot().getTitle());
            ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#personListView");

            System.out.println(1);
            assertEquals("Alice Pauline", label(window, "applicantName").getText());
            applicants.getSelectionModel().select(1);
            assertEquals("Benson Meier", label(window, "applicantName").getText());
            command(window, "edit 2 n/Benson Updated");
            assertEquals("Benson Updated", label(window, "applicantName").getText());

            System.out.println(2);
            command(window, "find Alice");
            assertEquals("Alice Pauline", label(window, "applicantName").getText());
            assertEquals("1 applicant", label(window, "applicantCount").getText());
            command(window, "delete 1");
            assertEquals("No applicant selected", label(window, "applicantName").getText());
            assertEquals("0 applicants", label(window, "applicantCount").getText());
            command(window, "list");
            assertNotNull(applicants.getSelectionModel().getSelectedItem());
            Person selected = (Person) applicants.getSelectionModel().getSelectedItem();
            assertEquals(selected.getName().fullName, label(window, "applicantName").getText());
            assertEquals("No application recorded.", label(window, "applicationStatus").getText());
        });
    }

    @Test
    public void editApplicant_selectedFirstApplicant_applicantNameUpdated() throws Exception {
        withWindow(window -> {
            command(window, "edit 1 n/Alice Updated");
            assertApplicantName(window, "Alice Updated");
        });
    }

    @Test
    public void editApplicant_selectedSecondApplicant_applicantNameUpdated() throws Exception {
        withWindow(window -> {
            selectApplicant(window, 1);
            command(window, "edit 2 n/Benson Updated");
            assertApplicantName(window, "Benson Updated");
        });
    }

    @Test
    public void editApplicant_unselectedFirstApplicant_applicantNameUnchanged() throws Exception {
        withWindow(window -> {
            selectApplicant(window, 1);
            command(window, "edit 1 n/Alice Updated");
            assertApplicantName(window, "Benson Meier");
        });
    }

    @Test
    public void editApplicant_unselectedSecondApplicant_applicantNameUnchanged() throws Exception {
        withWindow(window -> {
            command(window, "edit 2 n/Benson Updated");
            assertApplicantName(window, "Alice Pauline");
        });
    }

    @Test
    public void deleteApplicant_selectedApplicant_applicantNameUpdated() throws Exception {
        withWindow(window -> {
            command(window, "delete 1");
            assertApplicantName(window, "Benson Meier");
        });
    }

    @Test
    public void deleteApplicant_unselectedApplicant_applicantNameUnchanged() throws Exception {
        withWindow(window -> {
            command(window, "delete 2");
            assertApplicantName(window, "Alice Pauline");
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

    private MainWindow createWindow() {
        ModelManager model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());
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
        onFxThread(() -> {
            MainWindow window = createWindow();
            try {
                assertEquals("LinkedOut", window.getRoot().getTitle());
                action.accept(window);
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

    private Label label(MainWindow window, String id) {
        Label label = (Label) window.getRoot().getScene().lookup("#" + id);
        assertNotNull(label, "Missing applicant detail: " + id);
        return label;
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
