package seedu.address.ui;

import java.util.Objects;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.model.application.Application;
import seedu.address.model.person.Person;

/**
 * Panel containing the list of applications.
 */
public class ApplicationListPanel extends UiPart<Region> {
    private static final String FXML = "ApplicationListPanel.fxml";

    private final ObservableList<Person> applicants;
    private final ObservableList<Application> applications;
    private final ObservableList<Application> displayedApplications = FXCollections.observableArrayList();
    private final ObservableList<Application> visibleApplications =
            FXCollections.unmodifiableObservableList(displayedApplications);

    @FXML
    private ListView<Application> applicationListView;

    /**
     * Creates an application list for the applicants and applications currently stored.
     * @param applicants applicants whose indexes are shown on each application.
     * @param applications applications available to display.
     */
    public ApplicationListPanel(ObservableList<Person> applicants, ObservableList<Application> applications) {
        super(FXML);
        this.applicants = applicants;
        this.applications = applications;
        applicationListView.setItems(displayedApplications);
        applicationListView.setCellFactory(listView -> new ApplicationListViewCell());
        applicationListView.setPlaceholder(new Label("No applications to display."));
        applicants.addListener((ListChangeListener<Person>) change -> refreshApplications());
        applications.addListener((ListChangeListener<Application>) change -> refreshApplications());
        refreshApplications();
    }

    /**
     * Returns the applications currently shown, in the same order as the applicant list.
     */
    public ObservableList<Application> getDisplayedApplications() {
        return visibleApplications;
    }

    private void refreshApplications() {
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

    /**
     * Selects an application in the displayed list and scrolls to make it visible.
     * @param application application to select.
     */
    public void selectApplication(Application application) {
        Objects.requireNonNull(application);
        applicationListView.getSelectionModel().select(application);
        applicationListView.scrollTo(application);
    }

    /**
     * Returns the one-based index of the applicant who owns {@code application}.
     * @param application application shown in this list.
     */
    private int applicantIndex(Application application) {
        for (int i = 0; i < applicants.size(); i++) {
            if (application.isForApplicant(applicants.get(i))) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * Custom {@code ListCell} that displays an {@code Application} using an {@code ApplicationCard}.
     */
    class ApplicationListViewCell extends ListCell<Application> {
        @Override
        protected void updateItem(Application application, boolean empty) {
            super.updateItem(application, empty);

            if (empty || application == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new ApplicationCard(application, applicantIndex(application)).getRoot());
            }
        }
    }
}
