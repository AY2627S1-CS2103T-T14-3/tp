package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import org.junit.jupiter.api.Test;

import seedu.address.model.applicant.Applicant;
import seedu.address.testutil.ApplicantBuilder;

public class CommandResultTest {
    @Test
    public void constructor_withoutApplicant_doesNotRequestSelection() {
        assertTrue(new CommandResult("feedback").getPersonToView().isEmpty());
        assertTrue(new CommandResult("feedback", true, false).getPersonToView().isEmpty());
        assertTrue(new CommandResult("feedback", false, true).getPersonToView().isEmpty());
    }

    @Test
    public void constructor_withApplicant_requestsSelection() {
        CommandResult commandResult = new CommandResult("feedback", ALICE);
        assertEquals(ALICE, commandResult.getPersonToView().orElseThrow());
        assertFalse(commandResult.isShowHelp());
        assertFalse(commandResult.isExit());
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CommandResult("feedback", (Applicant) null));
        assertThrows(NullPointerException.class, () -> new CommandResult(null, ALICE));
    }

    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns true
        assertTrue(commandResult.equals(new CommandResult("feedback")));
        assertTrue(commandResult.equals(new CommandResult("feedback", false, false)));

        // same object -> returns true
        assertTrue(commandResult.equals(commandResult));

        // null -> returns false
        assertFalse(commandResult.equals(null));

        // different types -> returns false
        assertFalse(commandResult.equals(0.5f));

        // different feedbackToUser value -> returns false
        assertFalse(commandResult.equals(new CommandResult("different")));

        // different showHelp value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", true, false)));

        // different exit value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", false, true)));

        // different applicant selection -> returns false
        CommandResult viewResult = new CommandResult("feedback", ALICE);
        assertFalse(commandResult.equals(viewResult));
        assertFalse(viewResult.equals(commandResult));
        assertFalse(viewResult.equals(new CommandResult("feedback", BENSON)));

        // equal applicants -> returns true
        assertTrue(viewResult.equals(new CommandResult("feedback", new ApplicantBuilder(ALICE).build())));
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns same hashcode
        assertEquals(commandResult.hashCode(), new CommandResult("feedback").hashCode());

        // different feedbackToUser value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("different").hashCode());

        // different showHelp value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", true, false).hashCode());

        // different exit value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", false, true).hashCode());

        // equal applicants -> returns same hashcode
        CommandResult viewResult = new CommandResult("feedback", ALICE);
        assertEquals(viewResult.hashCode(),
                new CommandResult("feedback", new ApplicantBuilder(ALICE).build()).hashCode());

        // different applicant selections -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), viewResult.hashCode());
        assertNotEquals(viewResult.hashCode(), new CommandResult("feedback", BENSON).hashCode());
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback");
        String expected = CommandResult.class.getCanonicalName() + "{feedbackToUser="
                + commandResult.getFeedbackToUser() + ", showHelp=" + commandResult.isShowHelp()
                + ", exit=" + commandResult.isExit()
                + ", applicantToView=null, showJobs=false, jobToView=null}";
        assertEquals(expected, commandResult.toString());
    }
}
