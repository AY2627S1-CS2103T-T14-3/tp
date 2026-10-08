package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.ObjectMapper;

import seedu.address.commons.util.JsonUtil;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

/**
 * Exercises remark commands through parsing, model updates and JSON storage.
 */
public class RemarkIntegrationTest {
    @TempDir
    public Path testFolder;

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void execute_addReplaceAndClearRemark_preservesOtherFields() throws Exception {
        Person original = model.getFilteredPersonList().get(0);
        CommandResult result = parser.parseCommand("remark 1 r/Likes baseball").execute(model);
        Person updated = model.getFilteredPersonList().get(0);
        assertEquals("Likes baseball", storedRemark(updated));
        assertNotEquals(original, updated);
        assertTrue(original.isSamePerson(updated));
        assertEquals(original.getName(), updated.getName());
        assertEquals(original.getPhone(), updated.getPhone());
        assertEquals(original.getEmail(), updated.getEmail());
        assertEquals(original.getAddress(), updated.getAddress());
        assertEquals(original.getTags(), updated.getTags());
        assertTrue(result.getFeedbackToUser().startsWith("Added remark to Person:"));

        parser.parseCommand("remark 1 r/Prefers email").execute(model);
        assertEquals("Prefers email", storedRemark(model.getFilteredPersonList().get(0)));
        result = parser.parseCommand("remark 1 r/").execute(model);
        assertEquals("", storedRemark(model.getFilteredPersonList().get(0)));
        assertTrue(result.getFeedbackToUser().startsWith("Removed remark from Person:"));
    }

    @Test
    public void execute_filteredList_updatesDisplayedPerson() throws Exception {
        Person target = model.getFilteredPersonList().get(1);
        model.updateFilteredPersonList(person -> person.equals(target));
        parser.parseCommand("remark 1 r/Filtered person").execute(model);
        assertEquals(target.getName(), model.getAddressBook().getPersonList().get(1).getName());
        assertEquals("Filtered person", storedRemark(model.getAddressBook().getPersonList().get(1)));
        assertEquals("", storedRemark(model.getAddressBook().getPersonList().get(0)));
        assertEquals(model.getAddressBook().getPersonList().size(), model.getFilteredPersonList().size());
    }

    @Test
    public void execute_invalidDisplayedIndex_rejectsWithoutChangingModel() throws Exception {
        model.updateFilteredPersonList(person -> person.equals(model.getAddressBook().getPersonList().get(0)));
        String original = JsonUtil.toJsonString(new JsonSerializableAddressBook(model.getAddressBook()));
        CommandException exception = assertThrows(
                CommandException.class, () -> parser.parseCommand("remark 2 r/Outside filtered list").execute(model));
        assertEquals(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, exception.getMessage());
        assertEquals(original, JsonUtil.toJsonString(new JsonSerializableAddressBook(model.getAddressBook())));
    }

    @Test
    public void parse_invalidIndex_rejectsInput() {
        for (String input : new String[]{"remark", "remark 0 r/Note", "remark -1 r/Note", "remark abc r/Note"}) {
            assertThrows(ParseException.class, () -> parser.parseCommand(input));
        }
    }

    @Test
    public void execute_missingRemarkPrefix_clearsRemarkAsInTutorial() throws Exception {
        parser.parseCommand("remark 1 r/Note").execute(model);
        parser.parseCommand("remark 1").execute(model);
        assertEquals("", storedRemark(model.getFilteredPersonList().get(0)));
    }

    @Test
    public void saveAndReload_remarkSurvivesEditingAndStorage() throws Exception {
        parser.parseCommand("remark 1 r/Call after 5 pm").execute(model);
        parser.parseCommand("edit 1 p/91234567").execute(model);
        assertEquals("Call after 5 pm", storedRemark(model.getFilteredPersonList().get(0)));
        JsonAddressBookStorage storage = new JsonAddressBookStorage(testFolder.resolve("addressbook.json"));
        storage.saveAddressBook(model.getAddressBook());
        Person reloaded = storage.readAddressBook().orElseThrow().getPersonList().get(0);
        assertEquals("Call after 5 pm", storedRemark(reloaded));
        assertEquals(model.getAddressBook().getPersonList().get(0), reloaded);
    }

    @Test
    public void read_legacyPersonWithoutRemark_defaultsToEmpty() throws Exception {
        String json = JsonUtil.toJsonString(new JsonAdaptedPerson(model.getFilteredPersonList().get(0)));
        com.fasterxml.jackson.databind.node.ObjectNode node =
                (com.fasterxml.jackson.databind.node.ObjectNode) new ObjectMapper().readTree(json);
        node.remove("remark");
        Person restored = JsonUtil.fromJsonString(node.toString(), JsonAdaptedPerson.class).toModelType();
        assertEquals("", storedRemark(restored));
    }

    private String storedRemark(Person person) throws Exception {
        return new ObjectMapper().readTree(JsonUtil.toJsonString(new JsonAdaptedPerson(person)))
                .path("remark").asText("MISSING");
    }
}
