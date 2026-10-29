package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;
import seedu.address.model.applicant.Applicant;

/**
 * Jackson-friendly version of {@link Application}.
 */
class JsonAdaptedApplication {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Application's %s field is missing!";
    public static final String MESSAGE_UNKNOWN_APPLICANT =
            "Application refers to an applicant that does not exist.";
    public static final String MESSAGE_INVALID_STATUS = "Invalid status. Please choose a status from "
            + "\"accept\", \"reject\", or \"pending\".";

    private final String name;
    private final String jobDescription;
    private final String status;

    /**
     * Constructs a {@code JsonAdaptedApplication} with the given application details.
     * @param name name of the applicant who owns the application
     * @param jobDescription job the applicant is applying for
     * @param status application status
     */
    @JsonCreator
    public JsonAdaptedApplication(@JsonProperty("name") String name,
            @JsonProperty("jobDescription") String jobDescription, @JsonProperty("status") String status) {
        this.name = name;
        this.jobDescription = jobDescription;
        this.status = status;
    }

    /**
     * Converts a given {@code Application} into this class for Jackson use.
     * @param source application to convert
     */
    public JsonAdaptedApplication(Application source) {
        name = source.getApplicant().getName().fullName;
        jobDescription = source.getJob().toString();
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
        if (jobDescription == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "job description"));
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

        Applicant applicant = addressBook.getApplicantList().stream()
                .filter(existingApplicant -> existingApplicant.getName().fullName.equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalValueException(MESSAGE_UNKNOWN_APPLICANT));

        final Job modelJob;
        try {
            modelJob = new Job(jobDescription);
        } catch (IllegalArgumentException exception) {
            throw new IllegalValueException(Job.MESSAGE_CONSTRAINTS);
        }

        return new Application(applicant, modelJob, modelStatus);
    }
}
