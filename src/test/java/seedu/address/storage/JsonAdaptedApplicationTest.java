package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.VALID_JOB_AMY;
import static seedu.address.storage.JsonAdaptedApplication.MESSAGE_UNKNOWN_JOB;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalJobs.ENGINEER;
import static seedu.address.testutil.TypicalJobs.JANITOR;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;

public class JsonAdaptedApplicationTest {

    private static final String VALID_STATUS = Application.Status.PENDING.getValue();
    private static final AddressBook ADDRESS_BOOK = new AddressBook();

    static {
        ADDRESS_BOOK.addApplicant(ALICE);
        ADDRESS_BOOK.addJob(JANITOR);
    }

    @Test
    public void toModelType_validApplicationDetails_returnsApplication() throws Exception {
        JsonAdaptedApplication application = new JsonAdaptedApplication(
                ALICE.getName().fullName, new JsonAdaptedJob(JANITOR), VALID_STATUS);

        assertEquals(new Application(ALICE, new Job(VALID_JOB_AMY), Application.Status.PENDING),
                application.toModelType(ADDRESS_BOOK));
    }

    @Test
    public void toModelType_nullJob_throwsIllegalValueException() {
        JsonAdaptedApplication application = new JsonAdaptedApplication(
                ALICE.getName().fullName, null, VALID_STATUS);

        assertThrows(IllegalValueException.class,
                String.format(JsonAdaptedApplication.MISSING_FIELD_MESSAGE_FORMAT, "job"), () ->
                        application.toModelType(ADDRESS_BOOK));
    }

    @Test
    public void toModelType_invalidJob_throwsIllegalValueException() {
        JsonAdaptedApplication application = new JsonAdaptedApplication(
                ALICE.getName().fullName, new JsonAdaptedJob(ENGINEER), VALID_STATUS);

        assertThrows(IllegalValueException.class, MESSAGE_UNKNOWN_JOB, () ->
                application.toModelType(ADDRESS_BOOK));
    }
}
