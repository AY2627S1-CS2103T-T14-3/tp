package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.application.Application;

/**
 * A UI component that displays one application: the applicant, the job, and the status.
 */
public class ApplicationCard extends UiPart<Region> {

    private static final String FXML = "ApplicationListCard.fxml";

    public final Application application;

    @FXML
    private HBox cardPane;
    @FXML
    private Label id;
    @FXML
    private Label name;
    @FXML
    private Label job;
    @FXML
    private Label statusBadge;

    /**
     * Creates an {@code ApplicationCard} for {@code application} at {@code displayedIndex}.
     * @param application application to display.
     * @param displayedIndex one-based applicant index shown beside the application.
     */
    public ApplicationCard(Application application, int displayedIndex) {
        super(FXML);
        this.application = application;
        id.setText(displayedIndex + ". ");
        name.setText(application.getApplicantName());
        job.setText(application.getJob().toString());
        job.setWrapText(true);
        showStatus(application.getStatus());
    }

    /**
     * Shows a readable status label and its badge colour.
     * @param status application status to display.
     */
    private void showStatus(Application.Status status) {
        statusBadge.getStyleClass().removeAll("status-accept", "status-reject", "status-pending");
        if (status == Application.Status.ACCEPT) {
            statusBadge.setText("Accepted");
            statusBadge.getStyleClass().add("status-accept");
        } else if (status == Application.Status.REJECT) {
            statusBadge.setText("Rejected");
            statusBadge.getStyleClass().add("status-reject");
        } else {
            statusBadge.setText("Pending");
            statusBadge.getStyleClass().add("status-pending");
        }
    }
}
