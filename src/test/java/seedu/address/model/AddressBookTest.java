package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;
import seedu.address.model.application.exceptions.ApplicationNotFoundException;
import seedu.address.model.application.exceptions.DuplicateApplicationException;
import seedu.address.model.application.exceptions.DuplicateJobException;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.testutil.PersonBuilder;

public class AddressBookTest {

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(List.of(), addressBook.getPersonList());
        assertEquals(List.of(), addressBook.getApplicationList());
        assertEquals(List.of(), addressBook.getJobList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicatePersons_throwsDuplicatePersonException() {
        // Two persons with the same identity fields
        Person editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB)
                .build();
        List<Person> newPersons = List.of(ALICE, editedAlice);
        AddressBookStub newData = new AddressBookStub(newPersons);

        assertThrows(DuplicatePersonException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        assertTrue(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB)
                .build();
        assertTrue(addressBook.hasPerson(editedAlice));
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getPersonList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName() + "{persons=" + addressBook.getPersonList()
                + ", applications=" + addressBook.getApplicationList()
                + ", jobs=" + addressBook.getJobList() + "}";
        assertEquals(expected, addressBook.toString());
    }

    @Test
    public void addApplication_newApplicant_success() {
        addressBook.addPerson(ALICE);
        Application application = new Application(ALICE, new Job("Janitor"), Application.Status.PENDING);
        addressBook.addApplication(application);

        assertTrue(addressBook.hasApplication(ALICE));
        assertEquals(application, addressBook.getApplication(ALICE).orElseThrow());
    }

    @Test
    public void addApplication_duplicateApplicant_throwsDuplicateApplicationException() {
        addressBook.addPerson(ALICE);
        addressBook.addApplication(new Application(ALICE, new Job("Janitor"), Application.Status.PENDING));
        Application duplicate = new Application(
                new PersonBuilder(ALICE).withPhone("91234567").build(), new Job("Engineer"), Application.Status.ACCEPT);

        assertThrows(DuplicateApplicationException.class, () -> addressBook.addApplication(duplicate));
    }

    @Test
    public void setApplication_existingApplication_updatesStatusAndKeepsJob() {
        addressBook.addPerson(ALICE);
        Application original = new Application(ALICE, new Job("Janitor"), Application.Status.PENDING);
        addressBook.addApplication(original);

        Application updated = new Application(ALICE, new Job("Janitor"), Application.Status.ACCEPT);
        addressBook.setApplication(original, updated);

        assertEquals(updated, addressBook.getApplication(ALICE).orElseThrow());
    }

    @Test
    public void setApplication_missingApplication_throwsApplicationNotFoundException() {
        Application original = new Application(ALICE, new Job("Janitor"), Application.Status.PENDING);
        Application updated = new Application(ALICE, new Job("Janitor"), Application.Status.ACCEPT);

        assertThrows(ApplicationNotFoundException.class, () -> addressBook.setApplication(original, updated));
    }

    @Test
    public void setPerson_applicantWithApplication_updatesStoredApplicant() {
        addressBook.addPerson(ALICE);
        addressBook.addApplication(new Application(ALICE, new Job("Janitor"), Application.Status.PENDING));
        Person editedAlice = new PersonBuilder(ALICE).withPhone("91234567").build();

        addressBook.setPerson(ALICE, editedAlice);

        Application updated = addressBook.getApplication(editedAlice).orElseThrow();
        assertEquals(editedAlice, updated.getApplicant());
        assertEquals(new Job("Janitor"), updated.getJob());
        assertEquals(Application.Status.PENDING, updated.getStatus());
    }

    @Test
    public void removePerson_applicantWithApplication_removesApplication() {
        addressBook.addPerson(ALICE);
        addressBook.addPerson(BOB);
        addressBook.addJob(new Job("Janitor"));
        addressBook.addJob(new Job("Engineer"));
        addressBook.addApplication(new Application(ALICE, new Job("Janitor"), Application.Status.PENDING));
        addressBook.addApplication(new Application(BOB, new Job("Engineer"), Application.Status.ACCEPT));

        addressBook.removePerson(ALICE);

        assertFalse(addressBook.hasApplication(ALICE));
        assertTrue(addressBook.hasApplication(BOB));
        assertEquals(List.of(new Job("Janitor"), new Job("Engineer")), addressBook.getJobList());
    }

    @Test
    public void resetData_withApplications_copiesApplications() {
        AddressBook original = new AddressBook();
        original.addPerson(ALICE);
        original.addApplication(new Application(ALICE, new Job("Janitor"), Application.Status.ACCEPT));

        addressBook.resetData(original);

        assertEquals(original, addressBook);
    }

    @Test
    public void addJob_newJob_success() {
        Job job = new Job("Software Engineer");
        addressBook.addJob(job);
        assertTrue(addressBook.hasJob(job));
        assertEquals(List.of(job), addressBook.getJobList());
    }

    @Test
    public void addJob_duplicate_throwsDuplicateJobException() {
        addressBook.addJob(new Job("Software Engineer"));
        assertThrows(DuplicateJobException.class, () -> addressBook.addJob(new Job("Software Engineer")));
    }

    @Test
    public void removeJob_existingJob_removesJob() {
        Job job = new Job("Data Analyst");
        addressBook.addJob(job);
        addressBook.removeJob(job);
        assertFalse(addressBook.hasJob(job));
    }

    @Test
    public void removeJob_multipleApplications_removesAllMatchingApplicationsAndKeepsApplicants() {
        AddressBook original = getTypicalAddressBook();
        addressBook.resetData(original);
        Job job = new Job("Engineer");
        Job otherJob = new Job("engineer");
        addressBook.addJob(job);
        addressBook.addJob(otherJob);
        List<Person> applicants = addressBook.getPersonList();
        for (int i = 0; i < Application.Status.values().length; i++) {
            addressBook.addApplication(new Application(applicants.get(i), new Job("Engineer"),
                    Application.Status.values()[i]));
        }
        Application otherApplication = new Application(applicants.getLast(), otherJob, Application.Status.PENDING);
        addressBook.addApplication(otherApplication);

        addressBook.removeJob(new Job("Engineer"));

        assertEquals(original.getPersonList(), addressBook.getPersonList());
        assertEquals(List.of(otherJob), addressBook.getJobList());
        assertEquals(List.of(otherApplication), addressBook.getApplicationList());
    }

    @Test
    public void resetData_withJobs_copiesJobs() {
        AddressBook original = new AddressBook();
        original.addJob(new Job("Product Designer"));
        addressBook.resetData(original);
        assertEquals(original, addressBook);
    }

    /**
     * A stub ReadOnlyAddressBook whose persons list can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();

        AddressBookStub(Collection<Person> persons) {
            this.persons.setAll(persons);
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }

        @Override
        public ObservableList<Application> getApplicationList() {
            return FXCollections.observableArrayList();
        }

        @Override
        public ObservableList<Job> getJobList() {
            return FXCollections.observableArrayList();
        }
    }

}
