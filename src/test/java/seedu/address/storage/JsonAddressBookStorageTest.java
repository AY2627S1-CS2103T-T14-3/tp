package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.HOON;
import static seedu.address.testutil.TypicalApplicants.IDA;
import static seedu.address.testutil.TypicalApplicants.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.testutil.ApplicantBuilder;

public class JsonAddressBookStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonAddressBookStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readAddressBook(null));
    }

    private java.util.Optional<ReadOnlyAddressBook> readAddressBook(String filePath) throws Exception {
        return new JsonAddressBookStorage(Paths.get(filePath)).readAddressBook(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readAddressBook("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("notJsonFormatAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidApplicantAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("invalidApplicantAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidAndValidApplicantAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("invalidAndValidApplicantAddressBook.json"));
    }

    @Test
    public void readAddressBook_nullRoot_throwsDataLoadingException() throws Exception {
        Path filePath = testFolder.resolve("NullAddressBook.json");
        Files.writeString(filePath, "null");

        assertThrows(DataLoadingException.class, () -> new JsonAddressBookStorage(filePath).readAddressBook());
        assertEquals("null", Files.readString(filePath));
    }

    @Test
    public void readAddressBook_nullRecords_throwsDataLoadingException() throws Exception {
        Path filePath = testFolder.resolve("NullRecords.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);
        for (String field : List.of("applicants", "jobs", "applications")) {
            String json = "{\"applicants\":[],\"jobs\":[],\"applications\":[]}"
                    .replace("\"" + field + "\":[]", "\"" + field + "\":[null]");
            Files.writeString(filePath, json);

            assertThrows(DataLoadingException.class, storage::readAddressBook);
            assertEquals(json, Files.readString(filePath));
        }
    }

    @Test
    public void readAddressBook_trailingJson_throwsDataLoadingException() throws Exception {
        Path filePath = testFolder.resolve("TrailingAddressBook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);
        storage.saveAddressBook(getTypicalAddressBook());
        String saved = Files.readString(filePath);
        String withTrailingObject = saved + saved;
        Files.writeString(filePath, withTrailingObject);

        assertThrows(DataLoadingException.class, () -> storage.readAddressBook());
        assertEquals(withTrailingObject, Files.readString(filePath));
    }

    @Test
    public void readAndSaveAddressBook_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempAddressBook.json");
        AddressBook original = getTypicalAddressBook();
        JsonAddressBookStorage jsonAddressBookStorage = new JsonAddressBookStorage(filePath);

        // Save in new file and read back
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        ReadOnlyAddressBook readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Modify data, overwrite existing file, and read back
        original.addApplicant(HOON);
        original.removeApplicant(ALICE);
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Save and read without specifying file path
        original.addApplicant(IDA);
        jsonAddressBookStorage.saveAddressBook(original); // file path not specified
        readBack = jsonAddressBookStorage.readAddressBook().get(); // file path not specified
        assertEquals(original, new AddressBook(readBack));

    }

    @Test
    public void readAndSaveAddressBook_commonPunctuationAndUnicodeName_preservesName() throws Exception {
        JsonAddressBookStorage storage = new JsonAddressBookStorage(testFolder.resolve("names.json"));
        for (String name : List.of("Mary-Jane Tan", "Shaun O'Brien", "Rajesh s/o Kumar", "J. R. Tan",
                "José Lim", "Jose\u0301 Lim", "Shaun O’Brien", "陈伟")) {
            AddressBook original = new AddressBook();
            original.addApplicant(new ApplicantBuilder().withName(name).build());
            storage.saveAddressBook(original);
            assertEquals(original, new AddressBook(storage.readAddressBook().orElseThrow()));
        }
    }

    @Test
    public void readAndSaveAddressBook_internationalPhone_preservesFormatting() throws Exception {
        JsonAddressBookStorage storage = new JsonAddressBookStorage(testFolder.resolve("phones.json"));
        for (String phone : List.of("+65 9123 4567", "+1-202-555-0123", "9123-4567")) {
            AddressBook original = new AddressBook();
            original.addApplicant(new ApplicantBuilder().withPhone(phone).build());
            storage.saveAddressBook(original);
            assertEquals(original, new AddressBook(storage.readAddressBook().orElseThrow()));
        }
    }

    @Test
    public void saveAddressBook_nullAddressBook_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(null, "SomeFile.json"));
    }

    /**
     * Saves {@code addressBook} at the specified {@code filePath}.
     */
    private void saveAddressBook(ReadOnlyAddressBook addressBook, String filePath) {
        try {
            new JsonAddressBookStorage(Paths.get(filePath))
                    .saveAddressBook(addressBook, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(new AddressBook(), null));
    }
}
