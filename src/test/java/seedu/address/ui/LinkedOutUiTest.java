package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
        onFxThread(() -> {
            MainWindow window = createWindow();
            try {
                assertEquals("LinkedOut", window.getRoot().getTitle());
                ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#personListView");
                assertEquals("Alice Pauline", label(window, "applicantName").getText());
                command(window, "remark 1 r/Likes baseball");
                Person remarked = (Person) applicants.getItems().get(0);
                PersonCard card = new PersonCard(remarked, 1);
                assertEquals("Likes baseball", ((Label) card.getRoot().lookup("#remark")).getText());
                command(window, "remark 1 r/");
                Person cleared = (Person) applicants.getItems().get(0);
                PersonCard clearedCard = new PersonCard(cleared, 1);
                assertEquals("", ((Label) clearedCard.getRoot().lookup("#remark")).getText());
                applicants.getSelectionModel().select(1);
                assertEquals("Benson Meier", label(window, "applicantName").getText());

                command(window, "edit 2 n/Benson Updated");
                assertEquals("Benson Updated", label(window, "applicantName").getText());
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
            } finally {
                window.getRoot().close();
            }
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
