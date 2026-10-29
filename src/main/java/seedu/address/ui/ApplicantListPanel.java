package seedu.address.ui;

import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.applicant.Applicant;

/**
 * Panel containing the list of applicants.
 */
public class ApplicantListPanel extends UiPart<Region> {
    private static final String FXML = "ApplicantListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(ApplicantListPanel.class);
    private int lastSelectedIndex = -1;

    @FXML
    private ListView<Applicant> applicantListView;

    /**
     * Creates a {@code ApplicantListPanel} with the given {@code ObservableList}.
     */
    public ApplicantListPanel(ObservableList<Applicant> applicantList) {
        super(FXML);
        applicantListView.setItems(applicantList);
        applicantListView.setCellFactory(listView -> new ApplicantListViewCell());
        applicantListView.setPlaceholder(new Label("No applicants to display."));

        applicantListView.getSelectionModel().selectedIndexProperty()
                .addListener((observable, previous, current) ->
                        updateLastSelectedIndex(current.intValue()));

        applicantList.addListener((ListChangeListener<Applicant>) change ->
                handleApplicantListChange(change, applicantList));

        applicantListView.getSelectionModel().selectFirst();
    }

    private void updateLastSelectedIndex(int selectedIndex) {
        if (isValidSelectedIndex(selectedIndex)) {
            lastSelectedIndex = selectedIndex;
        }
    }

    private boolean isValidSelectedIndex(int selectedIndex) {
        return selectedIndex >= 0;
    }

    private void handleApplicantListChange(ListChangeListener.Change<? extends Applicant> change,
                                         ObservableList<Applicant> applicantList) {
        assert change != null : "List change must not be null";
        assert applicantList != null : "Applicant list must not be null";

        boolean wasReplaced = wasReplacement(change);
        if (wasReplaced && isValidLastSelectedIndex(applicantList)) {
            restoreLastSelection(applicantList);
        } else if (applicantListView.getSelectionModel().getSelectedItem() == null
                && !applicantList.isEmpty()) {
            applicantListView.getSelectionModel().selectFirst();
        }
    }

    private boolean wasReplacement(ListChangeListener.Change<? extends Applicant> change) {
        assert change != null : "List change must not be null";

        boolean wasReplaced = false;
        while (change.next()) {
            wasReplaced |= change.wasReplaced();
        }
        return wasReplaced;
    }

    private void restoreLastSelection(ObservableList<Applicant> applicantList) {
        assert applicantList != null : "Applicant list must not be null";

        if (isValidLastSelectedIndex(applicantList)) {
            applicantListView.getSelectionModel().select(lastSelectedIndex);
        }
    }

    private boolean isValidLastSelectedIndex(ObservableList<Applicant> applicantList) {
        return lastSelectedIndex >= 0 && lastSelectedIndex < applicantList.size();
    }

    /** The applicant currently selected by mouse or keyboard. */
    public ReadOnlyObjectProperty<Applicant> selectedApplicantProperty() {
        return applicantListView.getSelectionModel().selectedItemProperty();
    }

    /**
     * Selects an applicant in the displayed list and scrolls to make them visible.
     */
    public void selectApplicant(Applicant applicant) {
        applicantListView.getSelectionModel().select(applicant);
        applicantListView.scrollTo(applicant);
    }

    /** Clears the list selection when displaying a deleted applicant. */
    public void clearSelection() {
        applicantListView.getSelectionModel().clearSelection();
    }


    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Applicant} using a {@code ApplicantCard}.
     */
    class ApplicantListViewCell extends ListCell<Applicant> {
        @Override
        protected void updateItem(Applicant applicant, boolean empty) {
            super.updateItem(applicant, empty);

            if (empty || applicant == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new ApplicantCard(applicant, getIndex() + 1).getRoot());
            }
        }
    }

}
