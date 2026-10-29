package seedu.address.testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.model.AddressBook;
import seedu.address.model.application.Job;

/**
 * A utility class containing a list of {@code Jobs} objects to be used in tests.
 */
public class TypicalJobs {

    public static final Job JANITOR = new Job("Janitor");
    public static final Job ENGINEER = new Job("Engineer", "Fixes things");

    private TypicalJobs() {} // prevents instantiation

    /**
     * Adds the jobs to an {@code AddressBook} with all the typical jobs.
     */
    public static AddressBook addJobsToAddressBook(AddressBook ab) {
        for (Job job : getTypicalJobs()) {
            ab.addJob(job);
        }
        return ab;
    }

    public static List<Job> getTypicalJobs() {
        return new ArrayList<>(Arrays.asList(JANITOR, ENGINEER));
    }
}
