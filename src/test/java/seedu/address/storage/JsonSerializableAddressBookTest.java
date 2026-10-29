package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalJobs.JANITOR;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.application.Application;
import seedu.address.model.application.Job;
import seedu.address.testutil.TypicalPersons;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");
    private static final Path INVALID_APPLICATION_FILE =
            TEST_DATA_FOLDER.resolve("invalidApplicationAddressBook.json");
    private static final Path DUPLICATE_APPLICATION_FILE =
            TEST_DATA_FOLDER.resolve("duplicateApplicationAddressBook.json");
    private static final Path MISSING_APPLICANT_FILE = TEST_DATA_FOLDER.resolve("missingApplicantAddressBook.json");

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_applicationRoundTrip_success() throws Exception {
        AddressBook original = new AddressBook();
        original.addPerson(ALICE);
        original.addJob(JANITOR);
        original.addApplication(new Application(ALICE, JANITOR, Application.Status.ACCEPT));

        AddressBook loaded = new JsonSerializableAddressBook(original).toModelType();

        assertEquals(original, loaded);
        assertEquals(JANITOR, loaded.getApplication(ALICE).orElseThrow().getJob());
        assertEquals(Application.Status.ACCEPT, loaded.getApplication(ALICE).orElseThrow().getStatus());
    }

    @Test
    public void toModelType_jobsRoundTrip_success() throws Exception {
        AddressBook original = new AddressBook();
        original.addJob(new Job("Software Engineer"));
        original.addJob(new Job("Data Analyst"));

        AddressBook loaded = new JsonSerializableAddressBook(original).toModelType();

        assertEquals(original, loaded);
        assertEquals(List.of(new Job("Software Engineer"), new Job("Data Analyst")), loaded.getJobList());
    }

    @Test
    public void toModelType_duplicateJobs_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(
                TEST_DATA_FOLDER.resolve("duplicateJobAddressBook.json"),
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_JOB,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_invalidJob_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(
                TEST_DATA_FOLDER.resolve("invalidJobAddressBook.json"),
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonAdaptedJob.MISSING_ROLE_MESSAGE_FORMAT,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_invalidApplication_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_APPLICATION_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonAdaptedApplication.MESSAGE_INVALID_STATUS,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateApplications_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_APPLICATION_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_APPLICATION,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_missingApplicant_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(MISSING_APPLICANT_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonAdaptedApplication.MESSAGE_UNKNOWN_APPLICANT,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_sameEmailDifferentNames_throwsIllegalValueException() {
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(List.of(
                new JsonAdaptedPerson("Alice", "91234567", "shared@example.com"),
                new JsonAdaptedPerson("Bob", "98765432", "shared@example.com")), List.of(), List.of());

        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                data::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

}
