package seedu.address.ui;

import java.nio.file.Path;
import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.application.Application;
import seedu.address.model.person.Person;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";
    private static final String APPLICANT_HINT = "Find and manage applicants using commands.";
    private static final String JOB_HINT = "Add, view, and remove open roles.";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;
    private Path dataFilePath;

    // Independent Ui parts residing in this Ui container
    private PersonListPanel personListPanel;
    private JobListPanel jobListPanel;
    private ApplicantDetailPanel applicantDetailPanel;
    private JobDetailPanel jobDetailPanel;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;
    private boolean showingJobs;

    @FXML
    private StackPane applicantDetailPlaceholder;

    @FXML
    private Label listTitle;

    @FXML
    private Label applicantCount;

    @FXML
    private Label listHint;

    @FXML
    private Label consoleHint;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private StackPane personListPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage}, {@code Logic},
     * and the data file path to show in the status bar.
     */
    public MainWindow(Stage primaryStage, Logic logic, Path dataFilePath) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;
        this.dataFilePath = dataFilePath;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        setAccelerators();

        helpWindow = new HelpWindow();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in a later version of the SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField and
         * ResultDisplay contains a TextArea, some accelerators (e.g., F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add the following event filter to capture such key events and open
         * the help window purposely so as to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        personListPanel = new PersonListPanel(logic.getFilteredPersonList());
        jobListPanel = new JobListPanel(logic.getFilteredJobList());
        personListPanelPlaceholder.getChildren().add(personListPanel.getRoot());

        applicantDetailPanel = new ApplicantDetailPanel();
        jobDetailPanel = new JobDetailPanel();
        applicantDetailPlaceholder.getChildren().add(applicantDetailPanel.getRoot());
        personListPanel.selectedPersonProperty().addListener((observable, previous, selected) ->
                showSelectedApplicant());
        jobListPanel.selectedJobProperty().addListener((observable, previous, selected) ->
                showSelectedJob());
        logic.getApplicationList().addListener((ListChangeListener<Application>) change ->
                showSelectedApplicant());
        showSelectedApplicant();
        bindCount(logic.getFilteredPersonList(), "applicant", "applicants");

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());
        resultDisplay.setFeedbackToUser("Ready. Type a command to get started, or use help for available commands.");

        StatusBarFooter statusBarFooter = new StatusBarFooter(dataFilePath);
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(Math.max(primaryStage.getMinHeight(), guiSettings.getWindowHeight()));
        primaryStage.setWidth(Math.max(primaryStage.getMinWidth(), guiSettings.getWindowWidth()));
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        primaryStage.hide();
    }

    public PersonListPanel getPersonListPanel() {
        return personListPanel;
    }

    /**
     * Shows the selected applicant and that applicant's application, if one has been recorded.
     */
    private void showSelectedApplicant() {
        Person selected = personListPanel.selectedPersonProperty().get();
        Application application = null;
        if (selected != null) {
            application = logic.getApplicationList().stream()
                    .filter(existing -> existing.getApplicant().isSamePerson(selected))
                    .findFirst()
                    .orElse(null);
        }
        applicantDetailPanel.showApplicant(selected, application);
    }

    /**
     * Shows the selected job. Applications are not matched to the role by description.
     */
    private void showSelectedJob() {
        jobDetailPanel.showJob(jobListPanel.selectedJobProperty().get());
    }

    /**
     * Shows the applicant list and detail panel.
     */
    private void showApplicantView() {
        if (!showingJobs) {
            return;
        }
        showingJobs = false;
        listTitle.setText("Applicants");
        listHint.setText("Select an applicant to view their details.");
        consoleHint.setText(APPLICANT_HINT);
        personListPanelPlaceholder.getChildren().setAll(personListPanel.getRoot());
        applicantDetailPlaceholder.getChildren().setAll(applicantDetailPanel.getRoot());
        bindCount(logic.getFilteredPersonList(), "applicant", "applicants");
        showSelectedApplicant();
    }

    /**
     * Shows the job list and detail panel.
     */
    private void showJobView() {
        if (showingJobs) {
            showSelectedJob();
            return;
        }
        showingJobs = true;
        listTitle.setText("Jobs");
        listHint.setText("Select a job to view its details.");
        consoleHint.setText(JOB_HINT);
        personListPanelPlaceholder.getChildren().setAll(jobListPanel.getRoot());
        applicantDetailPlaceholder.getChildren().setAll(jobDetailPanel.getRoot());
        bindCount(logic.getFilteredJobList(), "job", "jobs");
        showSelectedJob();
    }

    /**
     * Binds the list count badge to {@code items}.
     * @param items list whose size is shown
     * @param singular label used when the list contains one item
     * @param plural label used when the list does not contain one item
     */
    private void bindCount(ObservableList<?> items, String singular, String plural) {
        applicantCount.textProperty().unbind();
        applicantCount.textProperty().bind(Bindings.createStringBinding(() -> {
            int count = items.size();
            return count + " " + (count == 1 ? singular : plural);
        }, items));
    }

    /**
     * Executes the command and returns the result.
     *
     * @see seedu.address.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());
            if (commandResult.isShowJobs()) {
                showJobView();
                commandResult.getJobToView().ifPresent(jobListPanel::selectJob);
            } else {
                showApplicantView();
                if (commandResult.getDeletedApplicant().isPresent()) {
                    CommandResult.DeletedApplicant deleted = commandResult.getDeletedApplicant().orElseThrow();
                    personListPanel.clearSelection();
                    applicantDetailPanel.showDeletedApplicant(deleted.applicant(), deleted.application());
                } else {
                    commandResult.getPersonToView().ifPresent(personListPanel::selectPerson);
                    if (personListPanel.selectedPersonProperty().get() == null
                            && !logic.getFilteredPersonList().isEmpty()) {
                        personListPanel.selectPerson(logic.getFilteredPersonList().getFirst());
                    }
                    showSelectedApplicant();
                }
            }

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }
}
