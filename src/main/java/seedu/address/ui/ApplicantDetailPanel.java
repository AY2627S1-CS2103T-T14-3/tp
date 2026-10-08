package seedu.address.ui;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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
    private Label address;
    @FXML
    private FlowPane tags;
    @FXML
    private VBox contactDetails;

    /** Creates the detail panel with no applicant selected. */
    public ApplicantDetailPanel() {
        super(FXML);
        showApplicant(null);
    }

    /** Updates all details, or shows the empty state when no applicant is selected. */
    public void showApplicant(Person person) {
        boolean selected = person != null;
        contactDetails.setVisible(selected);
        contactDetails.setManaged(selected);
        tags.getChildren().clear();
        if (!selected) {
            applicantName.setText("No applicant selected");
            initials.setText("?");
            selectionHint.setText("Select an applicant from the list to view their details.");
            email.setText("");
            phone.setText("");
            address.setText("");
            return;
        }

        applicantName.setText(person.getName().fullName);
        initials.setText(Stream.of(person.getName().fullName.split("\\s+"))
                .limit(2).map(word -> word.substring(0, 1).toUpperCase(java.util.Locale.ROOT))
                .collect(Collectors.joining()));
        selectionHint.setText("Applicant contact information");
        email.setText(person.getEmail().value);
        phone.setText(person.getPhone().value);
        address.setText(person.getAddress().value);
        person.getTags().stream().sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
        if (tags.getChildren().isEmpty()) {
            tags.getChildren().add(new Label("No tags"));
        }
    }
}
