package seedu.address.model;

import java.util.Optional;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Applicant> PREDICATE_SHOW_ALL_APPLICANTS = unused -> true;

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
     * Returns true if a applicant with the same name or email as {@code applicant} exists in the address book.
     */
    boolean hasApplicant(Applicant applicant);

    /**
     * Deletes the given applicant.
     * The applicant must exist in the address book.
     */
    void deleteApplicant(Applicant target);

    /**
     * Adds the given applicant.
     * {@code applicant} must not already exist in the address book.
     */
    void addApplicant(Applicant applicant);

    /**
     * Replaces the given applicant {@code target} with {@code editedApplicant}.
     * {@code target} must exist in the address book.
     * The applicant identity of {@code editedApplicant} must not be the same as another existing applicant
     * in the address book.
     */
    void setApplicant(Applicant target, Applicant editedApplicant);

    /**
     * Returns true if {@code applicant} already has an application.
     * @param applicant applicant to check
     * @return true if {@code applicant} already has an application
     */
    boolean hasApplication(Applicant applicant);

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
     * Returns the application recorded for {@code applicant}, if any.
     * @param applicant applicant whose application is requested
     * @return the application recorded for {@code applicant}, if any
     */
    Optional<Application> getApplication(Applicant applicant);

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

    /** Returns an unmodifiable view of the filtered applicant list */
    ObservableList<Applicant> getFilteredApplicantList();

    /**
     * Updates the filter of the filtered applicant list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredApplicantList(Predicate<Applicant> predicate);
}
