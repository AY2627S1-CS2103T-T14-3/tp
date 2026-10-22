package seedu.address.ui;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.model.application.Job;

/**
 * Panel containing the list of jobs.
 */
public class JobListPanel extends UiPart<Region> {
    private static final String FXML = "JobListPanel.fxml";

    @FXML
    private ListView<Job> jobListView;

    /**
     * Creates a {@code JobListPanel} with the given {@code ObservableList}.
     * @param jobList jobs to display
     */
    public JobListPanel(ObservableList<Job> jobList) {
        super(FXML);
        jobListView.setItems(jobList);
        jobListView.setCellFactory(listView -> new JobListViewCell());
        jobListView.setPlaceholder(new Label("No jobs to display."));
        jobListView.getSelectionModel().selectFirst();
        jobList.addListener((ListChangeListener<Job>) change -> {
            if (jobListView.getSelectionModel().getSelectedItem() == null && !jobList.isEmpty()) {
                jobListView.getSelectionModel().selectFirst();
            }
        });
    }

    /**
     * Returns the job currently selected by mouse or keyboard.
     * @return the selected job property
     */
    public ReadOnlyObjectProperty<Job> selectedJobProperty() {
        return jobListView.getSelectionModel().selectedItemProperty();
    }

    /**
     * Selects a job in the displayed list and scrolls to make it visible.
     * @param job job to select
     */
    public void selectJob(Job job) {
        jobListView.getSelectionModel().select(job);
        jobListView.scrollTo(job);
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Job} using a {@code JobCard}.
     */
    class JobListViewCell extends ListCell<Job> {
        @Override
        protected void updateItem(Job job, boolean empty) {
            super.updateItem(job, empty);

            if (empty || job == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new JobCard(job, getIndex() + 1).getRoot());
            }
        }
    }
}
