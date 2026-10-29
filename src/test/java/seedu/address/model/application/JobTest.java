package seedu.address.model.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class JobTest {

    @Test
    public void constructor_validRole_success() {
        Job job = new Job("Janitor");

        assertEquals("Janitor", job.toString());
        assertEquals("Janitor", job.toString());
    }

    @Test
    public void constructor_roleWithWhitespace_trimsDescription() {
        Job job = new Job("  Janitor  ");

        assertEquals("Janitor", job.toString());
    }

    @Test
    public void constructor_invalidRole_throwsException() {
        assertThrows(NullPointerException.class, () -> new Job(null));
        assertThrows(IllegalArgumentException.class, () -> new Job("   "));
    }

    @Test
    public void equals_sameRole_returnsTrue() {
        Job first = new Job("Janitor");
        Job same = new Job("  Janitor  ");

        assertEquals(first, same);
        assertEquals(first.hashCode(), same.hashCode());
    }

    @Test
    public void equals_differentRole_returnsFalse() {
        Job first = new Job("Janitor");
        Job different = new Job("Engineer");

        assertNotEquals(first, different);
        assertNotEquals(null, first);
    }

    @Test
    public void isValidRole_validRole_returnsTrue() {
        assertTrue(Job.isValidRole("Janitor"));
        assertTrue(Job.isValidRole("ClaudeGPT Expert"));
    }

    @Test
    public void isValidRole_invalidRole_returnsFalse() {
        assertFalse(Job.isValidRole(null));
        assertFalse(Job.isValidRole("   "));
    }
}
