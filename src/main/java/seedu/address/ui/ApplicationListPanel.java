package seedu.address.ui;

import java.util.Objects;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.application.Application;
import seedu.address.model.person.Person;


/** Panel containing the list of applications. */
public class ApplicationListPanel extends UiPart<Region> {
    private final ListView<Application> applicationListView;
    private final ObservableList<Application> displayedApplications = FXCollections.observableArrayList();
    private final ObservableList<Person> applicants;

    /** Creates an application list backed by the supplied observable list. */
    public ApplicationListPanel(ObservableList<Person> applicants, ObservableList<Application> applications) {
        super("ApplicationListPanel.fxml");
        this.applicants = applicants;
        applicationListView = (ListView<Application>) getRoot().lookup("#applicationListView");
        applicationListView.setItems(displayedApplications);
        applicationListView.setPlaceholder(new Label("No applications to display."));
        applicationListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Application application, boolean empty) {
                super.updateItem(application, empty);
                if (empty || application == null) {
                    setGraphic(null);
                } else {
                    VBox card = new VBox(4);
                    card.getStyleClass().add("application-card");
                    int applicantIndex = findApplicantIndex(application);
                    Label applicant = new Label(applicantIndex + ". "
                            + application.getApplicantName());
                    applicant.getStyleClass().add("list-name");
                    Label job = new Label(application.getJob().toString());
                    job.getStyleClass().add("application-job");
                    Label status = new Label(application.getStatusValue());
                    status.getStyleClass().addAll("status-badge", "status-" + application.getStatusValue());
                    card.getChildren().addAll(applicant, job, status);
                    setGraphic(card);
                }
            }
        });
        applicants.addListener((ListChangeListener<Person>) change ->
                refreshApplications(applications));
        applications.addListener((ListChangeListener<Application>) change ->
                refreshApplications(applications));
        refreshApplications(applications);
    }

    private void refreshApplications(ObservableList<Application> applications) {
        displayedApplications.setAll(applicants.stream()
                .map(applicant -> applications.stream()
                        .filter(application -> application.isForApplicant(applicant))
                        .findFirst().orElse(null))
                .filter(Objects::nonNull)
                .toList());
        if (applicationListView.getSelectionModel().getSelectedItem() == null
                && !displayedApplications.isEmpty()) {
            applicationListView.getSelectionModel().selectFirst();
        }
    }

    /** Returns the application currently selected by mouse or keyboard. */
    public ReadOnlyObjectProperty<Application> selectedApplicationProperty() {
        return applicationListView.getSelectionModel().selectedItemProperty();
    }

    /** Selects an application in the displayed list and scrolls to make it visible. */
    public void selectApplication(Application application) {
        Objects.requireNonNull(application);
        applicationListView.getSelectionModel().select(application);
        applicationListView.scrollTo(application);
    }

    private int findApplicantIndex(Application application) {
        for (int index = 0; index < applicants.size(); index++) {
            if (application.isForApplicant(applicants.get(index))) {
                return index + 1;
            }
        }
        return 0;
    }
}
