package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;
import seedu.address.model.person.Person;

/**
 * Jackson-friendly version of {@link Application}.
 */
class JsonAdaptedApplication {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Application's %s field is missing!";
    public static final String MESSAGE_UNKNOWN_APPLICANT =
            "Application refers to an applicant that does not exist.";
    public static final String MESSAGE_UNKNOWN_JOB =
            "Application refers to a job that does not exist.";
    public static final String MESSAGE_INVALID_STATUS = "Invalid status. Please choose a status from "
            + "\"accept\", \"reject\", or \"pending\".";

    private final String name;
    private final JsonAdaptedJob job;
    private final String status;

    /**
     * Constructs a {@code JsonAdaptedApplication} with the given application details.
     * @param name name of the applicant who owns the application
     * @param job job the applicant is applying for
     * @param status application status
     */
    @JsonCreator
    public JsonAdaptedApplication(@JsonProperty("name") String name,
            @JsonProperty("job") JsonAdaptedJob job, @JsonProperty("status") String status) {
        this.name = name;
        this.job = job;
        this.status = status;
    }

    /**
     * Converts a given {@code Application} into this class for Jackson use.
     * @param source application to convert
     */
    public JsonAdaptedApplication(Application source) {
        name = source.getApplicant().getName().fullName;
        job = new JsonAdaptedJob(source.getJob());
        status = source.getStatus().getValue();
    }

    /**
     * Converts this Jackson-friendly adapted application object into the model's {@code Application} object.
     * @param addressBook address book that already contains the applicant
     * @return the model's {@code Application} object
     * @throws IllegalValueException if there were any data constraints violated in the adapted application.
     */
    public Application toModelType(AddressBook addressBook) throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "name"));
        }
        if (job == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "job"));
        }
        if (status == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "status"));
        }

        final Application.Status modelStatus;
        try {
            modelStatus = Application.Status.fromValue(status);
        } catch (IllegalArgumentException exception) {
            throw new IllegalValueException(MESSAGE_INVALID_STATUS);
        }

        Person applicant = addressBook.getPersonList().stream()
                .filter(person -> person.getName().fullName.equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalValueException(MESSAGE_UNKNOWN_APPLICANT));

        Job applicationJob = this.job.toModelType(addressBook);
        Job job = addressBook.getJobList().stream()
                .filter(j -> j.equals(applicationJob))
                .findFirst()
                .orElseThrow(() -> new IllegalValueException(MESSAGE_UNKNOWN_JOB));

        return new Application(applicant, job, modelStatus);
    }
}
