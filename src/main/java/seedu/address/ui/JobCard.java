package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.application.Job;

/**
 * A UI component that displays information of a {@code Job}.
 */
public class JobCard extends UiPart<Region> {

    private static final String FXML = "JobListCard.fxml";

    public final Job job;

    @FXML
    private HBox cardPane;
    @FXML
    private Label id;
    @FXML
    private Label role;
    @FXML
    private Label description;

    /**
     * Creates a {@code JobCard} with the given {@code Job} and index to display.
     * @param job job to display
     * @param displayedIndex one-based index shown beside the job
     */
    public JobCard(Job job, int displayedIndex, ListCell<Job> listCell) {
        super(FXML);
        this.job = job;
        id.setText(displayedIndex + ". ");
        role.setText(job.getJobRole());
        role.setWrapText(true);

        description.setText(job.getJobDescription());
        description.setWrapText(true);

        cardPane.maxWidthProperty().bind(listCell.widthProperty());
    }
}
