package seedu.address.model.util;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.application.Job;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.Email;
import seedu.address.model.applicant.Name;
import seedu.address.model.applicant.Phone;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    public static Applicant[] getSampleApplicants() {
        return new Applicant[] {
            new Applicant(new Name("Alex Yeoh"), new Phone("87438807"), new Email("alexyeoh@example.com")),
            new Applicant(new Name("Bernice Yu"), new Phone("99272758"), new Email("berniceyu@example.com")),
            new Applicant(new Name("Charlotte Oliveiro"), new Phone("93210283"), new Email("charlotte@example.com")),
            new Applicant(new Name("David Li"), new Phone("91031282"), new Email("lidavid@example.com")),
            new Applicant(new Name("Irfan Ibrahim"), new Phone("92492021"), new Email("irfan@example.com")),
            new Applicant(new Name("Roy Balakrishnan"), new Phone("92624417"), new Email("royb@example.com"))
        };
    }

    public static Job[] getSampleJobs() {
        return new Job[] {
            new Job("Software Engineer Intern"),
            new Job("Data Analyst"),
            new Job("Product Designer")
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Applicant sampleApplicant : getSampleApplicants()) {
            sampleAb.addApplicant(sampleApplicant);
        }
        for (Job sampleJob : getSampleJobs()) {
            sampleAb.addJob(sampleJob);
        }
        return sampleAb;
    }

}
