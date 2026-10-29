package seedu.address.model.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.application.exceptions.DuplicateJobException;
import seedu.address.model.application.exceptions.JobNotFoundException;

/**
 * Checks that replacing jobs preserves the list's order and uniqueness guarantees.
 */
public class UniqueJobListTest {

    private static final Job ENGINEER = new Job("Software Engineer");
    private static final Job ANALYST = new Job("Data Analyst");
    private static final Job DESIGNER = new Job("Product Designer");

    private final UniqueJobList jobs = new UniqueJobList();

    @Test
    public void setJob_existingTarget_replacesAtSamePosition() {
        jobs.setJobs(List.of(ENGINEER, ANALYST));
        jobs.setJob(ENGINEER, DESIGNER);
        assertEquals(List.of(DESIGNER, ANALYST), jobs.asUnmodifiableObservableList());
    }

    @Test
    public void setJob_unchangedDescription_success() {
        jobs.add(ENGINEER);
        jobs.setJob(ENGINEER, new Job(" Software Engineer "));
        assertEquals(List.of(ENGINEER), jobs.asUnmodifiableObservableList());
    }

    @Test
    public void setJob_duplicateDescription_throwsDuplicateJobException() {
        jobs.setJobs(List.of(ENGINEER, ANALYST));
        assertThrows(DuplicateJobException.class, () -> jobs.setJob(ENGINEER, ANALYST));
        assertEquals(List.of(ENGINEER, ANALYST), jobs.asUnmodifiableObservableList());
    }

    @Test
    public void setJob_missingTarget_throwsJobNotFoundException() {
        jobs.add(ANALYST);
        assertThrows(JobNotFoundException.class, () -> jobs.setJob(ENGINEER, DESIGNER));
        assertEquals(List.of(ANALYST), jobs.asUnmodifiableObservableList());
    }

    @Test
    public void setJob_nullArguments_throwsNullPointerException() {
        jobs.add(ENGINEER);
        assertThrows(NullPointerException.class, () -> jobs.setJob(null, DESIGNER));
        assertThrows(NullPointerException.class, () -> jobs.setJob(ENGINEER, null));
        assertEquals(List.of(ENGINEER), jobs.asUnmodifiableObservableList());
    }
}
