package seedu.address.model.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;

import org.junit.jupiter.api.Test;

public class ApplicationTest {

    @Test
    public void constructor_validApplication_success() {
        Application application = new Application(ALICE, "Janitor", Application.Status.PENDING);

        assertEquals(ALICE, application.getApplicant());
        assertEquals("Janitor", application.getJobDescription());
        assertEquals(Application.Status.PENDING, application.getStatus());
    }

    @Test
    public void constructor_trimsJobDescription_success() {
        Application application = new Application(ALICE, "  Janitor  ", Application.Status.ACCEPT);

        assertEquals("Janitor", application.getJobDescription());
    }

    @Test
    public void constructor_invalidValues_throwsException() {
        assertThrows(NullPointerException.class, () -> new Application(null, "Janitor", Application.Status.PENDING));
        assertThrows(NullPointerException.class, () -> new Application(ALICE, null, Application.Status.PENDING));
        assertThrows(NullPointerException.class, () -> new Application(ALICE, "Janitor", null));
        assertThrows(IllegalArgumentException.class, ()
                -> new Application(ALICE, "   ", Application.Status.PENDING));
        assertThrows(IllegalArgumentException.class, ()
                -> new Application(ALICE, "ClaudeGPT -s accept", Application.Status.PENDING));
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
        Application first = new Application(ALICE, "Janitor", Application.Status.PENDING);
        Application second = new Application(ALICE, "Janitor", Application.Status.PENDING);

        assertTrue(first.equals(second));
        assertEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(null));
        assertFalse(first.equals("application"));
    }
}
