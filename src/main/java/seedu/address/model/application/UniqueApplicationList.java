package seedu.address.model.application;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.application.exceptions.ApplicationNotFoundException;
import seedu.address.model.application.exceptions.DuplicateApplicationException;
import seedu.address.model.person.Person;

/**
 * A list of applications that enforces one application per applicant and does not allow nulls.
 * An application is considered unique by comparing using {@code Application#isForSameApplicant(Application)}.
 *
 * @see Application#isForSameApplicant(Application)
 */
public class UniqueApplicationList implements Iterable<Application> {

    private final ObservableList<Application> internalList = FXCollections.observableArrayList();
    private final ObservableList<Application> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if an application for {@code applicant} exists in the list.
     * @param applicant applicant to check
     * @return true if an application for {@code applicant} exists in the list
     */
    public boolean contains(Person applicant) {
        requireNonNull(applicant);
        return get(applicant).isPresent();
    }

    /**
     * Returns the application for {@code applicant}, if one exists.
     * @param applicant applicant whose application is requested
     * @return the application for {@code applicant}, if one exists
     */
    public Optional<Application> get(Person applicant) {
        requireNonNull(applicant);
        return internalList.stream()
                .filter(application -> application.getApplicant().isSamePerson(applicant))
                .findFirst();
    }

    /**
     * Adds an application to the list.
     * The applicant must not already have an application.
     * @param toAdd application to add
     */
    public void add(Application toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd.getApplicant())) {
            throw new DuplicateApplicationException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the stored applicant {@code target} with {@code editedPerson}, keeping the job and status.
     * Does nothing when {@code target} has no application.
     * @param target applicant currently stored on the application
     * @param editedPerson applicant to store instead
     */
    public void setApplicant(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);
        get(target).ifPresent(application -> {
            int index = internalList.indexOf(application);
            internalList.set(index, new Application(
                    editedPerson, application.getJob(), application.getStatus()));
        });
    }

    /**
     * Replaces {@code target} with {@code editedApplication}.
     * {@code target} must already be in the list, and both applications must belong to the same applicant.
     * @param target application currently stored
     * @param editedApplication application to store instead
     */
    public void setApplication(Application target, Application editedApplication) {
        requireAllNonNull(target, editedApplication);
        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new ApplicationNotFoundException();
        }
        if (!target.isForSameApplicant(editedApplication)) {
            throw new DuplicateApplicationException();
        }
        internalList.set(index, editedApplication);
    }

    /**
     * Removes the application belonging to {@code applicant}, if one exists.
     * @param applicant applicant whose application should be removed
     */
    public void remove(Person applicant) {
        requireNonNull(applicant);
        get(applicant).ifPresent(internalList::remove);
    }

    /**
     * Removes every application for {@code job}, comparing jobs by their descriptions.
     *
     * @param job job whose applications should be removed.
     */
    public void removeForJob(Job job) {
        requireNonNull(job);
        internalList.removeIf(application -> application.getJob().equals(job));
    }

    /**
     * Replaces the contents of this list with {@code replacement}.
     * @param replacement list of applications to copy
     */
    public void setApplications(UniqueApplicationList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code applications}.
     * {@code applications} must not contain more than one application for the same applicant.
     * @param applications applications to store
     */
    public void setApplications(List<Application> applications) {
        requireAllNonNull(applications);
        if (!applicationsAreUnique(applications)) {
            throw new DuplicateApplicationException();
        }

        internalList.setAll(applications);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Application> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<Application> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UniqueApplicationList otherUniqueApplicationList)) {
            return false;
        }

        return internalList.equals(otherUniqueApplicationList.internalList);
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
     * Returns true if {@code applications} contains at most one application per applicant.
     * @param applications applications to check
     * @return true if no two applications belong to the same applicant
     */
    private boolean applicationsAreUnique(List<Application> applications) {
        for (int i = 0; i < applications.size() - 1; i++) {
            for (int j = i + 1; j < applications.size(); j++) {
                if (applications.get(i).isForSameApplicant(applications.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
