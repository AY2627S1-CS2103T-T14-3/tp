package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;
import seedu.address.model.applicant.Applicant;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_APPLICANT = "Applicants list contains duplicate applicant(s).";
    public static final String MESSAGE_DUPLICATE_APPLICATION =
            "Applications list contains duplicate application(s).";
    public static final String MESSAGE_DUPLICATE_JOB = "Jobs list contains duplicate job(s).";

    private final List<JsonAdaptedApplicant> applicants = new ArrayList<>();
    private final List<JsonAdaptedApplication> applications = new ArrayList<>();
    private final List<String> jobs = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given applicants, applications, and jobs.
     * {@code applications} may be null when loading a save file created before applications were stored.
     * {@code jobs} may be null when loading a save file created before jobs were stored.
     * @param applicants applicants to store
     * @param applications applications to store, or null when the save file has none
     * @param jobs job descriptions to store, or null when the save file has none
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("applicants") List<JsonAdaptedApplicant> applicants,
            @JsonProperty("applications") List<JsonAdaptedApplication> applications,
            @JsonProperty("jobs") List<String> jobs) {
        this.applicants.addAll(applicants);
        if (applications != null) {
            this.applications.addAll(applications);
        }
        if (jobs != null) {
            this.jobs.addAll(jobs);
        }
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        applicants.addAll(source.getApplicantList().stream()
                .map(JsonAdaptedApplicant::new).collect(Collectors.toList()));
        applications.addAll(source.getApplicationList().stream()
                .map(JsonAdaptedApplication::new).collect(Collectors.toList()));
        jobs.addAll(source.getJobList().stream().map(Job::toString).collect(Collectors.toList()));
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        for (JsonAdaptedApplicant jsonAdaptedApplicant : applicants) {
            Applicant applicant = jsonAdaptedApplicant.toModelType();
            if (addressBook.hasApplicant(applicant)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_APPLICANT);
            }
            addressBook.addApplicant(applicant);
        }
        for (JsonAdaptedApplication jsonAdaptedApplication : applications) {
            Application application = jsonAdaptedApplication.toModelType(addressBook);
            if (addressBook.hasApplication(application.getApplicant())) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_APPLICATION);
            }
            addressBook.addApplication(application);
        }
        for (String description : jobs) {
            if (!Job.isValidDescription(description)) {
                throw new IllegalValueException(Job.MESSAGE_CONSTRAINTS);
            }
            Job job = new Job(description);
            if (addressBook.hasJob(job)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_JOB);
            }
            addressBook.addJob(job);
        }
        return addressBook;
    }

}
