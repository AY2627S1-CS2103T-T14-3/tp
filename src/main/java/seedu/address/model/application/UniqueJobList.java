package seedu.address.model.application;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.application.exceptions.DuplicateJobException;
import seedu.address.model.application.exceptions.JobNotFoundException;

/**
 * A list of jobs that enforces uniqueness between its elements and does not allow nulls.
 * A job is unique by {@code Job#equals(Object)}, which compares descriptions.
 */
public class UniqueJobList implements Iterable<Job> {

    private final ObservableList<Job> internalList = FXCollections.observableArrayList();
    private final ObservableList<Job> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent job.
     * @param toCheck job to look for
     * @return true if an equivalent job is already stored
     */
    public boolean contains(Job toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::equals);
    }

    /**
     * Adds a job to the list.
     * The job must not already exist in the list.
     * @param toAdd job to add
     */
    public void add(Job toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateJobException();
        }
        internalList.add(toAdd);
    }

    /**
     * Removes the equivalent job from the list.
     * The job must exist in the list.
     * @param toRemove job to remove
     */
    public void remove(Job toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new JobNotFoundException();
        }
    }

    /**
     * Replaces the contents of this list with {@code replacement}.
     * @param replacement list of jobs to copy
     */
    public void setJobs(UniqueJobList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code jobs}.
     * {@code jobs} must not contain duplicate jobs.
     * @param jobs jobs to store
     */
    public void setJobs(List<Job> jobs) {
        requireAllNonNull(jobs);
        if (!jobsAreUnique(jobs)) {
            throw new DuplicateJobException();
        }
        internalList.setAll(jobs);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     * @return the jobs in this list
     */
    public ObservableList<Job> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<Job> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UniqueJobList otherUniqueJobList)) {
            return false;
        }
        return internalList.equals(otherUniqueJobList.internalList);
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
     * Returns true if {@code jobs} contains only unique jobs.
     * @param jobs jobs to check
     * @return true if no two jobs are equal
     */
    private boolean jobsAreUnique(List<Job> jobs) {
        for (int i = 0; i < jobs.size() - 1; i++) {
            for (int j = i + 1; j < jobs.size(); j++) {
                if (jobs.get(i).equals(jobs.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
