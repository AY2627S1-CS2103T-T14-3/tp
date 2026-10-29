package seedu.address.model;

import java.util.Optional;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;
import seedu.address.model.person.Person;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /** {@code Predicate} that always evaluates to true */
    Predicate<Job> PREDICATE_SHOW_ALL_JOBS = unused -> true;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a person with the same name or email as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void setPerson(Person target, Person editedPerson);

    /**
     * Returns true if {@code person} already has an application.
     * @param person applicant to check
     * @return true if {@code person} already has an application
     */
    boolean hasApplication(Person person);

    /**
     * Adds an application for an existing applicant who does not already have one.
     * @param application application to add
     */
    void addApplication(Application application);

    /**
     * Replaces {@code target} with {@code editedApplication}.
     * {@code target} must already be recorded, and both applications must belong to the same applicant.
     * @param target application currently stored
     * @param editedApplication application to store instead
     */
    void setApplication(Application target, Application editedApplication);

    /**
     * Returns the application recorded for {@code person}, if any.
     * @param person applicant whose application is requested
     * @return the application recorded for {@code person}, if any
     */
    Optional<Application> getApplication(Person person);

    /** Returns an unmodifiable view of the applications */
    ObservableList<Application> getApplicationList();

    /**
     * Returns true if a job with the same description as {@code job} exists.
     * @param job job to check
     * @return true if {@code job} is already stored
     */
    boolean hasJob(Job job);

    /**
     * Adds a job.
     * {@code job} must not already exist in the address book.
     * Shows every job afterwards so the added job is visible.
     * @param job job to add
     */
    void addJob(Job job);

    /**
     * Replaces {@code target} with {@code editedJob}, preserving its position in the job list.
     * The target must exist, and the edited description must not match another stored job.
     * Applicant applications remain unchanged.
     */
    void setJob(Job target, Job editedJob);

    /**
     * Deletes the given job.
     * The job must exist in the address book.
     * @param target job to delete
     */
    void deleteJob(Job target);

    /**
     * Returns an unmodifiable view of the full job list.
     * @return every stored job
     */
    ObservableList<Job> getJobList();

    /**
     * Returns an unmodifiable view of the filtered job list.
     * @return jobs that match the current filter
     */
    ObservableList<Job> getFilteredJobList();

    /**
     * Updates the filter of the filtered job list to filter by the given {@code predicate}.
     * @param predicate filter to apply
     * @throws NullPointerException if {@code predicate} is null
     */
    void updateFilteredJobList(Predicate<Job> predicate);

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);
}
