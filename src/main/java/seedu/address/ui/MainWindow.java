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
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.ApplicantSelection;
import seedu.address.logic.commands.ApplicationSelection;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.JobSelection;
import seedu.address.logic.commands.ViewRequest;
import seedu.address.logic.commands.ViewSelection;
import seedu.address.logic.commands.ViewType;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.application.Application;
import seedu.address.model.person.Person;

/** The main window containing the command console and list views. */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";
    private static final String APPLICANT_HINT = "Find and manage applicants using commands.";
    private static final String APPLICATION_HINT = "See which applicant applied to each job.";
    private static final String JOB_HINT = "Add, view, and remove open roles.";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;
    private Path dataFilePath;
    private ViewType currentView = ViewType.APPLICANTS;

    // Independent Ui parts residing in this Ui container
    private PersonListPanel personListPanel;
    private ApplicationListPanel applicationListPanel;
    private JobListPanel jobListPanel;
    private ApplicantDetailPanel applicantDetailPanel;
    private JobDetailPanel jobDetailPanel;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;

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
    private ToggleButton applicantsTab;

    @FXML
    private ToggleButton applicationsTab;

    @FXML
    private ToggleButton jobsTab;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private StackPane viewListPlaceholder;

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

        this.primaryStage = primaryStage;
        this.logic = logic;
        this.dataFilePath = dataFilePath;

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

    /** Sets the accelerator of a menu item. */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TextInputControl consumes function-key events. Capture matching events before they
         * reach the command box or result display so that F1 remains available there.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /** Fills all placeholders of this window. */
    void fillInnerParts() {
        personListPanel = new PersonListPanel(logic.getFilteredPersonList());
        applicationListPanel = new ApplicationListPanel(
                logic.getFilteredPersonList(), logic.getApplicationList());
        jobListPanel = new JobListPanel(logic.getFilteredJobList());

        applicantDetailPanel = new ApplicantDetailPanel();
        jobDetailPanel = new JobDetailPanel();

        viewListPlaceholder.getChildren().add(personListPanel.getRoot());
        applicantDetailPlaceholder.getChildren().add(applicantDetailPanel.getRoot());

        applicantsTab.setOnAction(event -> showView(ViewType.APPLICANTS));
        applicationsTab.setOnAction(event -> showView(ViewType.APPLICATIONS));
        jobsTab.setOnAction(event -> showView(ViewType.JOBS));

        personListPanel.selectedPersonProperty().addListener((observable, previous, selected) ->
                refreshSelectedDetails());
        applicationListPanel.selectedApplicationProperty()
                .addListener((observable, previous, selected) -> refreshSelectedDetails());
        jobListPanel.selectedJobProperty().addListener((observable, previous, selected) ->
                refreshSelectedDetails());
        logic.getApplicationList().addListener((ListChangeListener<Application>) change ->
                refreshSelectedDetails());

        showView(ViewType.APPLICANTS);

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());
        resultDisplay.setFeedbackToUser("Ready. Type a command to get started, or use help for available commands.");

        StatusBarFooter statusBarFooter = new StatusBarFooter(dataFilePath);
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());
    }

    /** Shows the requested list and its matching detail panel. */
    private void showView(ViewType viewType) {
        currentView = viewType;
        switch (viewType) {
            case APPLICANTS -> showApplicantsView();
            case APPLICATIONS -> showApplicationsView();
            case JOBS -> showJobsView();
            default -> throw new IllegalStateException("Unexpected view type: " + viewType);
        }
        selectTab(viewType);
        refreshSelectedDetails();
    }

    private void showApplicantsView() {
        listTitle.setText("Applicants");
        listHint.setText("Select an applicant to view their details.");
        consoleHint.setText(APPLICANT_HINT);
        viewListPlaceholder.getChildren().setAll(personListPanel.getRoot());
        applicantDetailPlaceholder.getChildren().setAll(applicantDetailPanel.getRoot());
        bindCount(logic.getFilteredPersonList(), "applicant", "applicants");
    }

    private void showApplicationsView() {
        listTitle.setText("Applications");
        listHint.setText("Each row shows an applicant, job, and status.");
        consoleHint.setText(APPLICATION_HINT);
        viewListPlaceholder.getChildren().setAll(applicationListPanel.getRoot());
        applicantDetailPlaceholder.getChildren().setAll(applicantDetailPanel.getRoot());
        bindCount(applicationListPanel.getDisplayedApplications(), "application", "applications");
    }

    private void showJobsView() {
        listTitle.setText("Jobs");
        listHint.setText("Select a job to view its details.");
        consoleHint.setText(JOB_HINT);
        viewListPlaceholder.getChildren().setAll(jobListPanel.getRoot());
        applicantDetailPlaceholder.getChildren().setAll(jobDetailPanel.getRoot());
        bindCount(logic.getFilteredJobList(), "job", "jobs");
    }

    private void selectTab(ViewType viewType) {
        switch (viewType) {
            case APPLICANTS -> applicantsTab.setSelected(true);
            case APPLICATIONS -> applicationsTab.setSelected(true);
            case JOBS -> jobsTab.setSelected(true);
            default -> throw new IllegalStateException("Unexpected view type: " + viewType);
        }
    }

    private void refreshSelectedDetails() {
        switch (currentView) {
            case APPLICANTS -> showSelectedApplicant();
            case APPLICATIONS -> showSelectedApplication();
            case JOBS -> showSelectedJob();
            default -> throw new IllegalStateException("Unexpected view type: " + currentView);
        }
    }

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

    private void showSelectedApplication() {
        Application selected = applicationListPanel.selectedApplicationProperty().get();
        if (selected == null) {
            applicantDetailPanel.showApplicant(null, null);
            return;
        }
        applicantDetailPanel.showApplicant(selected.getApplicant(), selected);
    }

    private void showSelectedJob() {
        jobDetailPanel.showJob(jobListPanel.selectedJobProperty().get());
    }

    private void bindCount(ObservableList<?> items, String singular, String plural) {
        applicantCount.textProperty().unbind();
        applicantCount.textProperty().bind(Bindings.createStringBinding(() -> {
            int count = items.size();
            return count + " " + (count == 1 ? singular : plural);
        }, items));
    }

    /** Sets the default size based on {@code guiSettings}. */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(Math.max(primaryStage.getMinHeight(), guiSettings.getWindowHeight()));
        primaryStage.setWidth(Math.max(primaryStage.getMinWidth(), guiSettings.getWindowWidth()));
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /** Opens the help window or focuses on it if it is already open. */
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

    /** Closes the application. */
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
     * Shows the applicants tab and the details captured before deletion.
     * The list selection is cleared first so another applicant is not highlighted.
     */
    private void showDeletedApplicant(CommandResult.DeletedApplicant deleted) {
        showView(ViewType.APPLICANTS);
        personListPanel.clearSelection();
        applicantDetailPanel.showDeletedApplicant(deleted.applicant(), deleted.application());
    }

    private void applyViewRequest(ViewRequest request) {
        showView(request.getViewType());
        request.getSelection().ifPresent(this::selectViewItem);
    }

    private void selectViewItem(ViewSelection selection) {
        if (selection instanceof ApplicantSelection applicant) {
            personListPanel.selectPerson(applicant.getPerson());
        } else if (selection instanceof ApplicationSelection application) {
            applicationListPanel.selectApplication(application.getApplication());
        } else if (selection instanceof JobSelection job) {
            jobListPanel.selectJob(job.getJob());
        } else {
            throw new IllegalArgumentException("Unknown view selection");
        }
    }

    /** Executes the command and returns the result. */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());
            if (commandResult.getDeletedApplicant().isPresent()) {
                showDeletedApplicant(commandResult.getDeletedApplicant().orElseThrow());
            } else {
                commandResult.getViewRequest().ifPresent(this::applyViewRequest);
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
