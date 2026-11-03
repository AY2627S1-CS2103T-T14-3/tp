package seedu.address.ui;

import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
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
import seedu.address.model.Model;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.NameContainsKeywordsPredicate;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;
import seedu.address.model.application.JobDescriptionContainsKeywordPredicate;

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

    // Independent UI parts residing in this UI container.
    private ApplicantListPanel applicantListPanel;
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
     * Creates a {@code MainWindow} with the given stage, logic, and data file path.
     *
     * @param primaryStage stage used to display the application.
     * @param logic application logic component.
     * @param dataFilePath path to show in the status bar.
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

        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /** Fills all placeholders of this window. */
    void fillInnerParts() {
        applicantListPanel = new ApplicantListPanel(logic.getFilteredApplicantList());
        applicationListPanel = new ApplicationListPanel(
                logic.getFilteredApplicantList(), logic.getApplicationList());
        jobListPanel = new JobListPanel(logic.getFilteredJobList());

        applicantDetailPanel = new ApplicantDetailPanel();
        jobDetailPanel = new JobDetailPanel();

        viewListPlaceholder.getChildren().add(applicantListPanel.getRoot());
        applicantDetailPlaceholder.getChildren().add(applicantDetailPanel.getRoot());

        applicantsTab.setOnAction(event -> showView(ViewType.APPLICANTS));
        applicationsTab.setOnAction(event -> showView(ViewType.APPLICATIONS));
        jobsTab.setOnAction(event -> showView(ViewType.JOBS));

        applicantListPanel.selectedApplicantProperty().addListener((observable, previous, selected) ->
                refreshSelectedDetails());
        applicationListPanel.selectedApplicationProperty()
                .addListener((observable, previous, selected) -> refreshSelectedDetails());
        jobListPanel.selectedJobProperty().addListener((observable, previous, selected) ->
                refreshSelectedDetails());
        logic.getApplicationList().addListener((ListChangeListener<Application>) change ->
                refreshSelectedDetails());
        logic.getFilteredApplicantList().addListener((ListChangeListener<Applicant>) change ->
                refreshListDetails());
        logic.getFilteredJobList().addListener((ListChangeListener<Job>) change ->
                refreshListDetails());

        showView(ViewType.APPLICANTS);

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());
        resultDisplay.setFeedbackToUser("Ready. Type a command to get started, or use help for available commands.");

        StatusBarFooter statusBarFooter = new StatusBarFooter(dataFilePath);
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, this::handleListNavigation);
    }

    /**
     * Moves the selection when Up or Down is pressed in the command box or a list.
     * The result text and detail panes keep those keys for their own scrolling.
     */
    private void handleListNavigation(KeyEvent event) {
        if (event.isShiftDown() || event.isControlDown() || event.isAltDown() || event.isMetaDown()) {
            return;
        }

        int offset = 0;
        if (event.getCode() == KeyCode.UP) {
            offset = -1;
        } else if (event.getCode() == KeyCode.DOWN) {
            offset = 1;
        }
        if (offset == 0 || !isCommandOrList(event.getTarget())) {
            return;
        }

        switch (currentView) {
            case APPLICANTS -> applicantListPanel.selectOffset(offset);
            case APPLICATIONS -> applicationListPanel.selectOffset(offset);
            case JOBS -> jobListPanel.selectOffset(offset);
            default -> throw new IllegalStateException("Unexpected view type: " + currentView);
        }
        event.consume();
    }

    /**
     * Returns true if {@code target} is the command box or a record list.
     */
    private static boolean isCommandOrList(Object target) {
        if (!(target instanceof Node node)) {
            return false;
        }
        for (Node current = node; current != null; current = current.getParent()) {
            if (current instanceof ListView || "commandTextField".equals(current.getId())) {
                return true;
            }
        }
        return false;
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
        FilteredList<Applicant> applicantFilteredList = logic.getFilteredApplicantList();
        int totalApplicants = logic.getApplicantList().size();
        Predicate<? super Applicant> applicantPredicate = applicantFilteredList.getPredicate();

        if (applicantPredicate == null
                || applicantPredicate.equals(Model.PREDICATE_SHOW_ALL_APPLICANTS)
                || NameContainsKeywordsPredicate.getKeywords(applicantPredicate) == null) {
            listTitle.setText("Applicants");
            listHint.setText("Use the up and down arrows to select an applicant.");
        } else {
            listTitle.setText(String.format("Found %d out of %d applicant(s)",
                    applicantFilteredList.size(), totalApplicants));
            listHint.setText(String.format("Filtered with keyword(s): %s",
                    NameContainsKeywordsPredicate.getKeywords(applicantPredicate)));
        }
        consoleHint.setText(APPLICANT_HINT);
        viewListPlaceholder.getChildren().setAll(applicantListPanel.getRoot());
        applicantDetailPlaceholder.getChildren().setAll(applicantDetailPanel.getRoot());
        bindCount(applicantFilteredList, "applicant", "applicants");
    }

    private void showApplicationsView() {
        FilteredList<Applicant> applicantFilteredList = logic.getFilteredApplicantList();
        int totalApplicants = logic.getApplicantList().size();
        Predicate<? super Applicant> applicantPredicate = applicantFilteredList.getPredicate();

        if (applicantPredicate == null
                || applicantPredicate.equals(Model.PREDICATE_SHOW_ALL_APPLICANTS)
                || NameContainsKeywordsPredicate.getKeywords(applicantPredicate) == null) {
            listTitle.setText("Applications");
            listHint.setText("Use the up and down arrows to select an application.");
        } else {
            listTitle.setText(String.format("From %d out of %d applicant(s)",
                    applicantFilteredList.size(), totalApplicants));
            listHint.setText(String.format("Filtered with keyword(s): %s",
                    NameContainsKeywordsPredicate.getKeywords(applicantPredicate)));
        }
        consoleHint.setText(APPLICATION_HINT);
        viewListPlaceholder.getChildren().setAll(applicationListPanel.getRoot());
        applicantDetailPlaceholder.getChildren().setAll(applicantDetailPanel.getRoot());
        bindCount(applicationListPanel.getDisplayedApplications(), "application", "applications");
    }

    private void showJobsView() {
        FilteredList<Job> jobFilteredList = logic.getFilteredJobList();
        int totalJobs = logic.getJobList().size();
        Predicate<? super Job> jobPredicate = jobFilteredList.getPredicate();

        if (jobPredicate == null
                || jobPredicate.equals(Model.PREDICATE_SHOW_ALL_JOBS)
                || JobDescriptionContainsKeywordPredicate.getKeywords(jobPredicate) == null) {
            listTitle.setText("Jobs");
            listHint.setText("Use the up and down arrows to select a job.");
        } else {
            listTitle.setText(String.format("Found %d out of %d job(s)",
                    jobFilteredList.size(), totalJobs));
            listHint.setText(String.format("Filtered with keyword: %s",
                    JobDescriptionContainsKeywordPredicate.getKeywords(jobPredicate)));
        }
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

    private void refreshListDetails() {
        switch (currentView) {
            case APPLICANTS -> showApplicantsView();
            case APPLICATIONS -> showApplicationsView();
            case JOBS -> showJobsView();
            default -> throw new IllegalStateException("Unexpected view type: " + currentView);
        }
    }

    private void showSelectedApplicant() {
        Applicant selected = applicantListPanel.selectedApplicantProperty().get();
        Application application = null;
        if (selected != null) {
            application = logic.getApplicationList().stream()
                    .filter(existing -> existing.getApplicant().isSameApplicant(selected))
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
        listTitle.setPrefWidth(0);
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

    public ApplicantListPanel getApplicantListPanel() {
        return applicantListPanel;
    }

    /** Shows the applicants tab and details captured before deletion. */
    private void showDeletedApplicant(CommandResult.DeletedApplicant deleted) {
        showView(ViewType.APPLICANTS);
        applicantListPanel.clearSelection();
        applicantDetailPanel.showDeletedApplicant(deleted.applicant(), deleted.application());
    }

    private void applyViewRequest(ViewRequest request) {
        showView(request.getViewType());
        request.getSelection().ifPresent(this::selectViewItem);
    }

    private void selectViewItem(ViewSelection selection) {
        if (selection instanceof ApplicantSelection applicant) {
            applicantListPanel.selectApplicant(applicant.getApplicant());
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
