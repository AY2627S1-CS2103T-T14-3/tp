package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.application.Application;
import seedu.address.model.application.UniqueApplicationList;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSamePerson comparison).
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons = new UniquePersonList();
    private final UniqueApplicationList applications = new UniqueApplicationList();

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
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
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
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
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
     * Replaces the application list with {@code applications}.
     * {@code applications} must not contain more than one application per applicant.
     * @param applications applications to store
     */
    public void setApplications(List<Application> applications) {
        this.applications.setApplications(applications);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .add("applications", applications)
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
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return persons.equals(otherAddressBook.persons)
                && applications.equals(otherAddressBook.applications);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persons, applications);
    }
}
