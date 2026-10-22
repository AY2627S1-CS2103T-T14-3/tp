package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class CommandResultTest {
    @Test
    public void constructor_withoutViewRequest_doesNotRequestViewChange() {
        assertTrue(new CommandResult("feedback").getViewRequest().isEmpty());
        assertTrue(new CommandResult("feedback", true, false).getViewRequest().isEmpty());
        assertTrue(new CommandResult("feedback", false, true).getViewRequest().isEmpty());
    }

    @Test
    public void constructor_withApplicant_requestsSelection() {
        CommandResult commandResult = new CommandResult("feedback", ViewRequest.applicant(ALICE));
        ViewRequest request = commandResult.getViewRequest().orElseThrow();
        ApplicantSelection selection = (ApplicantSelection) request.getSelection().orElseThrow();

        assertEquals(ViewType.APPLICANTS, request.getViewType());
        assertEquals(ALICE, selection.getPerson());
        assertFalse(commandResult.isShowHelp());
        assertFalse(commandResult.isExit());
    }

    @Test
    public void constructor_withApplicationsView_requestsViewChange() {
        CommandResult result = new CommandResult("feedback", ViewRequest.applications());
        ViewRequest request = result.getViewRequest().orElseThrow();

        assertEquals(ViewType.APPLICATIONS, request.getViewType());
        assertTrue(request.getSelection().isEmpty());
    }

    @Test
    public void equals_differentViews_areDifferent() {
        CommandResult applicants = new CommandResult("feedback", ViewRequest.applicants());
        CommandResult applications = new CommandResult("feedback", ViewRequest.applications());
        assertNotEquals(applicants, applications);
        assertNotEquals(applicants.hashCode(), applications.hashCode());
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CommandResult("feedback", ViewRequest.applicant(null)));
        assertThrows(NullPointerException.class, () -> new CommandResult(null, ViewRequest.applicants()));
    }

    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("feedback");

        assertTrue(commandResult.equals(new CommandResult("feedback")));
        assertTrue(commandResult.equals(new CommandResult("feedback", false, false)));
        assertTrue(commandResult.equals(commandResult));
        assertFalse(commandResult.equals(null));
        assertFalse(commandResult.equals(0.5f));
        assertFalse(commandResult.equals(new CommandResult("different")));
        assertFalse(commandResult.equals(new CommandResult("feedback", true, false)));
        assertFalse(commandResult.equals(new CommandResult("feedback", false, true)));

        CommandResult viewResult = new CommandResult("feedback", ViewRequest.applicant(ALICE));
        assertFalse(commandResult.equals(viewResult));
        assertFalse(viewResult.equals(commandResult));
        assertFalse(viewResult.equals(new CommandResult("feedback", ViewRequest.applicant(BENSON))));
        assertTrue(viewResult.equals(new CommandResult("feedback",
                ViewRequest.applicant(new PersonBuilder(ALICE).build()))));
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult("feedback");

        assertEquals(commandResult.hashCode(), new CommandResult("feedback").hashCode());
        assertNotEquals(commandResult.hashCode(), new CommandResult("different").hashCode());
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", true, false).hashCode());
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", false, true).hashCode());

        CommandResult viewResult = new CommandResult("feedback", ViewRequest.applicant(ALICE));
        assertEquals(viewResult.hashCode(), new CommandResult("feedback",
                ViewRequest.applicant(new PersonBuilder(ALICE).build())).hashCode());
        assertNotEquals(commandResult.hashCode(), viewResult.hashCode());
        assertNotEquals(viewResult.hashCode(),
                new CommandResult("feedback", ViewRequest.applicant(BENSON)).hashCode());
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback");
        String expected = CommandResult.class.getCanonicalName() + "{feedbackToUser="
                + commandResult.getFeedbackToUser() + ", showHelp=" + commandResult.isShowHelp()
                + ", exit=" + commandResult.isExit()
                + ", viewRequest=Optional.empty}";
        assertEquals(expected, commandResult.toString());
    }
}
