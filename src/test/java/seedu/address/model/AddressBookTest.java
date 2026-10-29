package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.BOB;
import static seedu.address.testutil.TypicalApplicants.getTypicalAddressBook;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.exceptions.DuplicateApplicantException;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;
import seedu.address.model.application.exceptions.ApplicationNotFoundException;
import seedu.address.model.application.exceptions.DuplicateApplicationException;
import seedu.address.model.application.exceptions.DuplicateJobException;
import seedu.address.testutil.ApplicantBuilder;

public class AddressBookTest {

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(List.of(), addressBook.getApplicantList());
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
    public void resetData_withDuplicateApplicants_throwsDuplicateApplicantException() {
        // Two applicants with the same identity fields
        Applicant editedAlice = new ApplicantBuilder(ALICE).withEmail(VALID_EMAIL_BOB)
                .build();
        List<Applicant> newApplicants = List.of(ALICE, editedAlice);
        AddressBookStub newData = new AddressBookStub(newApplicants);

        assertThrows(DuplicateApplicantException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void hasApplicant_nullApplicant_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasApplicant(null));
    }

    @Test
    public void hasApplicant_applicantNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasApplicant(ALICE));
    }

    @Test
    public void hasApplicant_applicantInAddressBook_returnsTrue() {
        addressBook.addApplicant(ALICE);
        assertTrue(addressBook.hasApplicant(ALICE));
    }

    @Test
    public void hasApplicant_applicantWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addApplicant(ALICE);
        Applicant editedAlice = new ApplicantBuilder(ALICE).withEmail(VALID_EMAIL_BOB)
                .build();
        assertTrue(addressBook.hasApplicant(editedAlice));
    }

    @Test
    public void getApplicantList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getApplicantList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName() + "{applicants=" + addressBook.getApplicantList()
                + ", applications=" + addressBook.getApplicationList()
                + ", jobs=" + addressBook.getJobList() + "}";
        assertEquals(expected, addressBook.toString());
    }

    @Test
    public void addApplication_newApplicant_success() {
        addressBook.addApplicant(ALICE);
        Application application = new Application(ALICE, new Job("Janitor"), Application.Status.PENDING);
        addressBook.addApplication(application);

        assertTrue(addressBook.hasApplication(ALICE));
        assertEquals(application, addressBook.getApplication(ALICE).orElseThrow());
    }

    @Test
    public void addApplication_duplicateApplicant_throwsDuplicateApplicationException() {
        addressBook.addApplicant(ALICE);
        addressBook.addApplication(new Application(ALICE, new Job("Janitor"), Application.Status.PENDING));
        Application duplicate = new Application(
                new ApplicantBuilder(ALICE).withPhone("91234567").build(), new Job("Engineer"),
                Application.Status.ACCEPT);

        assertThrows(DuplicateApplicationException.class, () -> addressBook.addApplication(duplicate));
    }

    @Test
    public void setApplication_existingApplication_updatesStatusAndKeepsJob() {
        addressBook.addApplicant(ALICE);
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
    public void setApplicant_applicantWithApplication_updatesStoredApplicant() {
        addressBook.addApplicant(ALICE);
        addressBook.addApplication(new Application(ALICE, new Job("Janitor"), Application.Status.PENDING));
        Applicant editedAlice = new ApplicantBuilder(ALICE).withPhone("91234567").build();

        addressBook.setApplicant(ALICE, editedAlice);

        Application updated = addressBook.getApplication(editedAlice).orElseThrow();
        assertEquals(editedAlice, updated.getApplicant());
        assertEquals(new Job("Janitor"), updated.getJob());
        assertEquals(Application.Status.PENDING, updated.getStatus());
    }

    @Test
    public void removeApplicant_applicantWithApplication_removesApplication() {
        addressBook.addApplicant(ALICE);
        addressBook.addApplicant(BOB);
        addressBook.addApplication(new Application(ALICE, new Job("Janitor"), Application.Status.PENDING));
        addressBook.addApplication(new Application(BOB, new Job("Engineer"), Application.Status.ACCEPT));

        addressBook.removeApplicant(ALICE);

        assertFalse(addressBook.hasApplication(ALICE));
        assertTrue(addressBook.hasApplication(BOB));
    }

    @Test
    public void resetData_withApplications_copiesApplications() {
        AddressBook original = new AddressBook();
        original.addApplicant(ALICE);
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
    public void resetData_withJobs_copiesJobs() {
        AddressBook original = new AddressBook();
        original.addJob(new Job("Product Designer"));
        addressBook.resetData(original);
        assertEquals(original, addressBook);
    }

    /**
     * A stub ReadOnlyAddressBook whose applicants list can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Applicant> applicants = FXCollections.observableArrayList();

        AddressBookStub(Collection<Applicant> applicants) {
            this.applicants.setAll(applicants);
        }

        @Override
        public ObservableList<Applicant> getApplicantList() {
            return applicants;
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
