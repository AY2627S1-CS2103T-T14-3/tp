package seedu.address.ui;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.application.Application;
import seedu.address.model.person.Person;

/** Displays the selected applicant's contact information and application placeholder. */
public class ApplicantDetailPanel extends UiPart<Region> {
    private static final String FXML = "ApplicantDetailPanel.fxml";

    @FXML
    private Label applicantName;
    @FXML
    private Label initials;
    @FXML
    private Label selectionHint;
    @FXML
    private Label email;
    @FXML
    private Label phone;
    @FXML
    private Label applicationStatus;
    @FXML
    private Label applicationStatusBadge;
    @FXML
    private VBox contactDetails;

    /** Creates the detail panel with no applicant selected. */
    public ApplicantDetailPanel() {
        super(FXML);
        showApplicant(null, null);
    }

    /**
     * Updates all details, or shows the empty state when no applicant is selected.
     * @param person applicant to display, or null when none is selected
     * @param application application belonging to {@code person}, or null when none is recorded
     */
    public void showApplicant(Person person, Application application) {
        boolean selected = person != null;
        contactDetails.setVisible(selected);
        contactDetails.setManaged(selected);
        if (!selected) {
            applicantName.setText("No applicant selected");
            initials.setText("?");
            selectionHint.setText("Select an applicant from the list to view their details.");
            email.setText("");
            phone.setText("");
            showApplication(null);
            return;
        }

        applicantName.setText(person.getName().fullName);
        initials.setText(Stream.of(person.getName().fullName.split("\\s+"))
                .limit(2).map(word -> word.substring(0, 1).toUpperCase(java.util.Locale.ROOT))
                .collect(Collectors.joining()));
        selectionHint.setText("Applicant contact information");
        email.setText(person.getEmail().value);
        phone.setText(person.getPhone().value);
        showApplication(application);
    }

    /**
     * Shows the job and a status badge, or the empty message when {@code application} is null.
     * @param application application to display, or null when none is recorded
     */
    private void showApplication(Application application) {
        applicationStatus.getStyleClass().removeAll("muted-label", "detail-value");
        applicationStatusBadge.getStyleClass().removeAll("status-accept", "status-reject", "status-pending");
        if (application == null) {
            applicationStatus.getStyleClass().add("muted-label");
            applicationStatus.setText("No application recorded.");
            applicationStatusBadge.setText("");
            applicationStatusBadge.setVisible(false);
            applicationStatusBadge.setManaged(false);
            return;
        }

        applicationStatus.getStyleClass().add("detail-value");
        applicationStatus.setText(application.getJob().toString());
        applicationStatusBadge.setVisible(true);
        applicationStatusBadge.setManaged(true);
        Application.Status status = application.getStatus();
        if (status == Application.Status.ACCEPT) {
            applicationStatusBadge.setText("Accepted");
            applicationStatusBadge.getStyleClass().add("status-accept");
        } else if (status == Application.Status.REJECT) {
            applicationStatusBadge.setText("Rejected");
            applicationStatusBadge.getStyleClass().add("status-reject");
        } else {
            applicationStatusBadge.setText("Pending");
            applicationStatusBadge.getStyleClass().add("status-pending");
        }
    }
}
