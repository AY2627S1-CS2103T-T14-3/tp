package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.model.person.Person;

/** Identifies an applicant to select in the applicants view. */
public final class ApplicantSelection implements ViewSelection {
    private final Person person;

    public ApplicantSelection(Person person) {
        this.person = requireNonNull(person);
    }

    public Person getPerson() {
        return person;
    }

    @Override
    public ViewType getViewType() {
        return ViewType.APPLICANTS;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ApplicantSelection otherSelection)) {
            return false;
        }
        return person.equals(otherSelection.person);
    }

    @Override
    public int hashCode() {
        return Objects.hash(person);
    }
}
