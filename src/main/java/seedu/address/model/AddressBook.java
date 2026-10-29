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
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;

/**
 * Wraps all data at the address-book level.
 * Applicant names and emails must each be unique.
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons = new UniquePersonList();
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
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        setPersons(newData.getPersonList());
        setApplications(newData.getApplicationList());
        setJobs(newData.getJobList());
    }

    //// person-level operations

    /**
     * Returns true if a person with the same name or email as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The name and email of {@code editedPerson} must not match another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);
        applications.setApplicant(target, editedPerson);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     * Also removes that person's application, if one exists.
     * @param key person to remove
     */
    public void removePerson(Person key) {
        persons.remove(key);
        applications.remove(key);
    }

    /**
     * Returns true if {@code person} already has an application.
     * @param person applicant to check
     * @return true if {@code person} already has an application
     */
    public boolean hasApplication(Person person) {
        requireNonNull(person);
        return applications.contains(person);
    }

    /**
     * Returns the application for {@code person}, if one has been recorded.
     * @param person applicant whose application is requested
     * @return the application recorded for {@code person}, if any
     */
    public Optional<Application> getApplication(Person person) {
        requireNonNull(person);
        return applications.get(person);
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
     * Replaces an existing job while preserving its position and leaving applications unchanged.
     * The edited description must not match another stored job.
     */
    public void setJob(Job target, Job editedJob) {
        jobs.setJob(target, editedJob);
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
                .add("persons", persons)
                .add("applications", applications)
                .add("jobs", jobs)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
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

        return persons.equals(otherAddressBook.persons)
                && applications.equals(otherAddressBook.applications)
                && jobs.equals(otherAddressBook.jobs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persons, applications, jobs);
    }
}
