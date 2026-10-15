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
import seedu.address.model.person.Person;

/**
 * Panel containing the list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);
    private int lastSelectedIndex = -1;

    @FXML
    private ListView<Person> personListView;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonListPanel(ObservableList<Person> personList) {
        super(FXML);
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
        personListView.setPlaceholder(new Label("No applicants to display."));

        personListView.getSelectionModel().selectedIndexProperty()
                .addListener((observable, previous, current) ->
                        updateLastSelectedIndex(current.intValue()));

        personList.addListener((ListChangeListener<Person>) change ->
                handlePersonListChange(change, personList));

        personListView.getSelectionModel().selectFirst();
    }

    private void updateLastSelectedIndex(int selectedIndex) {
        if (isValidSelectedIndex(selectedIndex)) {
            lastSelectedIndex = selectedIndex;
        }
    }

    private boolean isValidSelectedIndex(int selectedIndex) {
        return selectedIndex >= 0;
    }

    private void handlePersonListChange(ListChangeListener.Change<? extends Person> change,
                                         ObservableList<Person> personList) {
        assert change != null : "List change must not be null";
        assert personList != null : "Person list must not be null";

        boolean wasReplaced = wasReplacement(change);
        if (wasReplaced && isValidLastSelectedIndex(personList)) {
            restoreLastSelection(personList);
        } else if (personListView.getSelectionModel().getSelectedItem() == null
                && !personList.isEmpty()) {
            personListView.getSelectionModel().selectFirst();
        }
    }

    private boolean wasReplacement(ListChangeListener.Change<? extends Person> change) {
        assert change != null : "List change must not be null";

        boolean wasReplaced = false;
        while (change.next()) {
            wasReplaced |= change.wasReplaced();
        }
        return wasReplaced;
    }

    private void restoreLastSelection(ObservableList<Person> personList) {
        assert personList != null : "Person list must not be null";

        if (isValidLastSelectedIndex(personList)) {
            personListView.getSelectionModel().select(lastSelectedIndex);
        }
    }

    private boolean isValidLastSelectedIndex(ObservableList<Person> personList) {
        return lastSelectedIndex >= 0 && lastSelectedIndex < personList.size();
    }

    /** The applicant currently selected by mouse or keyboard. */
    public ReadOnlyObjectProperty<Person> selectedPersonProperty() {
        return personListView.getSelectionModel().selectedItemProperty();
    }

    /**
     * Selects an applicant in the displayed list and scrolls to make them visible.
     */
    public void selectPerson(Person person) {
        personListView.getSelectionModel().select(person);
        personListView.scrollTo(person);
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new PersonCard(person, getIndex() + 1).getRoot());
            }
        }
    }

}
