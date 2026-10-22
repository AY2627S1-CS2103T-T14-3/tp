package seedu.address.model.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class JobTest {

    @Test
    public void constructor_validDescription_success() {
        Job job = new Job("Janitor");

        assertEquals("Janitor", job.toString());
        assertEquals("Janitor", job.toString());
    }

    @Test
    public void constructor_descriptionWithWhitespace_trimsDescription() {
        Job job = new Job("  Janitor  ");

        assertEquals("Janitor", job.toString());
    }

    @Test
    public void constructor_invalidDescription_throwsException() {
        assertThrows(NullPointerException.class, () -> new Job(null));
        assertThrows(IllegalArgumentException.class, () -> new Job("   "));
    }

    @Test
    public void constructor_optionLikeDescription_throwsException() {
        List<String> invalidDescriptions = List.of(
                "-s accept",
                "Janitor -s",
                "Janitor -status accept",
                "Janitor\t-s\taccept",
                "Janitor\n-s\naccept");

        invalidDescriptions.forEach(description ->
                assertThrows(IllegalArgumentException.class, () -> new Job(description)));
    }

    @Test
    public void equals_sameDescriptions_returnsTrue() {
        Job first = new Job("Janitor");
        Job same = new Job("  Janitor  ");

        assertEquals(first, same);
        assertEquals(first.hashCode(), same.hashCode());
    }

    @Test
    public void equals_differentDescriptions_returnsFalse() {
        Job first = new Job("Janitor");
        Job different = new Job("Engineer");

        assertNotEquals(first, different);
        assertNotEquals(null, first);
    }

    @Test
    public void isValidDescription_validDescriptions_returnsTrue() {
        assertTrue(Job.isValidDescription("Janitor"));
        assertTrue(Job.isValidDescription("ClaudeGPT Expert"));
    }

    @Test
    public void isValidDescription_invalidDescriptions_returnsFalse() {
        assertFalse(Job.isValidDescription(null));
        assertFalse(Job.isValidDescription("   "));
        assertFalse(Job.isValidDescription("Janitor -s accept"));
    }
}
