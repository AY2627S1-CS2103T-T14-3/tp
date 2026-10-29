package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.VALID_JOB_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;

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
    }

    @Test
    public void toModelType_validApplicationDetails_returnsApplication() throws Exception {
        JsonAdaptedApplication application = new JsonAdaptedApplication(
                ALICE.getName().fullName, VALID_JOB_AMY, VALID_STATUS);

        assertEquals(new Application(ALICE, new Job(VALID_JOB_AMY), Application.Status.PENDING),
                application.toModelType(ADDRESS_BOOK));
    }

    @Test
    public void toModelType_nullJob_throwsIllegalValueException() {
        JsonAdaptedApplication application = new JsonAdaptedApplication(
                ALICE.getName().fullName, null, VALID_STATUS);

        assertThrows(IllegalValueException.class,
                String.format(JsonAdaptedApplication.MISSING_FIELD_MESSAGE_FORMAT, "job description"), () ->
                        application.toModelType(ADDRESS_BOOK));
    }

    @Test
    public void toModelType_invalidJob_throwsIllegalValueException() {
        JsonAdaptedApplication application = new JsonAdaptedApplication(
                ALICE.getName().fullName, "Janitor -s accept", VALID_STATUS);

        assertThrows(IllegalValueException.class, Job.MESSAGE_CONSTRAINTS, () ->
                application.toModelType(ADDRESS_BOOK));
    }
}
