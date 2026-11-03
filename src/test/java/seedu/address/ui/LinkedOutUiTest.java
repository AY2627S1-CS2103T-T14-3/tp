package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.BENSON;
import static seedu.address.testutil.TypicalApplicants.DANIEL;
import static seedu.address.testutil.TypicalApplicants.GEORGE;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.AddApplicationCommand;
import seedu.address.logic.commands.DeleteApplicationCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.SetApplicationStatusCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.ApplicantBuilder;
import seedu.address.testutil.TypicalApplicants;

/** Integration checks for applicant selection and the existing command workflow. */
public class LinkedOutUiTest {
    /** Allows for rounding when JavaFX snaps layout sizes to the display grid. */
    private static final double LAYOUT_TOLERANCE = 1;

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
    public void applicantInitials_supplementaryCharacters_preservesCompleteInitials() throws Exception {
        withWindow((window, model) -> {
            for (Map.Entry<String, String> entry : Map.of(
                    "\uD840\uDC00 Ming Third", "\uD840\uDC00M",
                    "Alice \uD840\uDC00 Third", "A\uD840\uDC00",
                    "\uD840\uDC00 \uD840\uDC01 Third", "\uD840\uDC00\uD840\uDC01",
                    "\uD801\uDC28 Ming Third", "\uD801\uDC00M",
                    "Alice Bob Carol", "AB",
                    "Alice", "A").entrySet()) {
                Applicant previous = model.getFilteredApplicantList().getFirst();
                model.setApplicant(previous, new ApplicantBuilder(previous).withName(entry.getKey()).build());
                command(window, "view 1");
                pulse(window);

                assertEquals(entry.getKey(), label(window, "applicantName").getText());
                assertEquals(entry.getValue(), label(window, "initials").getText());
            }
        });
    }

    @Test
    public void jobInitials_supplementaryCharacters_preservesFirstTwoWordInitials() throws Exception {
        withWindow((window, model) -> {
            for (Map.Entry<String, String> entry : Map.of(
                    "\uD840\uDC00 Engineer Third", "\uD840\uDC00E",
                    "Engineer \uD840\uDC00 Third", "E\uD840\uDC00",
                    "\uD840\uDC00 \uD840\uDC01 Third", "\uD840\uDC00\uD840\uDC01",
                    "\uD801\uDC28 Engineer Third", "\uD801\uDC00E",
                    "Software Engineer Intern", "SE",
                    "Software\u2003Engineer", "SE",
                    "\uD840\uDC00", "\uD840\uDC00").entrySet()) {
                model.addJob(new Job(entry.getKey()));
                command(window, "viewj " + model.getFilteredJobList().size());
                pulse(window);

                assertEquals(entry.getKey(), label(window, "jobName").getText());
                assertEquals(entry.getValue(), label(window, "initials").getText());
            }
        });
    }

    @Test
    public void selectionAndCommands_keepApplicantDetailsCurrent() throws Exception {
        withWindow((window, model) -> {
            assertEquals("LinkedOut", window.getRoot().getTitle());
            ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#applicantListView");

            assertEquals("Alice Pauline", label(window, "applicantName").getText());
            applicants.getSelectionModel().select(1);
            assertEquals("Benson Meier", label(window, "applicantName").getText());
            model.setApplicant(BENSON, new ApplicantBuilder(BENSON).withName("Benson Updated").build());
            assertEquals("Benson Updated", label(window, "applicantName").getText());

            command(window, "find Alice");
            assertEquals("Alice Pauline", label(window, "applicantName").getText());
            assertEquals("1 applicant", label(window, "applicantCount").getText());
            command(window, "delete 1");
            assertEquals("Alice Pauline", label(window, "applicantName").getText());
            assertEquals("0 applicants", label(window, "applicantCount").getText());
            command(window, "list");
            assertNotNull(applicants.getSelectionModel().getSelectedItem());
            Applicant selected = (Applicant) applicants.getSelectionModel().getSelectedItem();
            assertEquals(selected.getName().fullName, label(window, "applicantName").getText());
            assertApplication(window, "No application recorded.", null, null);
        });
    }

    @Test
    public void applicantCards_longFields_stayWithinListWhenResized() throws Exception {
        withWindow((window, model) -> {
            Applicant applicant = new ApplicantBuilder(ALICE).withName("LongName".repeat(100))
                    .withEmail("long".repeat(40) + "@example.com")
                    .withPhone("+1-2-3-4-5-6-7-8-9-0-1-2-3-4-5").build();
            model.setApplicant(ALICE, applicant);

            for (int width : List.of(1200, 900, 1100)) {
                window.getRoot().getScene().getRoot().resize(width, 680);
                assertCardsFitList(window, "applicantListView", "applicant-card");
            }
            assertEquals(applicant, model.getFilteredApplicantList().getFirst());
        });
    }

    @Test
    public void applicationCards_longFields_keepStatusVisibleWhenResized() throws Exception {
        withWindow((window, model) -> {
            Applicant applicant = new ApplicantBuilder(ALICE).withName("LongName".repeat(100)).build();
            model.setApplicant(ALICE, applicant);
            Job job = new Job("LongRole".repeat(100), "Long description ".repeat(40));
            model.addJob(job);
            model.addApplication(new Application(applicant, job, Application.Status.PENDING));
            command(window, "viewp");

            for (int width : List.of(1200, 900, 1100)) {
                window.getRoot().getScene().getRoot().resize(width, 680);
                assertCardsFitList(window, "applicationListView", "application-card");
                Label status = (Label) window.getRoot().getScene()
                        .lookup(".application-card #statusBadge");
                assertEquals("Pending", status.getText());
                assertBadgeFits(status);
                for (String id : List.of("name", "job")) {
                    Label summary = (Label) window.getRoot().getScene()
                            .lookup(".application-card #" + id);
                    assertTrue(summary.getHeight() <= summary.prefHeight(-1) + 1,
                            "Card summaries should remain a single line when fields are long");
                }
            }
            assertEquals(job.toString(), ((Label) window.getRoot().getScene()
                    .lookup(".application-card #job")).getText());
        });
    }

    @Test
    public void applicationDetails_longJobFields_keepFullStatusVisibleWhenResized() throws Exception {
        withWindow((window, model) -> {
            Job job = new Job("LongRole".repeat(10), "Long description ".repeat(40));
            model.addJob(job);
            Application application = new Application(ALICE, job, Application.Status.PENDING);
            model.addApplication(application);
            command(window, "view 1");

            for (Application.Status status : Application.Status.values()) {
                Application updatedApplication = new Application(ALICE, job, status);
                model.setApplication(application, updatedApplication);
                application = updatedApplication;
                for (int width : List.of(1200, 900, 1100)) {
                    window.getRoot().getScene().getRoot().resize(width, 680);
                    pulse(window);
                    pulse(window);
                    Label badge = label(window, "applicationStatusBadge");
                    String expectedStatus = switch (status) {
                        case ACCEPT -> "Accepted";
                        case REJECT -> "Rejected";
                        case PENDING -> "Pending";
                    };
                    assertApplication(window, job.getJobRole(), job.getJobDescription(), expectedStatus);
                    assertBadgeFits(badge);
                    Node viewport = window.getRoot().getScene().lookup(".detail-scroll .viewport");
                    Bounds viewportBounds = viewport.localToScene(viewport.getLayoutBounds());
                    Bounds badgeBounds = badge.localToScene(badge.getLayoutBounds());
                    // Long details may scroll vertically, but the badge must fit horizontally.
                    assertTrue(badgeBounds.getMinX() >= viewportBounds.getMinX() - LAYOUT_TOLERANCE
                            && badgeBounds.getMaxX() <= viewportBounds.getMaxX() + LAYOUT_TOLERANCE,
                            "Status bounds " + badgeBounds + " exceed detail viewport width " + viewportBounds);
                }
            }
        });
    }

    @Test
    public void applicationDetails_focusedStatusBadge_keepsFullStatusVisible() throws Exception {
        withWindow((window, model) -> {
            Job job = new Job("LongRole".repeat(10), "Long description ".repeat(40));
            model.addJob(job);
            model.addApplication(new Application(ALICE, job, Application.Status.PENDING));
            command(window, "view 1");
            Label badge = label(window, "applicationStatusBadge");
            // Apply the focus style without depending on the operating system to focus the test window.
            badge.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), true);
            pulse(window);
            pulse(window);
            assertNotNull(badge.getEffect(), "The regression check must include the focus glow");
            assertBadgeFits(badge);
        });
    }

    @Test
    public void copyApplicantDetails_usesCurrentSelectionAndDisablesEmptyValues() throws Exception {
        withWindow(window -> {
            assertCopiesText(window, "applicantName", ALICE.getName().fullName);
            assertCopiesText(window, "email", ALICE.getEmail().value);
            assertCopiesText(window, "phone", ALICE.getPhone().value);

            command(window, "view 2");
            assertCopiesText(window, "email", BENSON.getEmail().value);
            assertCopiesText(window, "phone", BENSON.getPhone().value);

            command(window, "clear");
            assertTrue(label(window, "email").getContextMenu().getItems().getFirst().isDisable());
            assertTrue(label(window, "phone").getContextMenu().getItems().getFirst().isDisable());
        });
    }

    @Test
    public void copyApplicationAndJobDetails_preservesCompleteText() throws Exception {
        withWindow((window, model) -> {
            String description = "First line\n第二行";
            model.addJob(new Job("Engineer", description));
            command(window, "addp 1 -j Engineer");
            command(window, "viewp");
            assertCopiesText(window, "applicationStatus", "Engineer");
            assertCopiesText(window, "applicationStatusBadge", "Pending");
            assertCopiesText(window, "jobDesc", description);

            command(window, "set 1 -s accept");
            assertCopiesText(window, "applicationStatusBadge", "Accepted");
            command(window, "viewj");
            pulse(window);
            assertCopiesText(window, "jobName", "Engineer");
            assertCopiesText(window, "jobDescription", description);
        });
    }

    @Test
    public void copyShortcut_tabNavigation_reachesDetailValues() throws Exception {
        withWindow(window -> {
            TextField input = (TextField) window.getRoot().getScene().lookup("#commandTextField");
            input.requestFocus();
            Label email = label(window, "email");
            for (int i = 0; i < 20 && window.getRoot().getScene().getFocusOwner() != email; i++) {
                window.getRoot().getScene().getFocusOwner().fireEvent(
                        new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.TAB, false, false, false, false));
            }
            assertEquals(email, window.getRoot().getScene().getFocusOwner());
            email.fireEvent(copyKeyEvent());
            assertEquals(ALICE.getEmail().value, Clipboard.getSystemClipboard().getString());
        });
    }

    @Test
    public void copyShortcut_textInputAndEmptyValues_preservesExpectedClipboard() throws Exception {
        withWindow(window -> {
            TextField input = (TextField) window.getRoot().getScene().lookup("#commandTextField");
            input.setText("find Alice");
            input.requestFocus();
            input.selectRange(5, 10);
            input.fireEvent(copyKeyEvent());
            assertEquals("Alice", Clipboard.getSystemClipboard().getString());

            Label email = label(window, "email");
            email.requestFocus();
            email.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "c", "c", KeyCode.C,
                    false, false, false, false));
            assertEquals("Alice", Clipboard.getSystemClipboard().getString());

            command(window, "clear");
            email.fireEvent(copyKeyEvent());
            assertEquals("Alice", Clipboard.getSystemClipboard().getString());
        });
    }

    @Test
    public void sidebarCommands_switchBetweenApplicantAndApplicationTabs() throws Exception {
        withWindow(window -> {
            ToggleButton applicantsTab = (ToggleButton) window.getRoot().getScene().lookup("#applicantsTab");
            ToggleButton applicationsTab = (ToggleButton) window.getRoot().getScene().lookup("#applicationsTab");

            command(window, "viewp");
            assertFalse(applicantsTab.isSelected());
            assertTrue(applicationsTab.isSelected());

            command(window, "view");
            assertTrue(applicantsTab.isSelected());
            assertFalse(applicationsTab.isSelected());
        });
    }

    @Test
    public void viewApplications_filteredApplicants_showsApplicantJobAndStatus() throws Exception {
        withWindow(window -> {
            command(window, "addj -j Janitor");
            command(window, "addj -j Analyst");
            command(window, "addp 1 -j Janitor -s accept");
            command(window, "addp 2 -j Analyst -s pending");
            command(window, "find Alice");
            command(window, "viewp");
            pulse(window);

            ListView<?> applications = (ListView<?>) window.getRoot().getScene().lookup("#applicationListView");
            assertEquals("From 1 out of 7 applicant(s)", label(window, "listTitle").getText());
            assertEquals("1 application", label(window, "applicantCount").getText());
            assertEquals(1, applications.getItems().size());
            assertEquals("1. ", ((Label) applications.lookup("#id")).getText());
            assertEquals("Alice Pauline", ((Label) applications.lookup("#name")).getText());
            assertEquals("Janitor : No information ~", ((Label) applications.lookup("#job")).getText());
            assertEquals("Accepted", ((Label) applications.lookup("#statusBadge")).getText());
            assertApplication(window, "Janitor", "No information ~", "Accepted");

            command(window, "list");
            command(window, "viewp");
            pulse(window);
            assertEquals("2 applications", label(window, "applicantCount").getText());
            assertEquals(2, applications.getItems().size());
            assertEquals("Alice Pauline", ((Label) applications.lookup("#name")).getText());
            assertEquals("Janitor : No information ~", ((Label) applications.lookup("#job")).getText());
        });
    }

    @Test
    public void updateApplicant_selectedFirstApplicant_applicantNameUpdated() throws Exception {
        withWindow((window, model) -> {
            model.setApplicant(ALICE, new ApplicantBuilder(ALICE).withName("Alice Updated").build());
            assertApplicantName(window, "Alice Updated");
        });
    }

    @Test
    public void updateApplicant_selectedSecondApplicant_applicantNameUpdated() throws Exception {
        withWindow((window, model) -> {
            selectApplicant(window, 1);
            model.setApplicant(BENSON, new ApplicantBuilder(BENSON).withName("Benson Updated").build());
            assertApplicantName(window, "Benson Updated");
        });
    }

    @Test
    public void updateApplicant_unselectedFirstApplicant_applicantNameUnchanged() throws Exception {
        withWindow((window, model) -> {
            selectApplicant(window, 1);
            model.setApplicant(ALICE, new ApplicantBuilder(ALICE).withName("Alice Updated").build());
            assertApplicantName(window, "Benson Meier");
        });
    }

    @Test
    public void updateApplicant_unselectedSecondApplicant_applicantNameUnchanged() throws Exception {
        withWindow((window, model) -> {
            model.setApplicant(BENSON, new ApplicantBuilder(BENSON).withName("Benson Updated").build());
            assertApplicantName(window, "Alice Pauline");
        });
    }

    @Test
    public void editCommand_invalidSyntax_preservesApplicantsAndShowsError() throws Exception {
        withWindow((window, model) -> {
            List<Applicant> expectedApplicants = List.copyOf(model.getAddressBook().getApplicantList());
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
                assertEquals(expectedApplicants, model.getAddressBook().getApplicantList());
                assertEquals(List.of(BENSON, DANIEL), model.getFilteredApplicantList());
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
            command(window, "addj -job Engineer");
            command(window, "find Alice");
            command(window, "addp 1 -j Engineer -s accept");
            command(window, "viewp 1");
            assertEquals("From 1 out of 7 applicant(s)", label(window, "listTitle").getText());
            command(window, "delete 1");

            assertApplicantName(window, "Alice Pauline");
            assertEquals(ALICE.getEmail().value, label(window, "email").getText());
            assertEquals(ALICE.getPhone().value, label(window, "phone").getText());
            assertApplication(window, "Engineer", "No information ~", "Accepted");
            assertTrue(feedback(window).contains("Warning: The applicant's application was also deleted."));
            assertEquals("0 applicants", label(window, "applicantCount").getText());
            assertTrue(model.getApplicationList().isEmpty());
            ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#applicantListView");
            assertNull(applicants.getSelectionModel().getSelectedItem());

            command(window, "list");
            assertApplicantDetails(window, BENSON);
            assertApplication(window, "No application recorded.", null, null);
        });
    }

    @Test
    public void helpCommand_afterDeletion_preservesDeletedSnapshot() throws Exception {
        withWindow(window -> {
            command(window, "delete 1");
            assertApplicantName(window, "Alice Pauline");
            assertNull(window.getApplicantListPanel().selectedApplicantProperty().get());

            command(window, "help");

            assertApplicantName(window, "Alice Pauline");
            assertEquals("Deleted applicant. These details are no longer stored.",
                    label(window, "selectionHint").getText());
            assertNull(window.getApplicantListPanel().selectedApplicantProperty().get());

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
            assertNull(window.getApplicantListPanel().selectedApplicantProperty().get());

            window.handleHelp();

            assertApplicantName(window, "Alice Pauline");
            assertEquals("Deleted applicant. These details are no longer stored.",
                    label(window, "selectionHint").getText());
            assertNull(window.getApplicantListPanel().selectedApplicantProperty().get());
        });
    }

    @Test
    public void helpCommand_afterDeletingLastDisplayedApplicant_preservesApplicationSnapshot() throws Exception {
        withWindow(window -> {
            command(window, "addj -job Engineer");
            command(window, "find Alice");
            command(window, "addp 1 -j Engineer -s accept");
            command(window, "delete 1");
            assertApplicantName(window, "Alice Pauline");
            assertApplication(window, "Engineer", "No information ~", "Accepted");

            command(window, "help");

            assertApplicantName(window, "Alice Pauline");
            assertApplication(window, "Engineer", "No information ~", "Accepted");
            assertEquals("Deleted applicant. These details are no longer stored.",
                    label(window, "selectionHint").getText());
            assertNull(window.getApplicantListPanel().selectedApplicantProperty().get());
            assertEquals("0 applicants", label(window, "applicantCount").getText());

            command(window, "list");
            assertApplicantDetails(window, BENSON);
            assertApplication(window, "No application recorded.", null, null);
        });
    }

    @Test
    public void find_afterJobsView_showsMatchingApplicants() throws Exception {
        withWindow(window -> {
            command(window, "viewj");
            assertEquals("Jobs", label(window, "listTitle").getText());

            command(window, "find Alice");
            assertEquals("Found 1 out of 7 applicant(s)", label(window, "listTitle").getText());
            assertEquals("1 applicant(s) listed!", feedback(window));
            assertEquals("1 applicant", label(window, "applicantCount").getText());
            assertApplicantDetails(window, ALICE);
            ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#applicantListView");
            assertEquals(1, applicants.getItems().size());
        });
    }

    @Test
    public void clear_afterDeletedApplicant_showsEmptyDetails() throws Exception {
        withWindow(window -> {
            command(window, "find Alice");
            command(window, "delete 1");
            assertEquals("Deleted applicant. These details are no longer stored.",
                    label(window, "selectionHint").getText());

            assertEquals("Found 0 out of 6 applicant(s)", label(window, "listTitle").getText());
            assertEquals("Filtered with keyword(s): Alice", label(window, "listHint").getText());
            command(window, "list");
            command(window, "clear");
            assertEquals("Applicants", label(window, "listTitle").getText());
            assertEquals("No applicant selected", label(window, "applicantName").getText());
            assertEquals("Select an applicant from the list to view their details.",
                    label(window, "selectionHint").getText());
            assertEquals("0 applicants", label(window, "applicantCount").getText());
            assertEquals("Address book has been cleared!", feedback(window));
        });
    }

    @Test
    public void addAndEdit_fromJobsView_showsUpdatedApplicant() throws Exception {
        withWindow(window -> {
            command(window, "addj -j Software Engineer");
            pulse(window);
            assertEquals("Jobs", label(window, "listTitle").getText());

            command(window, "add -n Ada Lovelace -p 81234567 -e ada@example.com");
            assertEquals("Applicants", label(window, "listTitle").getText());
            assertApplicantName(window, "Ada Lovelace");
            assertEquals("ada@example.com", label(window, "email").getText());

            command(window, "viewj");
            command(window, "edit 1 -n Alice Edited");
            assertEquals("Applicants", label(window, "listTitle").getText());
            assertApplicantName(window, "Alice Edited");
            assertEquals(ALICE.getEmail().value, label(window, "email").getText());
        });
    }

    @Test
    public void help_afterJobsView_keepsJobList() throws Exception {
        withWindow(window -> {
            command(window, "addj -j Software Engineer");
            pulse(window);
            command(window, "help");
            assertEquals("Jobs", label(window, "listTitle").getText());
            assertEquals("Software Engineer", label(window, "jobName").getText());
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
    public void arrowKeys_applicantList_selectsNeighborAndKeepsTypedCommand() throws Exception {
        withWindow(window -> {
            TextField input = (TextField) window.getRoot().getScene().lookup("#commandTextField");
            input.setText("list");

            assertApplicantName(window, "Alice Pauline");
            pressArrow(window, KeyCode.DOWN);
            assertApplicantName(window, "Benson Meier");
            assertEquals("list", input.getText());

            pressArrow(window, KeyCode.UP);
            assertApplicantName(window, "Alice Pauline");
            pressArrow(window, KeyCode.UP);
            assertApplicantName(window, "Alice Pauline");

            selectApplicant(window, 6);
            pressArrow(window, KeyCode.DOWN);
            assertApplicantName(window, "George Best");

            command(window, "find completely-random-string");
            pressArrow(window, KeyCode.DOWN);
            assertApplicantName(window, "No applicant selected");

            command(window, "list");
            command(window, "delete 1");
            pressArrow(window, KeyCode.DOWN);
            assertApplicantName(window, "Benson Meier");
        });
    }

    @Test
    public void arrowKeys_jobsAndApplications_selectsNeighbor() throws Exception {
        withWindow(window -> {
            command(window, "addj -j Software Engineer");
            command(window, "addj -j Data Analyst");
            command(window, "viewj");
            pressArrow(window, KeyCode.UP);
            assertEquals("Software Engineer", label(window, "jobName").getText());
            pressArrow(window, KeyCode.DOWN);
            assertEquals("Data Analyst", label(window, "jobName").getText());

            command(window, "addp 1 -j Software Engineer");
            command(window, "addp 2 -j Data Analyst");
            command(window, "viewp");
            pressArrow(window, KeyCode.DOWN);
            assertApplicantName(window, "Benson Meier");
            pressArrow(window, KeyCode.UP);
            assertApplicantName(window, "Alice Pauline");
        });
    }

    @Test
    public void arrowKeys_feedbackAndJobDetails_keepSelection() throws Exception {
        withWindow(window -> {
            TextField input = (TextField) window.getRoot().getScene().lookup("#commandTextField");
            input.setText("add");
            input.fireEvent(new ActionEvent());
            TextArea feedback = (TextArea) window.getRoot().getScene().lookup("#resultDisplay");
            feedback.requestFocus();
            int caret = feedback.getCaretPosition();
            assertApplicantName(window, "Alice Pauline");
            pressArrowOn(window, feedback, KeyCode.DOWN);
            assertApplicantName(window, "Alice Pauline");
            assertTrue(feedback.getCaretPosition() > caret);

            ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#applicantListView");
            applicants.requestFocus();
            pressArrowOn(window, applicants, KeyCode.DOWN);
            assertApplicantName(window, "Benson Meier");

            command(window, "addj -j Software Engineer -d " + "Long role details. ".repeat(40));
            command(window, "addj -j Data Analyst");
            command(window, "viewj");
            ListView<?> jobs = (ListView<?>) window.getRoot().getScene().lookup("#jobListView");
            jobs.getSelectionModel().select(0);
            pulse(window);
            ScrollPane details = (ScrollPane) window.getRoot().getScene().lookup(".detail-scroll");
            details.setMaxHeight(48);
            details.setPrefViewportHeight(36);
            details.requestFocus();
            pulse(window);
            pressArrowOn(window, details, KeyCode.DOWN);
            assertEquals("Software Engineer", label(window, "jobName").getText());
            // JavaFX may move focus to a copyable value inside the detail pane.
            Node focusOwner = window.getRoot().getScene().getFocusOwner();
            while (focusOwner != null && focusOwner != details) {
                focusOwner = focusOwner.getParent();
            }
            assertSame(details, focusOwner);
            assertTrue(details.getVvalue() > 0);
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
    public void viewCommand_noIndex_switchesToApplicantsAndKeepsFilterAndSelection() throws Exception {
        withWindow((window, model) -> {
            command(window, "find Meier");
            command(window, "view 2");
            for (String otherView : List.of("viewj", "viewp")) {
                command(window, otherView);
                command(window, "view");

                assertEquals("Found 2 out of 7 applicant(s)", label(window, "listTitle").getText());
                assertEquals(ViewCommand.MESSAGE_SHOWING_APPLICANTS, feedback(window));
                assertEquals(List.of(BENSON, DANIEL), model.getFilteredApplicantList());
                assertApplicantDetails(window, DANIEL);
            }

            command(window, "clear");
            command(window, "view");
            assertEquals(ViewCommand.MESSAGE_SHOWING_APPLICANTS, feedback(window));
            assertEquals("No applicant selected", label(window, "applicantName").getText());
        });
    }

    @Test
    public void viewCommand_validIndex_selectsApplicantAndShowsDetails() throws Exception {
        onFxThread(() -> {
            MainWindow window = createWindow();
            try {
                ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#applicantListView");
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
                ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#applicantListView");
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
                ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#applicantListView");
                TextField input = (TextField) window.getRoot().getScene().lookup("#commandTextField");
                for (String invalidCommand : List.of("view 3", "view 0", "view 1 extra")) {
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
            command(window, "addj -job Janitor");
            command(window, "addp 2 -job Janitor -s accept");
            ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#applicantListView");
            assertEquals(1, applicants.getSelectionModel().getSelectedIndex());
            assertApplicantDetails(window, BENSON);
            assertApplication(window, "Janitor", "No information ~", "Accepted");
            assertEquals(String.format(AddApplicationCommand.MESSAGE_SUCCESS, BENSON.getName(), BENSON.getEmail()),
                    feedback(window));
            assertEquals("7 applicants", label(window, "applicantCount").getText());
        });
    }

    @Test
    public void addApplication_duplicateAndInvalidStatus_preservesExistingApplication() throws Exception {
        withWindow(window -> {
            command(window, "addj -job Janitor");
            command(window, "addj -job Engineer");
            command(window, "find Meier");
            command(window, "addp 1 -j Janitor");
            ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#applicantListView");
            TextField input = (TextField) window.getRoot().getScene().lookup("#commandTextField");

            input.setText("addp 1 -j Engineer -s ACCEPT");
            input.fireEvent(new ActionEvent());
            assertEquals("addp 1 -j Engineer -s ACCEPT", input.getText());
            assertTrue(input.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
            assertEquals(AddApplicationCommand.MESSAGE_INVALID_STATUS, feedback(window));
            assertEquals(List.of(BENSON, DANIEL), applicants.getItems());
            assertApplicantDetails(window, BENSON);
            assertApplication(window, "Janitor", "No information ~", "Pending");

            input.setText("addp 1 -j Engineer");
            input.fireEvent(new ActionEvent());
            assertEquals(AddApplicationCommand.MESSAGE_DUPLICATE_APPLICATION, feedback(window));
            assertApplication(window, "Janitor", "No information ~", "Pending");
            assertEquals(List.of(BENSON, DANIEL), applicants.getItems());
        });
    }

    @Test
    public void setApplicationStatus_existingApplication_updatesDetailPanel() throws Exception {
        withWindow(window -> {
            command(window, "addj -job Janitor");
            command(window, "addp 2 -j Janitor");
            for (Map.Entry<String, String> status : Map.of("accept", "Accepted", "reject", "Rejected",
                    "pending", "Pending").entrySet()) {
                command(window, "set 2 -status " + status.getKey());
                ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#applicantListView");
                assertEquals(1, applicants.getSelectionModel().getSelectedIndex());
                assertApplicantDetails(window, BENSON);
                assertApplication(window, "Janitor", "No information ~", status.getValue());
                assertEquals(String.format(SetApplicationStatusCommand.MESSAGE_SUCCESS,
                        BENSON.getName(), BENSON.getEmail(), status.getValue()), feedback(window));
            }
        });
    }

    @Test
    public void deleteApplication_filteredList_selectsApplicantAndClearsApplicationDetails() throws Exception {
        withWindow((window, model) -> {
            command(window, "addj -j Janitor");
            command(window, "addp 2 -j Janitor");
            command(window, "find Meier");
            command(window, "view 2");
            command(window, "viewp");
            assertEquals("1 application", label(window, "applicantCount").getText());
            command(window, "deletep 1");

            assertEquals("Found 2 out of 7 applicant(s)", label(window, "listTitle").getText());
            assertApplicantDetails(window, BENSON);
            assertApplication(window, "No application recorded.", null, null);
            assertEquals(String.format(DeleteApplicationCommand.MESSAGE_SUCCESS,
                    BENSON.getName(), BENSON.getEmail()), feedback(window));
            assertEquals(List.of(BENSON, DANIEL), model.getFilteredApplicantList());
            assertFalse(model.hasApplication(BENSON));
        });
    }

    @Test
    public void deleteJob_withApplications_clearsApplicantApplicationDetails() throws Exception {
        withWindow((window, model) -> {
            command(window, "addj -j Janitor");
            command(window, "addp 2 -j Janitor");
            command(window, "viewp 2");
            assertEquals("Applications", label(window, "listTitle").getText());
            command(window, "viewj 1");
            pulse(window);
            assertEquals("Janitor", label(window, "jobName").getText());
            command(window, "deletej 1");
            assertTrue(feedback(window).contains("Warning: Applications deleted with this job: 1."));
            command(window, "viewp");
            assertEquals("0 applications", label(window, "applicantCount").getText());
            command(window, "view 2");

            assertApplicantDetails(window, BENSON);
            assertApplication(window, "No application recorded.", null, null);
            assertFalse(model.hasApplication(BENSON));
            assertTrue(model.getJobList().isEmpty());
        });
    }

    @Test
    public void setApplicationStatus_invalidStatusAndMissingApplication_preservesApplication() throws Exception {
        withWindow(window -> {
            command(window, "addj -job Janitor");
            command(window, "addp 1 -j Janitor");
            ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#applicantListView");
            TextField input = (TextField) window.getRoot().getScene().lookup("#commandTextField");

            input.setText("set 1 -s ACCEPT");
            input.fireEvent(new ActionEvent());
            assertEquals("set 1 -s ACCEPT", input.getText());
            assertTrue(input.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
            assertEquals(SetApplicationStatusCommand.MESSAGE_INVALID_STATUS, feedback(window));
            assertApplication(window, "Janitor", "No information ~", "Pending");

            input.setText("set 2 -s reject");
            input.fireEvent(new ActionEvent());
            assertEquals(SetApplicationStatusCommand.MESSAGE_MISSING_APPLICATION, feedback(window));
            assertEquals(0, applicants.getSelectionModel().getSelectedIndex());
            assertApplication(window, "Janitor", "No information ~", "Pending");
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
            assertEquals("Edited job: \nRole: Product Designer\nDescription: No information ~", feedback(window));
            ListView<?> jobs = (ListView<?>) window.getRoot().getScene().lookup("#jobListView");
            assertEquals("Product Designer : No information ~", jobs.getSelectionModel().getSelectedItem().toString());
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

            command(window, "addj -j Data Analyst");
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

            command(window, "addj -j Janitor");
            command(window, "addp 1 -j Janitor");
            command(window, "addj -j Janitwo");
            pulse(window);
            assertEquals("Jobs", label(window, "listTitle").getText());
            assertEquals("4 jobs", label(window, "applicantCount").getText());
            assertEquals("Janitwo", label(window, "jobName").getText());
            assertNull(window.getRoot().getScene().lookup("#applicantSummary"));

            command(window, "viewj");
            assertEquals("Listed all jobs.", feedback(window));
            assertEquals("Jobs", label(window, "listTitle").getText());

            command(window, "deletej 1");
            assertEquals("Deleted job and any associated applications: Software Engineer", feedback(window));
            assertEquals("3 jobs", label(window, "applicantCount").getText());
            ListView<?> jobs = (ListView<?>) window.getRoot().getScene().lookup("#jobListView");
            assertEquals("Data Analyst : No information ~", jobs.getItems().get(0).toString());
            assertNotNull(jobs.getSelectionModel().getSelectedItem());
        });
    }

    private MainWindow createWindow() {
        return createWindow(new ModelManager(TypicalApplicants.getTypicalAddressBook(), new UserPrefs()));
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
            Model model = new ModelManager(TypicalApplicants.getTypicalAddressBook(), new UserPrefs());
            MainWindow window = createWindow(model);
            try {
                assertEquals("LinkedOut", window.getRoot().getTitle());
                action.accept(window, model);
            } finally {
                window.getRoot().close();
            }
        });
    }

    private void pressArrow(MainWindow window, KeyCode code) {
        TextField input = (TextField) window.getRoot().getScene().lookup("#commandTextField");
        input.requestFocus();
        pressArrowOn(window, input, code);
    }

    private void pressArrowOn(MainWindow window, Node target, KeyCode code) {
        target.fireEvent(new KeyEvent(
                KeyEvent.KEY_PRESSED, "", "", code, false, false, false, false));
        pulse(window);
    }

    private void selectApplicant(MainWindow window, int zeroBasedIndex) {
        ListView<?> applicants = (ListView<?>) window.getRoot().getScene().lookup("#applicantListView");
        applicants.getSelectionModel().select(zeroBasedIndex);
    }

    private void assertApplicantName(MainWindow window, String expectedName) {
        assertEquals(expectedName, label(window, "applicantName").getText());
    }

    private void assertApplication(MainWindow window, String jobRole, String jobDescription, String status) {
        assertEquals(jobRole, label(window, "applicationStatus").getText());
        Label jobDescLabel = label(window, "jobDesc");
        Label badge = label(window, "applicationStatusBadge");
        if (jobDescription == null || status == null) {
            assertNull(status);
            assertNull(jobDescription);
            assertFalse(jobDescLabel.isVisible());
            assertFalse(badge.isVisible());
            return;
        }

        assertTrue(jobDescLabel.isVisible());
        assertEquals(jobDescription, jobDescLabel.getText());
        assertTrue(badge.isVisible());
        assertEquals(status, badge.getText());
    }

    /** Verifies that the menu and keyboard shortcut copy the complete, current detail value. */
    private void assertCopiesText(MainWindow window, String id, String expected) {
        Label value = label(window, id);
        assertNotNull(value.getContextMenu(), "Missing Copy menu: " + id);
        assertTrue(value.isFocusTraversable());
        assertFalse(value.getContextMenu().getItems().getFirst().isDisable());
        value.getContextMenu().getItems().getFirst().fire();
        assertEquals(expected, Clipboard.getSystemClipboard().getString());

        ClipboardContent previous = new ClipboardContent();
        previous.putString("Previous clipboard value");
        Clipboard.getSystemClipboard().setContent(previous);
        value.requestFocus();
        assertEquals(value, window.getRoot().getScene().getFocusOwner());
        value.fireEvent(copyKeyEvent());
        assertEquals(expected, Clipboard.getSystemClipboard().getString());
    }

    /** Creates a copy keystroke using the platform's shortcut modifier. */
    private KeyEvent copyKeyEvent() {
        boolean isMac = System.getProperty("os.name").startsWith("Mac");
        return new KeyEvent(KeyEvent.KEY_PRESSED, "c", "c", KeyCode.C, false, !isMac, false, isMac);
    }

    private Label label(MainWindow window, String id) {
        Label label = (Label) window.getRoot().getScene().lookup("#" + id);
        assertNotNull(label, "Missing applicant detail: " + id);
        return label;
    }

    private void assertApplicantDetails(MainWindow window, Applicant applicant) {
        assertEquals(applicant, window.getApplicantListPanel().selectedApplicantProperty().get());
        assertEquals(applicant.getName().fullName, label(window, "applicantName").getText());
        Node contactDetails = window.getRoot().getScene().lookup("#contactDetails");
        assertEquals(applicant.getEmail().value, ((Label) contactDetails.lookup("#email")).getText());
        assertEquals(applicant.getPhone().value, ((Label) contactDetails.lookup("#phone")).getText());
    }

    private String feedback(MainWindow window) {
        return ((TextArea) window.getRoot().getScene().lookup("#resultDisplay")).getText();
    }

    /** Verifies that a status badge displays its full text within its row. */
    private void assertBadgeFits(Label badge) {
        assertTrue(badge.isVisible());
        assertTrue(badge.getWidth() >= badge.prefWidth(-1) - LAYOUT_TOLERANCE,
                "Status width " + badge.getWidth() + " vs preferred " + badge.prefWidth(-1));
        Bounds rowBounds = badge.getParent().getLayoutBounds();
        // Compare layout boxes so a decorative focus glow does not count as clipped content.
        Bounds badgeBounds = badge.localToParent(badge.getLayoutBounds());
        assertTrue(badgeBounds.getMinX() >= rowBounds.getMinX() - LAYOUT_TOLERANCE
                && badgeBounds.getMaxX() <= rowBounds.getMaxX() + LAYOUT_TOLERANCE
                && badgeBounds.getMinY() >= rowBounds.getMinY() - LAYOUT_TOLERANCE
                && badgeBounds.getMaxY() <= rowBounds.getMaxY() + LAYOUT_TOLERANCE,
                "Status layout bounds " + badgeBounds + " exceed row bounds " + rowBounds);
    }

    /** Verifies that cards fit the viewport without a horizontal scrollbar. */
    private void assertCardsFitList(MainWindow window, String listId, String cardClass) {
        pulse(window);
        pulse(window);
        ListView<?> list = (ListView<?>) window.getRoot().getScene().lookup("#" + listId);
        assertFalse(list.lookupAll("." + cardClass).isEmpty());
        int visibleCardCount = 0;
        for (Node card : list.lookupAll("." + cardClass)) {
            // VirtualFlow retains offscreen cells at their old widths until they are reused.
            if (!card.getParent().isVisible()) {
                continue;
            }
            visibleCardCount++;
            assertTrue(card.getLayoutBounds().getWidth() >= list.getWidth() - 30,
                    "Visible cards should fill the list width, allowing space for its scrollbar");
            assertTrue(card.getLayoutBounds().getWidth() <= list.getWidth() + 1,
                    "Card width " + card.getLayoutBounds().getWidth() + " exceeds list width " + list.getWidth());
        }
        assertTrue(visibleCardCount > 0);
        for (Node node : list.lookupAll(".scroll-bar")) {
            ScrollBar bar = (ScrollBar) node;
            if (bar.getOrientation() == Orientation.HORIZONTAL) {
                assertFalse(bar.isVisible(), "Long fields must not cause horizontal scrolling");
            }
        }
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
