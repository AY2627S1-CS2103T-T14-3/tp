package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.application.Job;

/**
 * Jackson-friendly version of {@link Job}.
 */
public class JsonAdaptedJob {

    public static final String MISSING_ROLE_MESSAGE_FORMAT = "Job's role field is missing!";
    public static final String MISSING_DESC_MESSAGE_FORMAT = "Job's description field is missing!";

    private final String role;
    private final String description;

    /**
     * Constructs a {@code JsonAdaptedJob} with the given application details.
     * @param role name of the job
     * @param description description of the job
     */
    @JsonCreator
    public JsonAdaptedJob(@JsonProperty("role") String role,
                          @JsonProperty("description") String description) {
        this.role = role;
        this.description = description;
    }

    /**
     * Converts a given {@code Job} into this class for Jackson use.
     * @param source Job to convert
     */
    public JsonAdaptedJob(Job source) {
        role = source.getJobRole();
        description = source.getJobDescription();
    }

    /**
     * Converts this Jackson-friendly adapted application object into the model's {@code Job} object.
     * @param addressBook address book that already contains the applicant
     * @return the model's {@code Job} object
     * @throws IllegalValueException if there were any data constraints violated in the adapted job.
     */
    public Job toModelType(AddressBook addressBook) throws IllegalValueException {
        if (!Job.isValidRole(role)) {
            throw new IllegalValueException(MISSING_ROLE_MESSAGE_FORMAT);
        }
        if (!Job.isValidDescription(description)) {
            throw new IllegalValueException(MISSING_DESC_MESSAGE_FORMAT);
        }

        return new Job(role, description);
    }
}
