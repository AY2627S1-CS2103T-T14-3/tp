package seedu.address.model.applicant;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.applicant.exceptions.ApplicantNotFoundException;
import seedu.address.model.applicant.exceptions.DuplicateApplicantException;

/**
 * A list of persons that enforces uniqueness between its elements and does not allow nulls.
 * Each applicant's name and email must be unique, as checked by {@code Applicant#hasSameNameOrEmail(Applicant)}.
 * However, the removal of a applicant uses Applicant#equals(Object) so
 * as to ensure that the applicant with exactly the same fields will be removed.
 *
 * Supports a minimal set of list operations.
 *
 * @see Applicant#hasSameNameOrEmail(Applicant)
 */
public class UniqueApplicantList implements Iterable<Applicant> {

    private final ObservableList<Applicant> internalList = FXCollections.observableArrayList();
    private final ObservableList<Applicant> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains a applicant with the same name or email as the given argument.
     */
    public boolean contains(Applicant toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::hasSameNameOrEmail);
    }

    /**
     * Adds a applicant to the list.
     * The applicant must not already exist in the list.
     */
    public void add(Applicant toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateApplicantException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the applicant {@code target} in the list with {@code editedApplicant}.
     * {@code target} must exist in the list.
     * The name and email of {@code editedApplicant} must not match another existing applicant in the list.
     */
    public void setPerson(Applicant target, Applicant editedApplicant) {
        requireAllNonNull(target, editedApplicant);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new ApplicantNotFoundException();
        }

        for (int i = 0; i < internalList.size(); i++) {
            if (i != index && internalList.get(i).hasSameNameOrEmail(editedApplicant)) {
                throw new DuplicateApplicantException();
            }
        }

        internalList.set(index, editedApplicant);
    }

    /**
     * Removes the equivalent applicant from the list.
     * The applicant must exist in the list.
     */
    public void remove(Applicant toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new ApplicantNotFoundException();
        }
    }

    public void setPersons(UniqueApplicantList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code applicants}.
     * {@code applicants} must not contain duplicate applicants.
     */
    public void setPersons(List<Applicant> applicants) {
        requireAllNonNull(applicants);
        if (!personsAreUnique(applicants)) {
            throw new DuplicateApplicantException();
        }

        internalList.setAll(applicants);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Applicant> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<Applicant> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UniqueApplicantList otherUniqueApplicantList)) {
            return false;
        }

        return internalList.equals(otherUniqueApplicantList.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }

    /**
     * Returns true if {@code applicants} contains only unique applicants.
     */
    private boolean personsAreUnique(List<Applicant> applicants) {
        for (int i = 0; i < applicants.size() - 1; i++) {
            for (int j = i + 1; j < applicants.size(); j++) {
                if (applicants.get(i).hasSameNameOrEmail(applicants.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
