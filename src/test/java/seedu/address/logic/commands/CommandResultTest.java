package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.BENSON;

import org.junit.jupiter.api.Test;

import seedu.address.model.application.Application;
import seedu.address.model.application.Job;
import seedu.address.testutil.ApplicantBuilder;

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
        assertEquals(ALICE, selection.getApplicant());
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
        CommandResult viewResult = new CommandResult("feedback", ViewRequest.applicant(ALICE));
        assertFalse(commandResult.equals(viewResult));
        assertFalse(viewResult.equals(commandResult));
        assertFalse(viewResult.equals(new CommandResult("feedback", ViewRequest.applicant(BENSON))));

        // equal applicants -> returns true
        assertTrue(viewResult.equals(new CommandResult("feedback",
                ViewRequest.applicant(new ApplicantBuilder(ALICE).build()))));
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
        CommandResult viewResult = new CommandResult("feedback", ViewRequest.applicant(ALICE));
        assertEquals(viewResult.hashCode(), new CommandResult("feedback",
                ViewRequest.applicant(new ApplicantBuilder(ALICE).build())).hashCode());

        // different applicant selections -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), viewResult.hashCode());
        assertNotEquals(viewResult.hashCode(),
                new CommandResult("feedback", ViewRequest.applicant(BENSON)).hashCode());
    }

    @Test
    public void deletedApplicant_preservesSnapshotAndParticipatesInEquality() {
        Application application = new Application(ALICE, new Job("Engineer"), Application.Status.ACCEPT);
        CommandResult result = CommandResult.deletedApplicant("deleted", ALICE, application);
        CommandResult copy = CommandResult.deletedApplicant("deleted", ALICE, application);

        assertEquals(result, copy);
        assertEquals(result.hashCode(), copy.hashCode());
        assertEquals(application, result.getDeletedApplicant().orElseThrow().application());
        assertTrue(result.getViewRequest().isEmpty());
        assertNotEquals(result, new CommandResult("deleted"));
        assertNotEquals(result, CommandResult.deletedApplicant("deleted", ALICE, null));
        assertNotEquals(result, CommandResult.deletedApplicant("deleted", BENSON, null));
        assertThrows(NullPointerException.class, () -> CommandResult.deletedApplicant("deleted", null, null));
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback");
        String expected = CommandResult.class.getCanonicalName() + "{feedbackToUser="
                + commandResult.getFeedbackToUser() + ", showHelp=" + commandResult.isShowHelp()
                + ", exit=" + commandResult.isExit()
                + ", viewRequest=Optional.empty, deletedApplicant=null}";
        assertEquals(expected, commandResult.toString());
    }
}
