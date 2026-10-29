package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;
import seedu.address.model.application.UniqueApplicationList;
import seedu.address.model.application.UniqueJobList;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.UniqueApplicantList;

/**
 * Wraps all data at the address-book level.
 * Applicant names and emails must each be unique.
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniqueApplicantList applicants = new UniqueApplicantList();
    private final UniqueApplicationList applications = new UniqueApplicationList();
    private final UniqueJobList jobs = new UniqueJobList();

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the applicant list with {@code applicants}.
     * {@code applicants} must not contain duplicate applicants.
     */
    public void setApplicants(List<Applicant> applicants) {
        this.applicants.setPersons(applicants);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        setApplicants(newData.getPersonList());
        setApplications(newData.getApplicationList());
        setJobs(newData.getJobList());
    }

    //// applicant-level operations

    /**
     * Returns true if a applicant with the same name or email as {@code applicant} exists in the address book.
     */
    public boolean hasPerson(Applicant applicant) {
        requireNonNull(applicant);
        return applicants.contains(applicant);
    }

    /**
     * Adds a applicant to the address book.
     * The applicant must not already exist in the address book.
     */
    public void addPerson(Applicant p) {
        applicants.add(p);
    }

    /**
     * Replaces the given applicant {@code target} in the list with {@code editedApplicant}.
     * {@code target} must exist in the address book.
     * The name and email of {@code editedApplicant} must not match another existing applicant in the address book.
     */
    public void setPerson(Applicant target, Applicant editedApplicant) {
        requireNonNull(editedApplicant);

        applicants.setPerson(target, editedApplicant);
        applications.setApplicant(target, editedApplicant);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     * Also removes that applicant's application, if one exists.
     * @param key applicant to remove
     */
    public void removePerson(Applicant key) {
        applicants.remove(key);
        applications.remove(key);
    }

    /**
     * Returns true if {@code applicant} already has an application.
     * @param applicant applicant to check
     * @return true if {@code applicant} already has an application
     */
    public boolean hasApplication(Applicant applicant) {
        requireNonNull(applicant);
        return applications.contains(applicant);
    }

    /**
     * Returns the application for {@code applicant}, if one has been recorded.
     * @param applicant applicant whose application is requested
     * @return the application recorded for {@code applicant}, if any
     */
    public Optional<Application> getApplication(Applicant applicant) {
        requireNonNull(applicant);
        return applications.get(applicant);
    }

    /**
     * Adds an application to the address book.
     * The applicant must already exist and must not already have an application.
     * @param application application to add
     */
    public void addApplication(Application application) {
        applications.add(application);
    }

    /**
     * Replaces {@code target} with {@code editedApplication}.
     * {@code target} must already be recorded, and both applications must belong to the same applicant.
     * @param target application currently stored
     * @param editedApplication application to store instead
     */
    public void setApplication(Application target, Application editedApplication) {
        applications.setApplication(target, editedApplication);
    }

    /**
     * Replaces the application list with {@code applications}.
     * {@code applications} must not contain more than one application per applicant.
     * @param applications applications to store
     */
    public void setApplications(List<Application> applications) {
        this.applications.setApplications(applications);
    }

    /**
     * Returns true if a job with the same description as {@code job} exists.
     * @param job job to check
     * @return true if {@code job} is already stored
     */
    public boolean hasJob(Job job) {
        requireNonNull(job);
        return jobs.contains(job);
    }

    /**
     * Adds a job to the address book.
     * The job must not already exist in the address book.
     * @param job job to add
     */
    public void addJob(Job job) {
        assert job != null : "job must not be null";
        jobs.add(job);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     * @param key job to remove
     */
    public void removeJob(Job key) {
        assert key != null : "key must not be null";
        assert jobs.contains(key) : "job must exist in the address book";
        jobs.remove(key);
    }

    /**
     * Replaces the job list with {@code jobs}.
     * {@code jobs} must not contain duplicate jobs.
     * @param jobs jobs to store
     */
    public void setJobs(List<Job> jobs) {
        assert jobs != null : "jobs must not be null";
        assert jobs.stream().allMatch(Objects::nonNull) : "jobs must not contain null";
        assert jobs.stream().distinct().count() == jobs.size() : "jobs must not contain duplicates";
        this.jobs.setJobs(jobs);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("applicants", applicants)
                .add("applications", applications)
                .add("jobs", jobs)
                .toString();
    }

    @Override
    public ObservableList<Applicant> getPersonList() {
        return applicants.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Application> getApplicationList() {
        return applications.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Job> getJobList() {
        return jobs.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return applicants.equals(otherAddressBook.applicants)
                && applications.equals(otherAddressBook.applications)
                && jobs.equals(otherAddressBook.jobs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(applicants, applications, jobs);
    }
}
