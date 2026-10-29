package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;
import seedu.address.model.applicant.Applicant;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Applicant> filteredApplicants;
    private final FilteredList<Job> filteredJobs;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredApplicants = new FilteredList<>(this.addressBook.getPersonList());
        filteredJobs = new FilteredList<>(this.addressBook.getJobList());
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Applicant applicant) {
        requireNonNull(applicant);
        return addressBook.hasPerson(applicant);
    }

    @Override
    public void deletePerson(Applicant target) {
        addressBook.removePerson(target);
    }

    @Override
    public void addPerson(Applicant applicant) {
        addressBook.addPerson(applicant);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setPerson(Applicant target, Applicant editedApplicant) {
        requireAllNonNull(target, editedApplicant);

        addressBook.setPerson(target, editedApplicant);
    }

    @Override
    public boolean hasApplication(Applicant applicant) {
        requireNonNull(applicant);
        return addressBook.hasApplication(applicant);
    }

    @Override
    public void addApplication(Application application) {
        requireNonNull(application);
        addressBook.addApplication(application);
    }

    @Override
    public void setApplication(Application target, Application editedApplication) {
        requireAllNonNull(target, editedApplication);
        addressBook.setApplication(target, editedApplication);
    }

    @Override
    public Optional<Application> getApplication(Applicant applicant) {
        requireNonNull(applicant);
        return addressBook.getApplication(applicant);
    }

    @Override
    public ObservableList<Application> getApplicationList() {
        return addressBook.getApplicationList();
    }

    @Override
    public boolean hasJob(Job job) {
        requireNonNull(job);
        return addressBook.hasJob(job);
    }

    @Override
    public void addJob(Job job) {
        requireNonNull(job);
        addressBook.addJob(job);
        updateFilteredJobList(PREDICATE_SHOW_ALL_JOBS);
    }

    @Override
    public void deleteJob(Job target) {
        requireNonNull(target);
        addressBook.removeJob(target);
    }

    @Override
    public ObservableList<Job> getJobList() {
        return addressBook.getJobList();
    }

    @Override
    public ObservableList<Job> getFilteredJobList() {
        return filteredJobs;
    }

    @Override
    public void updateFilteredJobList(Predicate<Job> predicate) {
        requireNonNull(predicate);
        filteredJobs.setPredicate(predicate);
    }

    //=========== Filtered Applicant List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Applicant} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<Applicant> getFilteredPersonList() {
        return filteredApplicants;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Applicant> predicate) {
        requireNonNull(predicate);
        filteredApplicants.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredApplicants.equals(otherModelManager.filteredApplicants)
                && filteredJobs.equals(otherModelManager.filteredJobs);
    }

}
