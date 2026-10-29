package seedu.address.model.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class ApplicationTest {

    @Test
    public void constructor_validApplication_success() {
        Application application = new Application(ALICE, new Job("Janitor"), Application.Status.PENDING);

        assertEquals(ALICE, application.getApplicant());
        assertEquals(new Job("Janitor"), application.getJob());
        assertEquals(Application.Status.PENDING, application.getStatus());
    }

    @Test
    public void applicantDisplayMethods_returnApplicantDetails() {
        Application application = new Application(ALICE, new Job("Janitor"), Application.Status.PENDING);

        assertEquals(ALICE.getDisplayName(), application.getApplicantName());
        assertEquals("pending", application.getStatusValue());
        assertTrue(application.isForApplicant(ALICE));
        assertFalse(application.isForApplicant(BOB));
    }

    @Test
    public void constructor_trimsJobDescription_success() {
        Application application = new Application(ALICE, new Job("  Janitor  "), Application.Status.ACCEPT);

        assertEquals(new Job("Janitor"), application.getJob());
    }

    @Test
    public void constructor_invalidValues_throwsException() {
        assertThrows(NullPointerException.class, () -> new Application(null,
                new Job("Janitor"), Application.Status.PENDING));
        assertThrows(NullPointerException.class, () -> new Application(ALICE, null, Application.Status.PENDING));
        assertThrows(NullPointerException.class, () -> new Application(ALICE, new Job("Janitor"), null));
        assertThrows(IllegalArgumentException.class, ()
                -> new Application(ALICE, new Job("   "), Application.Status.PENDING));
    }

    @Test
    public void status_fromValue_isCaseSensitive() {
        assertEquals(Application.Status.ACCEPT, Application.Status.fromValue("accept"));
        assertEquals(Application.Status.REJECT, Application.Status.fromValue("reject"));
        assertEquals(Application.Status.PENDING, Application.Status.fromValue("pending"));
        assertThrows(IllegalArgumentException.class, () -> Application.Status.fromValue("ACCEPT"));
    }

    @Test
    public void equals_sameValues_success() {
        Application first = new Application(ALICE, new Job("Janitor"), Application.Status.PENDING);
        Application second = new Application(ALICE, new Job("Janitor"), Application.Status.PENDING);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void equals_differentValues_failure() {
        Application alicePending = new Application(ALICE, new Job("Janitor"), Application.Status.PENDING);
        Application aliceAccept = new Application(ALICE, new Job("Janitor"), Application.Status.ACCEPT);
        Application aliceReject = new Application(ALICE, new Job("Janitor"), Application.Status.REJECT);
        Application aliceNotJanitor = new Application(ALICE, new Job("Not Janitor"), Application.Status.PENDING);
        Application bobPending = new Application(BOB, new Job("Janitor"), Application.Status.PENDING);

        assertFalse(alicePending.equals(aliceAccept));
        assertFalse(alicePending.equals(aliceReject));
        assertFalse(alicePending.equals(aliceNotJanitor));
        assertFalse(aliceAccept.equals(aliceReject));
        assertFalse(alicePending.equals(bobPending));
    }

    @Test
    public void isForSameApplicant_variousApplicants_returnsExpectedResult() {
        Application aliceApplication = new Application(ALICE, new Job("Janitor"), Application.Status.PENDING);
        Application sameApplicantApplication = new Application(
                new PersonBuilder(ALICE).withPhone("91234567").build(),
                new Job("Engineer"), Application.Status.ACCEPT);
        Application bobApplication = new Application(BOB, new Job("Janitor"), Application.Status.PENDING);

        assertTrue(aliceApplication.isForSameApplicant(aliceApplication));
        assertTrue(aliceApplication.isForSameApplicant(sameApplicantApplication));
        assertFalse(aliceApplication.isForSameApplicant(null));
        assertFalse(aliceApplication.isForSameApplicant(bobApplication));
    }
}
