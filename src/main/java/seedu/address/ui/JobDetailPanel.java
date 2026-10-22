package seedu.address.ui;

import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.application.Job;

/** Displays the selected job. */
public class JobDetailPanel extends UiPart<Region> {
    private static final String FXML = "JobDetailPanel.fxml";

    @FXML
    private Label initials;
    @FXML
    private Label jobName;
    @FXML
    private Label openBadge;
    @FXML
    private Label selectionHint;
    @FXML
    private Label jobDescription;
    @FXML
    private VBox roleDetails;

    /** Creates the detail panel with no job selected. */
    public JobDetailPanel() {
        super(FXML);
        showJob(null);
    }

    /**
     * Updates the job details, or shows the empty state when no job is selected.
     * @param job job to display, or null when none is selected
     */
    public void showJob(Job job) {
        boolean selected = job != null;
        roleDetails.setVisible(selected);
        roleDetails.setManaged(selected);
        openBadge.setVisible(selected);
        openBadge.setManaged(selected);
        if (!selected) {
            jobName.setText("No job selected");
            initials.setText("?");
            selectionHint.setText("Select a job from the list to view its details.");
            jobDescription.setText("");
            return;
        }

        jobName.setText(job.toString());
        initials.setText(initialsOf(job.toString()));
        selectionHint.setText("Open role");
        jobDescription.setText(job.toString());
    }

    /**
     * Returns up to two initials from {@code description}.
     * @param description job description to abbreviate
     * @return up to two uppercase initials, or {@code J} when {@code description} has no words
     */
    private static String initialsOf(String description) {
        StringBuilder builder = new StringBuilder();
        for (String word : description.split("\\s+")) {
            if (word.isEmpty()) {
                continue;
            }
            builder.append(word.substring(0, 1).toUpperCase(Locale.ROOT));
            if (builder.length() == 2) {
                break;
            }
        }
        return builder.isEmpty() ? "J" : builder.toString();
    }
}
